package com.github.sp00m.jopenapi.generate;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.Modifier;
import com.github.javaparser.ast.NodeList;
import com.github.javaparser.ast.body.CompactConstructorDeclaration;
import com.github.javaparser.ast.body.Parameter;
import com.github.javaparser.ast.body.RecordDeclaration;
import com.github.sp00m.jopenapi.read.vo.JavaValueClassDefinition;
import lombok.RequiredArgsConstructor;

import java.util.Collections;

import static com.github.javaparser.StaticJavaParser.parseBlock;
import static com.github.javaparser.StaticJavaParser.parseType;
import static com.github.javaparser.ast.Modifier.Keyword.PUBLIC;
import static com.github.javaparser.ast.Modifier.Keyword.STATIC;

@RequiredArgsConstructor
final class JavaValueClassGenerator implements JavaTypeGenerator {

    private final JavaValueClassDefinition valueClassDefinition;
    private final CompilationUnit compiler = new CompilationUnit();

    @Override
    public CompilationUnit generate() {
        var recordDeclaration = new RecordDeclaration(
                NodeList.nodeList(Modifier.publicModifier()),
                valueClassDefinition.getName()
        );
        compiler.addType(recordDeclaration);

        var fieldDefinition = valueClassDefinition.getField();
        var fieldType = fieldDefinition.getType();

        // Determine parameter type
        String paramType;
        boolean isCollection = fieldType.isWrapped();
        if (isCollection) {
            paramType = fieldType.getFullName();
        } else {
            paramType = JavaClassGenerator.toPrimitiveType(fieldType.getFullName())
                    .map(Class::getName)
                    .orElse(fieldType.getFullName());
        }

        // Create parameter with @JsonValue
        var param = new Parameter(parseType(paramType), fieldDefinition.getName());
        recordDeclaration.getParameters().add(param);
        param.addAnnotation(JsonValue.class);

        // Compact constructor for collections
        if (isCollection) {
            compiler.addImport(Collections.class);
            var emptyValue = fieldType.getDefaultValue();
            var unmodifiable = JavaClassGenerator.getUnmodifiableWrapper(fieldType.getFullName());
            var statement = "%s = %s == null ? %s : %s(%s);".formatted(
                    fieldDefinition.getName(), fieldDefinition.getName(),
                    emptyValue, unmodifiable, fieldDefinition.getName());
            var compactConstructor = new CompactConstructorDeclaration(
                    NodeList.nodeList(Modifier.publicModifier()),
                    valueClassDefinition.getName()
            );
            compactConstructor.setBody(parseBlock("{" + statement + "}"));
            recordDeclaration.addMember(compactConstructor);
        }

        // Static factory method
        recordDeclaration
                .addMethod("of", PUBLIC, STATIC)
                .addParameter(paramType, fieldDefinition.getName())
                .setType(valueClassDefinition.getName())
                .setBody(parseBlock("{return new %s(%s);}".formatted(valueClassDefinition.getName(), fieldDefinition.getName())))
                .addAnnotation(JsonCreator.class);

        return compiler;
    }

}
