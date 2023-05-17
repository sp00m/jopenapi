package com.github.sp00m.jopenapi.read;

import java.net.URI;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.Collections;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

public final class OpenApiPropertyReader {

    private final String basePackage;
    private final OpenApiProperty property;

    public OpenApiPropertyReader(String basePackage, OpenApiProperty property) {
        this.basePackage = basePackage;
        this.property = property;
    }

    public JavaFieldDefinition read() {
        var name = Names.toFieldName(property.getName());
        var type = readType();
        return new JavaFieldDefinition(property, name, type);
    }

    private JavaFieldDefinition.Type readType() {
        var ref = property.getSchema().get$ref();
        if (ref != null) {
            return readRef();
        }
        return switch (property.getSchema().getType()) {
            case "string" -> readString();
            case "number" -> readNumber();
            case "integer" -> readInteger();
            case "boolean" -> readBoolean();
            case "array" -> readArray();
            case "object" -> readObject();
            default -> throw new IllegalArgumentException(property.getSchema().getType());
        };
    }

    private JavaFieldDefinition.Type readBoolean() {
        return new JavaFieldDefinition.Type(Boolean.class);
    }

    private JavaFieldDefinition.Type readArray() {
        var schema = property.getSchema().getItems();
        var transientProperty = new OpenApiProperty(property.getName(), schema, true);
        var transientPropertyReader = new OpenApiPropertyReader(basePackage, transientProperty);
        var transientFieldType = transientPropertyReader.read();
        var rawType = transientFieldType.getType().getRawType();
        var type = new JavaFieldDefinition.Type(rawType);
        return Boolean.TRUE.equals(schema.getUniqueItems()) ? type.set() : type.list();
    }

    private JavaFieldDefinition.Type readObject() {
        var innerClassName = Names.toClassName(property.getName());
        var transientComponentReader = new OpenApiComponent(innerClassName, property.getSchema());
        var reader = new OpenApiComponentReader(basePackage, transientComponentReader);
        return new JavaFieldDefinition.Type(innerClassName, reader.read());
    }

    private JavaFieldDefinition.Type readNumber() {
        var format = Optional
                .ofNullable(property.getSchema().getFormat())
                .orElse("");
        var field = switch (format) {
            case "float" -> new JavaFieldDefinition.Type(Float.class);
            case "double" -> new JavaFieldDefinition.Type(Double.class);
            default -> new JavaFieldDefinition.Type(Number.class);
        };
        return field.number();
    }

    private JavaFieldDefinition.Type readInteger() {
        var format = Optional
                .ofNullable(property.getSchema().getFormat())
                .orElse("");
        var field = switch (format) {
            case "int32" -> new JavaFieldDefinition.Type(Integer.class);
            case "int64" -> new JavaFieldDefinition.Type(Long.class);
            default -> new JavaFieldDefinition.Type(Number.class);
        };
        return field.number();
    }

    private JavaFieldDefinition.Type readString() {
        var format = Optional
                .ofNullable(property.getSchema().getFormat())
                .orElse("");
        return switch (format) {
            case "date" -> new JavaFieldDefinition.Type(LocalDate.class);
            case "date-time" -> new JavaFieldDefinition.Type(OffsetDateTime.class);
            case "uuid" -> new JavaFieldDefinition.Type(UUID.class);
            case "uri" -> new JavaFieldDefinition.Type(URI.class);
            default -> readDefaultStringField();
        };
    }

    private JavaFieldDefinition.Type readDefaultStringField() {
        var enumValueNames = Optional
                .ofNullable(property.getSchema().getEnum())
                .orElseGet(Collections::emptyList)
                .stream()
                .filter(Objects::nonNull)
                .map(Object::toString)
                .toList();
        final JavaFieldDefinition.Type type;
        if (enumValueNames.isEmpty()) {
            type = new JavaFieldDefinition.Type(String.class).string();
        } else {
            var enumName = Names.toClassName(property.getName());
            var enumDefinition = new JavaEnumDefinition(enumName, enumValueNames);
            type = new JavaFieldDefinition.Type(enumName, enumDefinition);
        }
        return type;
    }

    private JavaFieldDefinition.Type readRef() {
        var ref = property.getSchema().get$ref();
        var schemaName = ref.substring(ref.lastIndexOf('/') + 1);
        var type = basePackage + "." + Names.toClassName(schemaName);
        return new JavaFieldDefinition.Type(type);
    }

}
