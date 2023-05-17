package com.github.sp00m.jopenapi.generate;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.EnumConstantDeclaration;
import com.github.javaparser.ast.body.EnumDeclaration;
import com.github.sp00m.jopenapi.read.JavaEnumDefinition;
import com.github.sp00m.jopenapi.read.Names;
import lombok.RequiredArgsConstructor;

import static com.github.javaparser.StaticJavaParser.parseBlock;
import static com.github.javaparser.StaticJavaParser.parseExpression;
import static com.github.javaparser.ast.Modifier.Keyword.FINAL;
import static com.github.javaparser.ast.Modifier.Keyword.PRIVATE;
import static com.github.javaparser.ast.Modifier.Keyword.PUBLIC;
import static com.github.javaparser.ast.Modifier.Keyword.STATIC;

@RequiredArgsConstructor
public final class JavaEnumGenerator implements JavaTypeGenerator {

    private final JavaEnumDefinition enumDefinition;
    private final CompilationUnit compiler = new CompilationUnit();

    @Override
    public CompilationUnit generate() {

        EnumDeclaration enumDeclaration = compiler
                .addEnum(enumDefinition.getName())
                .addAnnotation(RequiredArgsConstructor.class);

        enumDeclaration.addFieldWithInitializer(
                "java.util.Map<String, %s>".formatted(enumDefinition.getName()),
                "BY_VALUE",
                parseExpression("java.util.stream.Stream.of(values()).collect(java.util.stream.Collectors.toUnmodifiableMap(%s::getValue, java.util.function.Function.identity()))".formatted(enumDefinition.getName())),
                PRIVATE, STATIC, FINAL
        );

        enumDeclaration.addField(String.class, "value", PRIVATE, FINAL);

        enumDeclaration.addMethod("getValue", PUBLIC)
                .setType(String.class)
                .setBody(parseBlock("{return value;}"))
                .addAnnotation(JsonValue.class);

        enumDeclaration.addMethod("getByValue", PUBLIC, STATIC)
                .setType(enumDefinition.getName())
                .addParameter(String.class, "value")
                .setBody(parseBlock("{return java.util.Optional.ofNullable(BY_VALUE.get(value)).orElseThrow(() -> new java.lang.IllegalArgumentException(\"No %s with value \" + value));}".formatted(enumDefinition.getName())))
                .addAnnotation(JsonCreator.class);

        enumDefinition.getValues().forEach(value -> {
            var entry = new EnumConstantDeclaration(Names.toEnumValue(value));
            entry.addArgument("\"" + value + "\"");
            enumDeclaration.addEntry(entry);
        });

        return compiler;
    }

}
