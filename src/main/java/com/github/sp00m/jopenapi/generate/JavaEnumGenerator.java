package com.github.sp00m.jopenapi.generate;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.EnumConstantDeclaration;
import com.github.sp00m.jopenapi.Names;
import com.github.sp00m.jopenapi.read.vo.JavaEnumDefinition;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jooq.Catalog;
import org.jooq.EnumType;
import org.jooq.Schema;
import org.jooq.impl.DSL;

import java.util.Map;

import static com.github.javaparser.StaticJavaParser.parseBlock;
import static com.github.javaparser.StaticJavaParser.parseExpression;
import static com.github.javaparser.ast.Modifier.Keyword.*;

/**
 * Generates enum declarations with a {@code String value} field, a reverse lookup map
 * ({@code BY_VALUE}), and a {@code @JsonCreator findByValue} method.
 *
 * <p>When the enum has a default, invalid values fall back to it (with a warning log).
 * When it has no default, invalid values throw {@code InvalidPropertyException}.
 *
 * <p>Optionally implements jOOQ's {@code EnumType} when the schema has an {@code x-jooq}
 * extension, adding {@code getLiteral()}, {@code getName()}, and optional
 * {@code getCatalog()}/{@code getSchema()} overrides.
 */
@RequiredArgsConstructor
final class JavaEnumGenerator implements JavaTypeGenerator {

    private final JavaEnumDefinition enumDefinition;
    private final CompilationUnit compiler = CompilationUnitFactory.create();

    @Override
    public CompilationUnit generate() {

        var enumDeclaration = compiler
                .addEnum(enumDefinition.name())
                .addAnnotation(RequiredArgsConstructor.class);

        enumDeclaration.addFieldWithInitializer(
                "Map<String, %s>".formatted(enumDefinition.name()),
                "BY_VALUE",
                parseExpression("Stream.of(values()).collect(Collectors.toUnmodifiableMap(%s::value, Function.identity()))".formatted(enumDefinition.name())),
                PRIVATE, STATIC, FINAL
        );

        enumDeclaration.addField(String.class, "value", PRIVATE, FINAL);

        enumDeclaration
                .addMethod("value", PUBLIC)
                .setType(String.class)
                .setBody(parseBlock("{ return value; }"))
                .addAnnotation(JsonValue.class);

        final String onValueNotFound;
        if (enumDefinition.defaultValue() == null) {
            onValueNotFound = ".orElseThrow(() -> new InvalidPropertyException(\"%s\", value))".formatted(enumDefinition.name());
        } else {
            onValueNotFound = ".orElseGet(() -> { log.warn(\"No %s with value {}\", value); return %s; })".formatted(enumDefinition.name(), enumDefinition.decorateDefaultValue());
            enumDeclaration.addAnnotation(Slf4j.class);
        }

        enumDeclaration
                .addMethod("findByValue", PUBLIC, STATIC)
                .setType(enumDefinition.name())
                .addParameter(String.class, "value")
                .setBody(parseBlock("{ return Optional.ofNullable(value).map(BY_VALUE::get)%s; }".formatted(onValueNotFound)))
                .addAnnotation(JsonCreator.class);

        enumDefinition.values().forEach(value -> {
            var valueDeclaration = new EnumConstantDeclaration(Names.toEnumValue(value));
            valueDeclaration.addArgument("\"" + value + "\"");
            enumDeclaration.addEntry(valueDeclaration);
        });

        var extensions = enumDefinition.schema().getExtensions();
        if (extensions.get("x-jooq") instanceof Map<?, ?> jooq) {
            compiler.addImport("org.jooq.*", false, true);
            compiler.addImport(DSL.class);
            enumDeclaration.addImplementedType(EnumType.class);
            enumDeclaration
                    .addMethod("getLiteral", PUBLIC)
                    .setType(String.class)
                    .setBody(parseBlock("{ return value; }"))
                    .addAnnotation(Override.class);
            enumDeclaration
                    .addMethod("getName", PUBLIC)
                    .setType(String.class)
                    .setBody(parseBlock("{ return %s; }".formatted(jooq.get("name") instanceof String name ? "\"%s\"".formatted(name) : "null")))
                    .addAnnotation(Override.class);
            if (jooq.get("catalog") instanceof String catalog) {
                enumDeclaration
                        .addMethod("getCatalog", PUBLIC)
                        .setType(Catalog.class)
                        .setBody(parseBlock("{ return DSL.catalog(\"%s\"); }".formatted(catalog)))
                        .addAnnotation(Override.class);
            }
            if (jooq.get("schema") instanceof String schema) {
                enumDeclaration
                        .addMethod("getSchema", PUBLIC)
                        .setType(Schema.class)
                        .setBody(parseBlock("{ return DSL.schema(\"%s\"); }".formatted(schema)))
                        .addAnnotation(Override.class);
            }
        }

        return compiler;
    }

}
