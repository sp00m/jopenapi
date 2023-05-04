package com.github.sp00m.jopenapi;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonProperty.Access;
import com.fasterxml.jackson.annotation.JsonValue;
import com.github.javaparser.ast.Modifier;
import com.github.javaparser.ast.NodeList;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.EnumConstantDeclaration;
import com.github.javaparser.ast.body.EnumDeclaration;
import com.github.javaparser.ast.body.FieldDeclaration;
import com.github.javaparser.ast.expr.MemberValuePair;
import com.github.javaparser.ast.expr.NormalAnnotationExpr;
import io.swagger.v3.oas.models.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.net.URI;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.Collections;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

import static com.github.javaparser.StaticJavaParser.parseBlock;
import static com.github.javaparser.StaticJavaParser.parseExpression;
import static com.github.javaparser.StaticJavaParser.parseName;
import static com.github.javaparser.ast.Modifier.createModifierList;

/**
 * https://swagger.io/docs/specification/data-models/data-types
 */
@Slf4j
public final class FieldGenerator {

    private final String basePackage;
    private final ClassOrInterfaceDeclaration dto;
    private final String name;
    private final Schema<?> schema;
    private final boolean optional;

    public FieldGenerator(String basePackage, ClassOrInterfaceDeclaration dto, String name, Schema<?> schema, boolean required) {
        this.basePackage = basePackage;
        this.dto = dto;
        this.name = name;
        this.schema = schema;
        // https://swagger.io/docs/specification/data-models/enums/
        var enumValues = Optional
                .ofNullable(schema.getEnum())
                .orElseGet(Collections::emptyList);
        this.optional = (!required || Boolean.TRUE.equals(schema.getNullable()))
                && (enumValues.isEmpty() || enumValues.contains(null));
    }

    public void run() {
        var ref = schema.get$ref();
        if (ref == null) {
            switch (schema.getType()) {
                case "string":
                    generateStringFormattedField();
                    break;
                case "number":
                case "integer":
                    schema.getMinimum();
                    schema.getExclusiveMinimum();
                    schema.getMaximum();
                    schema.getExclusiveMaximum();
                    break;
                case "boolean":
                    break;
                case "array":
                    break;
                case "object":
                    break;
            }
        } else {
            generateRefField();
        }
    }

    private void generateRefField() {
        var ref = schema.get$ref();
        var schemaName = ref.substring(ref.lastIndexOf('/') + 1);
        var type = basePackage + "." + Generator.normalizeClassName(schemaName);
        generateField(type);
    }

    private void generateArrayField() {
        schema.getItems();
        schema.getItems().get$ref();
        schema.getMinItems();
        schema.getMaxItems();
        schema.getUniqueItems();
    }

    private void generateObjectField() {
        schema.getProperties();
        schema.getAdditionalProperties();
        schema.getMinProperties();
        schema.getMaxProperties();
    }

    private void generateStringFormattedField() {
        var format = Optional
                .ofNullable(schema.getFormat())
                .orElse("");
        switch (format) {
            case "date" -> generateField(LocalDate.class);
            case "date-time" -> generateField(OffsetDateTime.class);
            case "uuid" -> generateField(UUID.class);
            case "uri" -> generateField(URI.class);
            default -> generateStringField();
        }
    }

    private void generateStringField() {
        var enumValueNames = Optional
                .ofNullable(schema.getEnum())
                .orElseGet(Collections::emptyList)
                .stream()
                .filter(Objects::nonNull)
                .toList();
        if (enumValueNames.isEmpty()) {
            var field = generateField(String.class);
            // TODO:
            schema.getMinLength();
            schema.getMaxLength();
            schema.getPattern();
        } else {
            // TODO move into dedicated generator class to be reused
            dto.tryAddImportToParentCompilationUnit(JsonValue.class);
            dto.tryAddImportToParentCompilationUnit(RequiredArgsConstructor.class);
            var enumName = Generator.normalizeClassName(name);
            var enumDeclaration = new EnumDeclaration(createModifierList(Modifier.Keyword.PUBLIC), enumName)
                    .addAnnotation(RequiredArgsConstructor.class);
            enumDeclaration.addField(String.class, "value", Modifier.Keyword.FINAL);
            enumDeclaration.addMethod("getValue", Modifier.Keyword.PUBLIC)
                    .setType(String.class)
                    .setBody(parseBlock("{return value;}"))
                    .addAnnotation(JsonValue.class);
            // TODO add JsonCreator
            enumValueNames.forEach(valueName -> {
                var enumValue = new EnumConstantDeclaration(Generator.normalizeEnumValueName(valueName.toString()));
                enumValue.addArgument("\"" + valueName + "\"");
                enumDeclaration.addEntry(enumValue);
            });
            dto.addMember(enumDeclaration);
            generateField(enumName);
        }
    }

    private FieldDeclaration generateField(Class<?> type) {
        return generateField(type.getName());
    }

    private FieldDeclaration generateField(String type) {
        var field = dto
                .addField(resolveType(type), Generator.normalizeFieldName(name))
                .addAnnotation(generateJsonPropertyAnnotation());
        if (!optional) {
            field.addAnnotation(NotNull.class);
        }
        return field;
    }

    private NormalAnnotationExpr generateJsonPropertyAnnotation() {
        var attributes = new NodeList<MemberValuePair>();
        attributes.add(new MemberValuePair("value", parseExpression("\"" + name + "\"")));
        if (Boolean.TRUE.equals(schema.getReadOnly())) {
            attributes.add(new MemberValuePair("access", parseExpression(Access.class.getName() + "." + Access.READ_ONLY.name())));
        } else if (Boolean.TRUE.equals(schema.getWriteOnly())) {
            attributes.add(new MemberValuePair("access", parseExpression(Access.class.getName() + "." + Access.WRITE_ONLY.name())));
        }
        return new NormalAnnotationExpr(parseName(JsonProperty.class.getName()), attributes);
    }

    private String resolveType(String type) {
        return optional ? "java.util.Optional<" + type + ">" : type;
    }

}
