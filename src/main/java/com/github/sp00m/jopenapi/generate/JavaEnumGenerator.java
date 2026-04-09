package com.github.sp00m.jopenapi.generate;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.EnumConstantDeclaration;
import com.github.javaparser.ast.body.EnumDeclaration;
import com.github.sp00m.jopenapi.Names;
import com.github.sp00m.jopenapi.read.vo.JavaEnumDefinition;
import lombok.RequiredArgsConstructor;

import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.github.javaparser.StaticJavaParser.parseBlock;
import static com.github.javaparser.StaticJavaParser.parseExpression;
import static com.github.javaparser.ast.Modifier.Keyword.*;

@RequiredArgsConstructor
final class JavaEnumGenerator implements JavaTypeGenerator {

    private final JavaEnumDefinition enumDefinition;
    private final CompilationUnit compiler = new CompilationUnit();

    @Override
    public CompilationUnit generate() {

        EnumDeclaration enumDeclaration = compiler
                .addEnum(enumDefinition.name())
                .addAnnotation(RequiredArgsConstructor.class);

        compiler
                .addImport(Map.class)
                .addImport(Stream.class)
                .addImport(Collectors.class)
                .addImport(Function.class)
                .addImport(Optional.class);

        enumDeclaration.addFieldWithInitializer(
                "Map<String, %s>".formatted(enumDefinition.name()),
                "BY_VALUE",
                parseExpression("Stream.of(values()).collect(Collectors.toUnmodifiableMap(%s::get, Function.identity()))".formatted(enumDefinition.name())),
                PRIVATE, STATIC, FINAL
        );

        enumDeclaration.addField(String.class, "value", PRIVATE, FINAL);

        enumDeclaration.addMethod("get", PUBLIC)
                .setType(String.class)
                .setBody(parseBlock("{return value;}"))
                .addAnnotation(JsonValue.class);

        enumDeclaration.addMethod("get", PUBLIC, STATIC)
                .setType(enumDefinition.name())
                .addParameter(String.class, "value")
                .setBody(parseBlock("{return Optional.ofNullable(BY_VALUE.get(value)).orElseThrow(() -> new IllegalArgumentException(\"No %s with value \" + value));}".formatted(enumDefinition.name())))
                .addAnnotation(JsonCreator.class);

        enumDefinition.values().forEach(value -> {
            var valueDeclaration = new EnumConstantDeclaration(Names.toEnumValue(value));
            valueDeclaration.addArgument("\"" + value + "\"");
            enumDeclaration.addEntry(valueDeclaration);
        });

        return compiler;
    }

}
