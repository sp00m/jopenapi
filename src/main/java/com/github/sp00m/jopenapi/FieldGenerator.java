package com.github.sp00m.jopenapi;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonProperty.Access;
import com.github.javaparser.ast.NodeList;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.FieldDeclaration;
import com.github.javaparser.ast.expr.MemberValuePair;
import com.github.javaparser.ast.expr.NormalAnnotationExpr;
import io.swagger.v3.oas.models.media.Schema;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.net.URI;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.Optional;
import java.util.UUID;

import static com.github.javaparser.StaticJavaParser.parseExpression;
import static com.github.javaparser.StaticJavaParser.parseName;

/**
 * https://swagger.io/docs/specification/data-models/data-types
 */
@Slf4j
@RequiredArgsConstructor
public final class FieldGenerator {

    private final String basePackage;
    private final ClassOrInterfaceDeclaration dto;
    private final String name;
    private final Schema<?> schema;
    private final boolean required;

    public void run() {
        var ref = schema.get$ref();
        if (ref == null) {
            switch (schema.getType()) {
                case "string":
                    generateStringFormattedField();
                    break;
                case "number":
                    break;
                case "integer":
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
        String ref = schema.get$ref();
        String schemaName = ref.substring(ref.lastIndexOf('/') + 1);
        String type = basePackage + "." + DtoGenerator.normalizeClassName(schemaName);
        generateField(resolveType(type));
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
        String format = Optional
                .ofNullable(schema.getFormat())
                .orElse("");
        switch (format) {
            case "date" -> generateField(resolveType(LocalDate.class));
            case "date-time" -> generateField(resolveType(OffsetDateTime.class));
            case "uuid" -> generateField(resolveType(UUID.class));
            case "uri" -> generateField(resolveType(URI.class));
            default -> generateStringField();
        }
    }

    private void generateStringField() {
        FieldDeclaration field = generateField(resolveType(String.class));
        // TODO
        schema.getMinLength();
        schema.getMaxLength();
        schema.getPattern();
    }

    private FieldDeclaration generateField(String type) {
        return dto
                .addField(type, normalizeFieldName(name))
                .addAnnotation(generateJsonPropertyAnnotation());
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

    private String resolveType(Class<?> type) {
        return resolveType(type.getName());
    }

    private String resolveType(String type) {
        boolean optional = !required || Boolean.TRUE.equals(schema.getNullable());
        return optional ? "java.util.Optional<" + type + ">" : type;
    }

    private static String normalizeFieldName(String name) {
        // TODO lowerCamelCase
        return name;
    }

}
