package com.github.sp00m.jopenapi.generate;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.CompactConstructorDeclaration;
import com.github.javaparser.ast.body.Parameter;
import com.github.javaparser.ast.body.RecordDeclaration;
import com.github.sp00m.jopenapi.read.vo.JavaValueRecordDefinition;
import lombok.RequiredArgsConstructor;

import static com.github.javaparser.StaticJavaParser.parseBlock;
import static com.github.javaparser.StaticJavaParser.parseType;
import static com.github.javaparser.ast.Modifier.Keyword.PUBLIC;

/**
 * Generates wrapper records for top-level schemas that are simple types or collections
 * (e.g. {@code type: array} → {@code record SimpleArray(@JsonValue List<String> value)}).
 *
 * <p>Uses {@code @JsonValue} on the single field and {@code @JsonCreator} on the compact
 * constructor so Jackson transparently wraps/unwraps the value. Reuses
 * {@link JavaRecordGenerator#getCompactConstructorStatement} to handle collection immutability.
 */
@RequiredArgsConstructor
final class JavaValueRecordGenerator implements JavaTypeGenerator {

    private final JavaValueRecordDefinition valueRecordDefinition;
    private final CompilationUnit compiler = CompilationUnitFactory.create();

    @Override
    public CompilationUnit generate() {

        var recordDeclaration = new RecordDeclaration()
                .setName(valueRecordDefinition.name())
                .addModifier(PUBLIC);
        compiler.addType(recordDeclaration);

        var fieldDefinition = valueRecordDefinition.field();
        var fieldType = fieldDefinition.type();
        var paramType = JavaRecordGenerator
                .toPrimitiveType(fieldType.fullName())
                .map(Class::getName)
                .orElse(fieldType.fullName());

        var param = new Parameter(parseType(paramType), fieldDefinition.name());
        recordDeclaration.getParameters().add(param);
        param.addAnnotation(JsonValue.class);

        var compactConstructor = new CompactConstructorDeclaration()
                .setName(valueRecordDefinition.name())
                .setModifiers(PUBLIC)
                .addAnnotation(JsonCreator.class);

        var statement = JavaRecordGenerator.getCompactConstructorStatement(fieldDefinition);
        if (statement != null) {
            compactConstructor.setBody(parseBlock("{" + statement + "}"));
        }

        recordDeclaration.addMember(compactConstructor);

        return compiler;
    }

}
