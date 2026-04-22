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

    private static String getFieldType(JavaFieldDefinition fieldDefinition) {
        var fieldType = fieldDefinition.type();
        if (fieldDefinition.property().optional() && fieldType.decoratedDefaultValue() == null && !fieldType.collection()) {
            return "Optional<%s>".formatted(fieldType.fullName());
        }
        return toPrimitiveType(fieldType.fullName())
                .map(Class::getName)
                .orElse(fieldType.fullName());
    }

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

    private void addFactoryMethod(RecordDeclaration recordDeclaration) {

        var factoryArgs = new ArrayList<Parameter>();
        var checks = new ArrayList<String>();
        var constructorArgs = new ArrayList<String>();

        for (JavaFieldDefinition field : recordDefinition.fields()) {

            if (field.property().readOnly()) {
                constructorArgs.add("null");
                continue;
            }

            var param = new Parameter(parseType(field.type().fullName()), field.name());
            field
                    .type()
                    .propertyAnnotators()
                    .forEach(annotator -> annotator.annotateFactoryArgument(param, field.property()));
            factoryArgs.add(param);

            if (field.type().collection()) {
                constructorArgs.add(field.name());
            } else if (!field.property().optional()) {
                checks.add("if (%s == null) { throw new MissingPropertyException(\"%s\"); }\n".formatted(
                        field.name(), field.property().name()));
                constructorArgs.add(field.name());
            } else if (field.type().decoratedDefaultValue() != null) {
                constructorArgs.add("Objects.requireNonNullElse(%s, %s)".formatted(field.name(), field.type().decoratedDefaultValue()));
            } else {
                constructorArgs.add("Optional.ofNullable(%s)".formatted(field.name()));
            }

        }

        var body = """
                {
                    %s
                    return new %s(%s);
                }
                """.formatted(
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
