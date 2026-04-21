package com.github.sp00m.jopenapi.generate;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonUnwrapped;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.Modifier;
import com.github.javaparser.ast.NodeList;
import com.github.javaparser.ast.body.*;
import com.github.sp00m.jopenapi.read.JavaFieldAnnotator;
import com.github.sp00m.jopenapi.read.vo.JavaFieldDefinition;
import com.github.sp00m.jopenapi.read.vo.JavaRecordDefinition;
import com.github.sp00m.jopenapi.read.vo.JavaTypeDefinition;
import lombok.Builder;
import lombok.RequiredArgsConstructor;
import lombok.With;
import org.apache.commons.lang3.ClassUtils;

import java.util.*;

import static com.github.javaparser.StaticJavaParser.*;
import static com.github.javaparser.ast.Modifier.Keyword.STATIC;

@RequiredArgsConstructor
final class JavaRecordGenerator implements JavaTypeGenerator {

    private final JavaRecordDefinition recordDefinition;
    private final CompilationUnit compiler = new CompilationUnit();

    @Override
    public CompilationUnit generate() {

        var recordDeclaration = new RecordDeclaration(
                NodeList.nodeList(Modifier.publicModifier()),
                recordDefinition.name()
        );
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
        var param = new Parameter(parseType(getRecordParamType(field)), field.name());
        recordDeclaration.getParameters().add(param);
        fieldType
                .fieldAnnotators()
                .forEach(annotator -> annotator.annotate(param, field.property()));
        return getCompactConstructorStatement(field);
    }

    private static String getRecordParamType(JavaFieldDefinition fieldDefinition) {
        var fieldType = fieldDefinition.type();
        if (fieldType.collection()) {
            return fieldType.fullName();
        }
        if (!fieldDefinition.property().optional() || fieldType.decoratedDefaultValue() != null) {
            return toPrimitiveType(fieldType.fullName())
                    .map(Class::getName)
                    .orElse(fieldType.fullName());
        }
        return "Optional<%s>".formatted(fieldType.fullName());
    }

    private static String getCompactConstructorStatement(JavaFieldDefinition fieldDefinition) {
        var fieldType = fieldDefinition.type();
        var fieldName = fieldDefinition.name();
        if (fieldType.collection()) {
            var emptyValue = fieldType.decoratedDefaultValue();
            var unmodifiable = getUnmodifiableWrapper(fieldType.fullName());
            return "%s = %s == null ? %s : %s(%s);".formatted(
                    fieldName, fieldName, emptyValue, unmodifiable, fieldName);
        }
        if (fieldDefinition.property().optional() && fieldType.decoratedDefaultValue() == null) {
            return "%s = Objects.requireNonNullElse(%s, Optional.empty());".formatted(
                    fieldName, fieldName);
        }
        return null;
    }

    private void addCompactConstructor(RecordDeclaration recordDeclaration, List<String> statements) {
        if (statements.stream().anyMatch(s -> s.contains("Optional.empty()"))) {
            compiler.addImport(Optional.class);
        }
        if (statements.stream().anyMatch(s -> s.contains("Collections.unmodifiable"))) {
            compiler.addImport(Collections.class);
        }
        if (statements.stream().anyMatch(s -> s.contains("Objects.requireNonNullElse"))) {
            compiler.addImport(Objects.class);
        }
        var compactConstructor = new CompactConstructorDeclaration(
                NodeList.nodeList(Modifier.publicModifier()),
                recordDefinition.name()
        );
        var body = "{\n" + String.join("\n", statements) + "\n}";
        compactConstructor.setBody(parseBlock(body));
        recordDeclaration.addMember(compactConstructor);
    }

    private void addFactoryMethod(RecordDeclaration recordDeclaration) {
        var fields = recordDefinition.fields();

        // Build factory parameter list (excluding read-only fields)
        var factoryParams = new StringBuilder();
        var factoryChecks = new StringBuilder();
        var constructorArgs = new StringBuilder();

        boolean needsObjects = false;
        boolean needsOptional = false;

        for (int i = 0; i < fields.size(); i++) {
            var field = fields.get(i);
            var isReadOnly = Boolean.TRUE.equals(field.property().schema().getReadOnly());
            var isWriteOnly = Boolean.TRUE.equals(field.property().schema().getWriteOnly());
            var isJsonUnwrapped = field.type().fieldAnnotators().contains(JavaFieldAnnotator.JSON_UNWRAPPED);

            if (i > 0) {
                constructorArgs.append(", ");
            }

            if (isReadOnly) {
                // Read-only fields are excluded from factory params, pass default
                constructorArgs.append(getReadOnlyDefault(field));
                if (getReadOnlyDefault(field).contains("Optional.empty()")) {
                    needsOptional = true;
                }
                continue;
            }

            // Add factory parameter
            if (factoryParams.length() > 0) {
                factoryParams.append(", ");
            }

            var paramType = field.type().fullName();
            var paramName = field.name();

            // Build annotation for the factory parameter
            if (isJsonUnwrapped) {
                factoryParams.append("@JsonUnwrapped() ");
            } else {
                factoryParams.append("@JsonProperty(value = \"%s\"".formatted(field.property().name()));
                if (isWriteOnly) {
                    factoryParams.append(", access = JsonProperty.Access.WRITE_ONLY");
                }
                factoryParams.append(") ");
            }

            factoryParams.append("%s %s".formatted(paramType, paramName));

            // Build validation/transformation in factory body and constructor arg
            if (field.type().collection()) {
                // Collections pass through as-is — compact constructor handles null/immutability
                constructorArgs.append(paramName);
            } else if (!field.property().optional()) {
                // Required non-collection: null-check
                factoryChecks.append("if (%s == null) { throw new com.github.jopenapi.support.MissingPropertyException(\"%s\"); }\n".formatted(
                        paramName, field.property().name()));
                constructorArgs.append(paramName);
            } else if (field.type().decoratedDefaultValue() != null) {
                // Optional with default: use Objects.requireNonNullElse
                needsObjects = true;
                constructorArgs.append("Objects.requireNonNullElse(%s, %s)".formatted(
                        paramName, field.type().decoratedDefaultValue()));
            } else {
                // Optional without default, non-collection: wrap in Optional.ofNullable
                needsOptional = true;
                constructorArgs.append("Optional.ofNullable(%s)".formatted(paramName));
            }
        }

        var methodBody = new StringBuilder();
        methodBody.append("{\n");
        methodBody.append(factoryChecks);
        methodBody.append("return new %s(%s);\n".formatted(recordDefinition.name(), constructorArgs));
        methodBody.append("}");

        var method = recordDeclaration.addMethod("create", STATIC);
        method.setType(parseType(recordDefinition.name()));
        method.setBody(parseBlock(methodBody.toString()));
        method.addAnnotation(JsonCreator.class);

        // Parse and add parameters to the method
        if (factoryParams.length() > 0) {
            // Parse each parameter with annotations
            var paramString = factoryParams.toString();
            var parsedParams = parseParameters(paramString);
            for (var param : parsedParams) {
                method.addParameter(param);
            }
        }

        // Add imports
        compiler.addImport(JsonCreator.class);
        if (needsObjects) {
            compiler.addImport(Objects.class);
        }
        if (needsOptional) {
            compiler.addImport(Optional.class);
        }
        // Check if we need JsonProperty/JsonUnwrapped for factory params
        var hasJsonProperty = fields.stream()
                .anyMatch(f -> !Boolean.TRUE.equals(f.property().schema().getReadOnly())
                        && !f.type().fieldAnnotators().contains(JavaFieldAnnotator.JSON_UNWRAPPED));
        var hasJsonUnwrapped = fields.stream()
                .anyMatch(f -> !Boolean.TRUE.equals(f.property().schema().getReadOnly())
                        && f.type().fieldAnnotators().contains(JavaFieldAnnotator.JSON_UNWRAPPED));
        if (hasJsonProperty) {
            compiler.addImport(JsonProperty.class);
        }
        if (hasJsonUnwrapped) {
            compiler.addImport(JsonUnwrapped.class);
        }
    }

    private static NodeList<Parameter> parseParameters(String paramString) {
        // Parse by creating a temporary method declaration
        var tempMethod = "class Tmp { void m(%s) {} }".formatted(paramString);
        var tempCu = parse(tempMethod);
        var tempMethodDecl = tempCu.getType(0).getMethods().get(0);
        return tempMethodDecl.getParameters();
    }

    private static String getReadOnlyDefault(JavaFieldDefinition field) {
        var fieldType = field.type();
        if (fieldType.collection()) {
            return fieldType.decoratedDefaultValue(); // e.g. java.util.Collections.emptyList()
        }
        if (field.property().optional() && fieldType.decoratedDefaultValue() == null) {
            // It's Optional<X> in the record
            return "Optional.empty()";
        }
        if (fieldType.decoratedDefaultValue() != null) {
            return fieldType.decoratedDefaultValue();
        }
        // Fallback for required read-only primitives (shouldn't normally happen
        // since read-only properties are now treated as optional)
        var primitiveType = toPrimitiveType(fieldType.fullName());
        if (primitiveType.isPresent()) {
            var prim = primitiveType.get();
            if (prim == boolean.class) return "false";
            return "0";
        }
        return "null";
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
