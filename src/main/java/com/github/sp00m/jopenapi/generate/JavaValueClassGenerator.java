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

@RequiredArgsConstructor
final class JavaValueClassGenerator implements JavaTypeGenerator {

    private final JavaValueClassDefinition valueClassDefinition;
    private final CompilationUnit compiler = new CompilationUnit();

    @Override
    public CompilationUnit generate() {

        var recordDeclaration = new RecordDeclaration(
                NodeList.nodeList(Modifier.publicModifier()),
                valueClassDefinition.name()
        );
        compiler.addType(recordDeclaration);

        var fieldDefinition = valueClassDefinition.field();
        var fieldType = fieldDefinition.type();

        String paramType;
        boolean isCollection = fieldType.isWrapped();
        if (isCollection) {
            paramType = fieldType.getFullName();
        } else {
            paramType = JavaClassGenerator
                    .toPrimitiveType(fieldType.getFullName())
                    .map(Class::getName)
                    .orElse(fieldType.getFullName());
        }

        var param = new Parameter(parseType(paramType), fieldDefinition.name());
        recordDeclaration.getParameters().add(param);
        param.addAnnotation(JsonValue.class);

        var compactConstructor = new CompactConstructorDeclaration(
                NodeList.nodeList(Modifier.publicModifier()),
                valueClassDefinition.name()
        );
        compiler.addImport(JsonCreator.class);
        compactConstructor.addAnnotation(JsonCreator.class);

        if (isCollection) {
            compiler.addImport(Collections.class);
            var emptyValue = fieldType.getDefaultValue();
            var unmodifiable = JavaClassGenerator.getUnmodifiableWrapper(fieldType.getFullName());
            var statement = "%s = %s == null ? %s : %s(%s);".formatted(
                    fieldDefinition.name(), fieldDefinition.name(),
                    emptyValue, unmodifiable, fieldDefinition.name());
            compactConstructor.setBody(parseBlock("{" + statement + "}"));
        }

        recordDeclaration.addMember(compactConstructor);

        return compiler;
    }

}
