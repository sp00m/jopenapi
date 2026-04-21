package com.github.sp00m.jopenapi.generate;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.EnumConstantDeclaration;
import com.github.javaparser.ast.body.EnumDeclaration;
import com.github.sp00m.jopenapi.Names;
import com.github.sp00m.jopenapi.read.vo.JavaEnumDefinition;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import static com.github.javaparser.StaticJavaParser.parseBlock;
import static com.github.javaparser.StaticJavaParser.parseExpression;
import static com.github.javaparser.ast.Modifier.Keyword.*;

@RequiredArgsConstructor
final class JavaEnumGenerator implements JavaTypeGenerator {

    private final JavaEnumDefinition enumDefinition;
    private final CompilationUnit compiler = CompilationUnitFactory.create();

    @Override
    public CompilationUnit generate() {

        EnumDeclaration enumDeclaration = compiler
                .addEnum(enumDefinition.name())
                .addAnnotation(RequiredArgsConstructor.class);

        enumDeclaration.addFieldWithInitializer(
                "Map<String, %s>".formatted(enumDefinition.name()),
                "BY_VALUE",
                parseExpression("Stream.of(values()).collect(Collectors.toUnmodifiableMap(%s::get, Function.identity()))".formatted(enumDefinition.name())),
                PRIVATE, STATIC, FINAL
        );

        enumDeclaration.addField(String.class, "value", PRIVATE, FINAL);

        enumDeclaration.addMethod("value", PUBLIC)
                .setType(String.class)
                .setBody(parseBlock("{return value;}"))
                .addAnnotation(JsonValue.class);

        final String onValueNotFound;
        if (enumDefinition.defaultValue() == null) {
            onValueNotFound = ".orElseThrow(() -> new InvalidPropertyException(\"%s\", value))".formatted(enumDefinition.name());
        } else {
            onValueNotFound = ".orElseGet(() -> {log.warn(\"No %s with value {}\", value);return %s;})".formatted(enumDefinition.name(), enumDefinition.decorateDefaultValue());
            enumDeclaration.addAnnotation(Slf4j.class);
        }

        enumDeclaration.addMethod("findByValue", PUBLIC, STATIC)
                .setType(enumDefinition.name())
                .addParameter(String.class, "value")
                .setBody(parseBlock("{return Optional.ofNullable(value).map(BY_VALUE::get)%s;}".formatted(onValueNotFound)))
                .addAnnotation(JsonCreator.class);

        enumDefinition.values().forEach(value -> {
            var valueDeclaration = new EnumConstantDeclaration(Names.toEnumValue(value));
            valueDeclaration.addArgument("\"" + value + "\"");
            enumDeclaration.addEntry(valueDeclaration);
        });

        return compiler;
    }

}
