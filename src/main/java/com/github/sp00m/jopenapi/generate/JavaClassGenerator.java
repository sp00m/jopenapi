package com.github.sp00m.jopenapi.generate;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.Modifier;
import com.github.javaparser.ast.NodeList;
import com.github.javaparser.ast.body.*;
import com.github.sp00m.jopenapi.read.vo.JavaClassDefinition;
import com.github.sp00m.jopenapi.read.vo.JavaFieldDefinition;
import com.github.sp00m.jopenapi.read.vo.JavaTypeDefinition;
import lombok.Builder;
import lombok.RequiredArgsConstructor;
import lombok.extern.jackson.Jacksonized;
import org.apache.commons.lang3.ClassUtils;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
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

        var compactConstructorStatements = classDefinition
                .fields()
                .stream()
                .map(field -> addField(compiler, recordDeclaration, field))
                .filter(Objects::nonNull)
                .toList();

        if (!compactConstructorStatements.isEmpty()) {
            addCompactConstructor(recordDeclaration, compactConstructorStatements);
        }

        return compiler;
    }

    private static String addField(CompilationUnit compiler, RecordDeclaration recordDeclaration, JavaFieldDefinition field) {
        var fieldType = field.type();
        Optional
                .ofNullable(fieldType.getDefinition())
                .ifPresent(typeDefinition -> addMember(compiler, recordDeclaration, typeDefinition));
        var param = new Parameter(parseType(getRecordParamType(field)), field.name());
        recordDeclaration.getParameters().add(param);
        fieldType
                .getFieldAnnotators()
                .forEach(annotator -> annotator.annotate(param, field.property()));
        return getCompactConstructorStatement(field);
    }

    private static String getRecordParamType(JavaFieldDefinition fieldDefinition) {
        var fieldType = fieldDefinition.type();
        if (fieldType.isWrapped()) {
            return fieldType.getFullName();
        }
        if (!fieldDefinition.property().optional()) {
            return toPrimitiveType(fieldType.getFullName())
                    .map(Class::getName)
                    .orElse(fieldType.getFullName());
        }
        if (fieldType.getDefaultValue() != null) {
            return fieldType.getFullName();
        }
        return "Optional<%s>".formatted(fieldType.getFullName());
    }

    private static String getCompactConstructorStatement(JavaFieldDefinition fieldDefinition) {
        var fieldType = fieldDefinition.type();
        var fieldName = fieldDefinition.name();
        if (fieldType.isWrapped()) {
            var emptyValue = fieldType.getDefaultValue();
            var unmodifiable = getUnmodifiableWrapper(fieldType.getFullName());
            return "%s = %s == null ? %s : %s(%s);".formatted(
                    fieldName, fieldName, emptyValue, unmodifiable, fieldName);
        }
        if (!fieldDefinition.property().optional()) {
            return null;
        }
        if (fieldType.getDefaultValue() != null) {
            return "%s = %s == null ? %s : %s;".formatted(
                    fieldName, fieldName, fieldType.getDefaultValue(), fieldName);
        }
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
        if (fullName.startsWith("java.util.List<")) {
            return "java.util.Collections.unmodifiableList";
        }
        if (fullName.startsWith("java.util.Set<")) {
            return "java.util.Collections.unmodifiableSet";
        }
        if (fullName.startsWith("java.util.Map<")) {
            return "java.util.Collections.unmodifiableMap";
        }
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
