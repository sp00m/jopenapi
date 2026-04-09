package com.github.sp00m.jopenapi.generate;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.Modifier;
import com.github.javaparser.ast.NodeList;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.CompactConstructorDeclaration;
import com.github.javaparser.ast.body.Parameter;
import com.github.javaparser.ast.body.RecordDeclaration;
import com.github.javaparser.ast.body.TypeDeclaration;
import com.github.sp00m.jopenapi.read.vo.JavaClassDefinition;
import com.github.sp00m.jopenapi.read.vo.JavaFieldDefinition;
import com.github.sp00m.jopenapi.read.vo.JavaTypeDefinition;
import lombok.Builder;
import lombok.RequiredArgsConstructor;
import lombok.extern.jackson.Jacksonized;
import org.apache.commons.lang3.ClassUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static com.github.javaparser.StaticJavaParser.parseBlock;
import static com.github.javaparser.StaticJavaParser.parseType;
import static com.github.javaparser.ast.Modifier.Keyword.STATIC;

@RequiredArgsConstructor
final class JavaClassGenerator implements JavaTypeGenerator {

    private final JavaClassDefinition classDefinition;
    private final CompilationUnit compiler = new CompilationUnit();

    @Override
    public CompilationUnit generate() {
        var recordDeclaration = new RecordDeclaration(
                NodeList.nodeList(Modifier.publicModifier()),
                classDefinition.name()
        );
        compiler.addType(recordDeclaration);

        recordDeclaration.addAnnotation(Jacksonized.class);
        recordDeclaration.addAndGetAnnotation(Builder.class).addPair("toBuilder", "true");

        classDefinition
                .implementedTypes()
                .forEach(recordDeclaration::addImplementedType);

        List<String> compactConstructorStatements = new ArrayList<>();

        for (var field : classDefinition.fields()) {
            var fieldType = field.type();

            // Add inner type definition as member if present
            Optional
                    .ofNullable(fieldType.getDefinition())
                    .ifPresent(typeDefinition -> addMember(compiler, recordDeclaration, typeDefinition));

            // Determine record parameter type
            String paramType = getRecordParamType(field);

            // Create parameter and add to record
            var param = new Parameter(parseType(paramType), field.name());
            recordDeclaration.getParameters().add(param);

            // Add annotations from field annotators
            fieldType
                    .getFieldAnnotators()
                    .forEach(annotator -> annotator.annotate(param, field.property()));

            // Build compact constructor statement if needed
            var statement = getCompactConstructorStatement(field);
            if (statement != null) {
                compactConstructorStatements.add(statement);
            }
        }

        if (!compactConstructorStatements.isEmpty()) {
            addCompactConstructor(recordDeclaration, compactConstructorStatements);
        }

        return compiler;
    }

    private static String getRecordParamType(JavaFieldDefinition fieldDefinition) {
        var fieldType = fieldDefinition.type();

        if (fieldType.isWrapped()) {
            // Collection types: always use the collection type directly
            return fieldType.getFullName();
        }

        if (!fieldDefinition.property().optional()) {
            // Required: use primitive if possible
            return toPrimitiveType(fieldType.getFullName())
                    .map(Class::getName)
                    .orElse(fieldType.getFullName());
        }

        if (fieldType.getDefaultValue() != null) {
            // Optional with default: use the type, default in compact constructor
            return fieldType.getFullName();
        }

        // Optional without default: wrap in Optional
        return "Optional<%s>".formatted(fieldType.getFullName());
    }

    private static String getCompactConstructorStatement(JavaFieldDefinition fieldDefinition) {
        var fieldType = fieldDefinition.type();
        var fieldName = fieldDefinition.name();

        if (fieldType.isWrapped()) {
            // Collection: default null to empty, wrap in unmodifiable
            var emptyValue = fieldType.getDefaultValue();
            var unmodifiable = getUnmodifiableWrapper(fieldType.getFullName());
            return "%s = %s == null ? %s : %s(%s);".formatted(
                    fieldName, fieldName, emptyValue, unmodifiable, fieldName);
        }

        if (!fieldDefinition.property().optional()) {
            // Required non-collection: no null handling
            return null;
        }

        if (fieldType.getDefaultValue() != null) {
            // Optional with default
            return "%s = %s == null ? %s : %s;".formatted(
                    fieldName, fieldName, fieldType.getDefaultValue(), fieldName);
        }

        // Optional without default: wrap in Optional
        return "%s = %s == null ? Optional.empty() : %s;".formatted(
                fieldName, fieldName, fieldName);
    }

    private void addCompactConstructor(RecordDeclaration recordDeclaration, List<String> statements) {
        if (statements.stream().anyMatch(s -> s.contains("Optional.empty()"))) {
            compiler.addImport(Optional.class);
        }
        if (statements.stream().anyMatch(s -> s.contains("Collections.unmodifiable"))) {
            compiler.addImport(Collections.class);
        }
        var compactConstructor = new CompactConstructorDeclaration(
                NodeList.nodeList(Modifier.publicModifier()),
                classDefinition.name()
        );
        var body = "{\n" + String.join("\n", statements) + "\n}";
        compactConstructor.setBody(parseBlock(body));
        recordDeclaration.addMember(compactConstructor);
    }

    static String getUnmodifiableWrapper(String fullName) {
        if (fullName.startsWith("java.util.List<")) return "java.util.Collections.unmodifiableList";
        if (fullName.startsWith("java.util.Set<")) return "java.util.Collections.unmodifiableSet";
        if (fullName.startsWith("java.util.Map<")) return "java.util.Collections.unmodifiableMap";
        throw new IllegalStateException("Unknown collection type: " + fullName);
    }

    static void addMember(CompilationUnit compiler, TypeDeclaration<?> parentType, JavaTypeDefinition innerTypeDefinition) {
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
