package com.github.sp00m.jopenapi.generate;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.NodeList;
import com.github.javaparser.ast.body.*;
import com.github.sp00m.jopenapi.read.vo.JavaFieldDefinition;
import com.github.sp00m.jopenapi.read.vo.JavaRecordDefinition;
import com.github.sp00m.jopenapi.read.vo.JavaTypeDefinition;
import joptsimple.internal.Strings;
import lombok.Builder;
import lombok.RequiredArgsConstructor;
import lombok.With;
import org.apache.commons.lang3.ClassUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import static com.github.javaparser.StaticJavaParser.parseBlock;
import static com.github.javaparser.StaticJavaParser.parseType;
import static com.github.javaparser.ast.Modifier.Keyword.PUBLIC;
import static com.github.javaparser.ast.Modifier.Keyword.STATIC;

/**
 * Generates Java record declarations with:
 * <ul>
 *   <li>{@code @Builder(toBuilder = true)} and {@code @With} — Lombok annotations that
 *       will be expanded by the delombok pass in the write phase.</li>
 *   <li>A compact constructor that ensures collection immutability and Optional non-nullness.</li>
 *   <li>A package-private {@code @JsonCreator} static factory method ({@code create}) that
 *       handles null-checking, defaults, and Optional wrapping. It is intentionally not public
 *       so developers can't call it directly — only Jackson uses it via reflection.</li>
 * </ul>
 *
 * <p>The factory accepts <em>boxed</em> types for all parameters (e.g. {@code Integer} instead
 * of {@code int}) so Jackson can pass {@code null} to indicate a missing value. The factory
 * then decides whether to throw, apply a default, or wrap in Optional.
 */
@RequiredArgsConstructor
final class JavaRecordGenerator implements JavaTypeGenerator {

    private final JavaRecordDefinition recordDefinition;
    private final CompilationUnit compiler = CompilationUnitFactory.create();

    @Override
    public CompilationUnit generate() {

        var recordDeclaration = new RecordDeclaration()
                .setName(recordDefinition.name())
                .addModifier(PUBLIC);
        compiler.addType(recordDeclaration);

        recordDeclaration.addAnnotation(With.class);
        recordDeclaration.addAndGetAnnotation(Builder.class).addPair("toBuilder", "true");

        recordDefinition
                .implementedTypes()
                .forEach(recordDeclaration::addImplementedType);

        var compactConstructorStatements = recordDefinition
                .fields()
                .stream()
                .map(field -> addField(compiler, recordDeclaration, field))
                .filter(Objects::nonNull)
                .toList();

        if (!compactConstructorStatements.isEmpty()) {
            addCompactConstructor(recordDeclaration, compactConstructorStatements);
        }

        addFactoryMethod(recordDeclaration);

        return compiler;
    }

    private static String addField(CompilationUnit compiler, RecordDeclaration recordDeclaration, JavaFieldDefinition field) {
        var fieldType = field.type();
        Optional
                .ofNullable(fieldType.definition())
                .ifPresent(typeDefinition -> addMember(compiler, recordDeclaration, typeDefinition));
        var param = new Parameter(parseType(getFieldType(field)), field.name());
        recordDeclaration.getParameters().add(param);
        fieldType
                .propertyAnnotators()
                .forEach(annotator -> annotator.annotateRecordField(param, field.property()));
        return getCompactConstructorStatement(field);
    }

    /**
     * Determines the record component type for a field. Three cases:
     * <ol>
     *   <li>Optional without default → {@code Optional<T>}</li>
     *   <li>Has a primitive equivalent (Integer→int, etc.) → use primitive</li>
     *   <li>Otherwise → use the type as-is</li>
     * </ol>
     */
    private static String getFieldType(JavaFieldDefinition fieldDefinition) {
        var fieldType = fieldDefinition.type();
        if (fieldDefinition.property().optional() && fieldType.decoratedDefaultValue() == null && !fieldType.collection()) {
            return "Optional<%s>".formatted(fieldType.fullName());
        }
        return toPrimitiveType(fieldType.fullName())
                .map(Class::getName)
                .orElse(fieldType.fullName());
    }

    /**
     * Returns the compact constructor statement for this field, or null if none needed.
     * Only collections and bare optionals need handling here — defaults are the factory's job.
     */
    static String getCompactConstructorStatement(JavaFieldDefinition fieldDefinition) {
        var fieldType = fieldDefinition.type();
        var fieldName = fieldDefinition.name();
        if (fieldType.collection()) {
            var emptyValue = fieldType.decoratedDefaultValue();
            var unmodifier = fieldType.unmodifier();
            return "%s = %s == null ? %s : %s;".formatted(
                    fieldName, fieldName, emptyValue, unmodifier.apply(fieldName));
        }
        if (fieldDefinition.property().optional() && fieldType.decoratedDefaultValue() == null) {
            return "%s = Objects.requireNonNullElse(%s, Optional.empty());".formatted(
                    fieldName, fieldName);
        }
        return null;
    }

    private void addCompactConstructor(RecordDeclaration recordDeclaration, List<String> statements) {
        var body = "{\n" + String.join("\n", statements) + "\n}";
        var compactConstructor = new CompactConstructorDeclaration()
                .setName(recordDefinition.name())
                .setModifiers(PUBLIC)
                .setBody(parseBlock(body));
        recordDeclaration.addMember(compactConstructor);
    }

    /**
     * Builds the {@code @JsonCreator} factory. Read-only fields are excluded from the
     * parameter list and receive {@code null} (the compact constructor will convert to
     * empty collection / Optional.empty()).
     */
    private void addFactoryMethod(RecordDeclaration recordDeclaration) {

        var factoryArgs = new ArrayList<Parameter>();
        var checks = new ArrayList<String>();
        var constructorArgs = new ArrayList<String>();

        for (JavaFieldDefinition field : recordDefinition.fields()) {

            if (field.property().readOnly()) {
                // Read-only fields aren't deserialized; pass null and let the compact constructor
                // convert to Optional.empty() or an empty collection
                constructorArgs.add("null");
                continue;
            }

            // Factory params use the boxed type (e.g. Integer not int) so Jackson can pass null for missing values
            var param = new Parameter(parseType(field.type().fullName()), field.name());
            field
                    .type()
                    .propertyAnnotators()
                    .forEach(annotator -> annotator.annotateFactoryArgument(param, field.property()));
            factoryArgs.add(param);

            if (field.type().collection()) {
                // Collections are passed through; the compact constructor handles null → empty + immutability
                constructorArgs.add(field.name());
            } else if (!field.property().optional()) {
                // Required field: null means the caller forgot it → throw
                checks.add("if (%s == null) { throw new MissingPropertyException(\"%s\"); }".formatted(
                        field.name(), field.property().name()));
                constructorArgs.add(field.name());
            } else if (field.type().decoratedDefaultValue() != null) {
                // Optional with default: fall back to the default when absent
                constructorArgs.add("Objects.requireNonNullElse(%s, %s)".formatted(field.name(), field.type().decoratedDefaultValue()));
            } else {
                // Optional without default: wrap in Optional
                constructorArgs.add("Optional.ofNullable(%s)".formatted(field.name()));
            }

        }

        var body = "{ %s return new %s(%s); }".formatted(
                Strings.join(checks, ""),
                recordDefinition.name(),
                Strings.join(constructorArgs, ", ")
        );

        recordDeclaration
                .addMethod("create", STATIC)
                .addAnnotation(JsonCreator.class)
                .setType(parseType(recordDefinition.name()))
                .setBody(parseBlock(body))
                .setParameters(new NodeList<>(factoryArgs));
    }

    /**
     * When a field's type has its own {@link JavaTypeDefinition} (e.g. an inline enum or
     * nested object), that type is generated as a static inner class of the enclosing record.
     */
    private static void addMember(CompilationUnit compiler, TypeDeclaration<?> parentType, JavaTypeDefinition innerTypeDefinition) {
        var innerTypeCompiler = JavaGenerator.generateCompiler(innerTypeDefinition);
        var innerType = innerTypeCompiler.getType(0);
        if (innerType instanceof ClassOrInterfaceDeclaration declaration) {
            if (declaration.isInterface()) {
                throw new IllegalStateException("'oneOf' not supported at property level");
            }
            declaration.addModifier(STATIC);
        }
        parentType.addMember(innerType);
        innerTypeCompiler.getImports().forEach(compiler::addImport);
    }

    static Optional<? extends Class<?>> toPrimitiveType(String wrapperType) {
        try {
            return Optional
                    .ofNullable(ClassUtils.wrapperToPrimitive(Class.forName("java.lang.%s".formatted(wrapperType))))
                    .filter(primitive -> !wrapperType.equals(primitive.getName()));
        } catch (Throwable t) {
            return Optional.empty();
        }
    }

}
