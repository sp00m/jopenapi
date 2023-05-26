package com.github.sp00m.jopenapi.read;

import com.github.sp00m.jopenapi.Names;
import com.github.sp00m.jopenapi.read.vo.JavaClassDefinition;
import com.github.sp00m.jopenapi.read.vo.JavaEnumDefinition;
import com.github.sp00m.jopenapi.read.vo.JavaFieldDefinition;
import com.github.sp00m.jopenapi.read.vo.JavaInterfaceDefinition;
import com.github.sp00m.jopenapi.read.vo.JavaType;
import com.github.sp00m.jopenapi.read.vo.OpenApiProperty;
import io.swagger.v3.oas.models.media.Discriminator;
import io.swagger.v3.oas.models.media.Schema;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import javax.annotation.Nullable;
import java.net.URI;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

import static java.util.stream.Collectors.toMap;

@RequiredArgsConstructor
@Slf4j
final class OpenApiSchemaReader {

    private final String packageName;
    private final String schemaName;
    private final Schema<?> schema;

    @Nullable
    public JavaType read() {
        try {
            var type = readOrThrow()
                    .description(schema.getDescription());
            return Optional
                    .ofNullable(schema.getDefault())
                    .map(defaultValue -> type.defaultValue(prepareDefaultValue(defaultValue)))
                    .orElse(type);
        } catch (Throwable t) {
            log.error("Unable to read schema {}", schemaName, t);
            return null;
        }
    }

    private String prepareDefaultValue(Object defaultValue) {
        return defaultValue instanceof Date date
                ? new SimpleDateFormat("yyyy-MM-dd").format(date)
                : defaultValue.toString();
    }

    @Nullable
    private JavaType readOrThrow() {
        if (schema.getNot() != null) {
            throw new IllegalStateException("'not' not supported");
        }
        var anyOf = Optional
                .ofNullable(schema.getAnyOf())
                .orElseGet(Collections::emptyList);
        var allOf = Optional
                .ofNullable(schema.getAllOf())
                .orElseGet(Collections::emptyList);
        var oneOf = Optional
                .ofNullable(schema.getOneOf())
                .orElseGet(Collections::emptyList);
        var enumValues = Optional
                .ofNullable(schema.getEnum())
                .orElseGet(Collections::emptyList)
                .stream()
                .filter(Objects::nonNull)
                .map(Object::toString)
                .toList();
        if (!enumValues.isEmpty()) {
            return readEnum(enumValues);
        } else if (oneOf.isEmpty() && schema.getDiscriminator() != null) {
            log.warn("'discriminator' without 'oneOf', defaulting to 'oneOf'");
            return readOneOf(schema.getDiscriminator());
        } else if (!anyOf.isEmpty()) {
            throw new IllegalStateException("'anyOf' not supported");
        } else if (!allOf.isEmpty()) {
            return readAllOf(allOf);
        } else if (!oneOf.isEmpty()) {
            return readOneOf(schema.getDiscriminator());
        } else if (schema.get$ref() != null) {
            return readRef();
        } else if (schema.getType() == null) {
            log.warn("null type, defaulting to 'object'");
            return readObject();
        }
        return switch (schema.getType()) {
            case "string" -> readString();
            case "number" -> readNumber();
            case "integer" -> readInteger();
            case "boolean" -> readBoolean();
            case "array" -> readArray();
            case "object" -> readObject();
            default -> {
                log.warn("Unknown type '{}', defaulting to 'object'", schema.getType());
                yield readObject();
            }
        };
    }

    private JavaType readRef() {
        return new JavaType(refToTypeFullName(schema.get$ref()));
    }

    private String refToTypeFullName(String ref) {
        var packageName = findRefPackageName(ref);
        var componentName = findRefComponentName(ref);
        var className = Names.toClassName(componentName);
        return "%s.%s".formatted(packageName, className);
    }

    private String findRefPackageName(String ref) {
        if (!ref.contains("#")) {
            return packageName;
        }
        var filePath = ref.substring(0, ref.indexOf('#'));
        if (filePath.isEmpty()) {
            return packageName;
        }
        List<String> packageNames = new ArrayList<>(List.of(packageName.split("\\.", -1)));
        packageNames.remove(packageNames.size() - 1);
        for (String dirName : filePath.split("/", -1)) {
            if (dirName.equals("..")) {
                packageNames.remove(packageNames.size() - 1);
            } else if (!dirName.equals(".")) {
                packageNames.add(Names.toPackageName(dirName));
            }
        }
        return String.join(".", packageNames);
    }

    private String findRefComponentName(String ref) {
        return ref.substring(ref.lastIndexOf('/') + 1);
    }

    private JavaType readString() {
        var format = Optional
                .ofNullable(schema.getFormat())
                .orElse("");
        return switch (format) {
            case "date" -> new JavaType(LocalDate.class)
                    .defaultValueDecorator("java.time.LocalDate.parse(\"%s\")"::formatted);
            case "date-time" -> new JavaType(OffsetDateTime.class)
                    .defaultValueDecorator("java.time.OffsetDateTime.parse(\"%s\")"::formatted);
            case "uuid" -> new JavaType(UUID.class)
                    .defaultValueDecorator("java.util.UUID.fromString(\"%s\")"::formatted);
            case "uri" -> new JavaType(URI.class)
                    .defaultValueDecorator("java.net.URI.create(\"%s\")"::formatted);
            default -> new JavaType(String.class)
                    .string()
                    .defaultValueDecorator("\"%s\""::formatted);
        };
    }

    private JavaType readEnum(List<String> enumValues) {
        var enumName = Names.toClassName(schemaName);
        var enumDefinition = new JavaEnumDefinition(packageName, enumName, schema.getDescription(), enumValues);
        return new JavaType(enumName, enumDefinition).defaultValueDecorator(x -> "%s.%s".formatted(enumName, Names.toEnumValue(x)));
    }

    private JavaType readNumber() {
        var format = Optional
                .ofNullable(schema.getFormat())
                .orElse("");
        var field = switch (format) {
            case "float" -> new JavaType(Float.class).defaultValueDecorator("%sF"::formatted);
            case "double" -> new JavaType(Double.class).defaultValueDecorator("%sD"::formatted);
            default -> new JavaType(Number.class).defaultValueDecorator("new java.math.BigDecimal(\"%s\")"::formatted);
        };
        return field.number();
    }

    private JavaType readInteger() {
        var format = Optional
                .ofNullable(schema.getFormat())
                .orElse("");
        var field = switch (format) {
            case "int32" -> new JavaType(Integer.class);
            case "int64" -> new JavaType(Long.class).defaultValueDecorator("%sL"::formatted);
            default -> new JavaType(Integer.class);
        };
        return field.number();
    }

    private JavaType readBoolean() {
        return new JavaType(Boolean.class);
    }

    @Nullable
    private JavaType readArray() {
        var itemsSchema = schema.getItems();
        var itemsType = new OpenApiSchemaReader(packageName, schemaName, itemsSchema).read();
        if (itemsType == null) {
            return null;
        }
        var type = new JavaType(itemsType.getFullName(), itemsType.getDefinition());
        return Boolean.TRUE.equals(schema.getUniqueItems()) ? type.set() : type.list();
    }

    @Nullable
    private JavaType readObject() {
        var additionalProperties = schema.getAdditionalProperties();
        if (additionalProperties instanceof Schema<?> additionalPropertiesSchema) {
            return readAdditionalPropertiesSchema(additionalPropertiesSchema);
        } else if (additionalProperties instanceof Boolean additionalPropertiesBoolean && additionalPropertiesBoolean) {
            return new JavaType(Object.class).map();
        }
        return readStandardObject();
    }

    @Nullable
    private JavaType readAdditionalPropertiesSchema(Schema<?> additionalPropertiesSchema) {
        var additionalPropertiesType = new OpenApiSchemaReader(packageName, schemaName, additionalPropertiesSchema).read();
        if (additionalPropertiesType == null) {
            return null;
        }
        return new JavaType(additionalPropertiesType.getFullName(), additionalPropertiesType.getDefinition()).map();
    }

    private JavaType readStandardObject() {
        var properties = Optional
                .ofNullable(schema.getProperties())
                .orElseGet(Collections::emptyMap);
        if (properties.isEmpty()) {
            log.warn("'object' without 'properties'");
            return new JavaType(Object.class);
        }
        var requiredProperties = Optional
                .ofNullable(schema.getRequired())
                .orElseGet(Collections::emptyList);
        var fieldDefinitions = properties
                .entrySet()
                .stream()
                .map(e -> toFieldDefinition(e.getKey(), e.getValue(), requiredProperties))
                .filter(Objects::nonNull)
                .toList();
        var className = Names.toClassName(schemaName);
        var classDefinition = new JavaClassDefinition(packageName, className, schema.getDescription(), fieldDefinitions);
        return new JavaType(className, classDefinition);
    }

    private JavaFieldDefinition toFieldDefinition(String propertyName, Schema<?> propertySchema, List<String> requiredProperties) {
        var optional = isPropertyOptional(propertyName, propertySchema, requiredProperties);
        var property = new OpenApiProperty(propertyName, propertySchema, optional);
        var fieldType = new OpenApiSchemaReader(packageName, propertyName, propertySchema).read();
        if (fieldType == null) {
            return null;
        }
        var fieldName = Names.toFieldName(propertyName);
        return new JavaFieldDefinition(property, fieldName, fieldType.jsonProperty());
    }

    private boolean isPropertyOptional(String propertyName, Schema<?> propertySchema, List<String> requiredProperties) {
        var enumValues = Optional
                .ofNullable(propertySchema.getEnum())
                .orElseGet(Collections::emptyList);
        var nullable = Boolean.TRUE.equals(propertySchema.getNullable()) && (enumValues.isEmpty() || enumValues.contains(null));
        var optional = !requiredProperties.contains(propertyName);
        var hasMin = propertySchema.getMinItems() != null && propertySchema.getMinItems() > 0
                || propertySchema.getMinProperties() != null && propertySchema.getMinProperties() > 0;
        return (nullable || optional) && !hasMin;
    }

    private JavaType readAllOf(List<Schema> allOf) {
        var refFieldDefinitions = allOf
                .stream()
                .filter(allOfSchema -> allOfSchema.get$ref() != null)
                .map(allOfSchema -> {
                    var refType = new OpenApiSchemaReader(packageName, schemaName, allOfSchema).read();
                    if (refType == null) {
                        return null;
                    }
                    var refTypeName = refType.getFullName().substring(refType.getFullName().lastIndexOf('.'));
                    var fieldName = Names.toFieldName(refTypeName);
                    var property = new OpenApiProperty(fieldName, allOfSchema, false);
                    return new JavaFieldDefinition(property, fieldName, refType.jsonUnwrapped());
                })
                .filter(Objects::nonNull)
                .toList();
        var nonRefTypes = allOf
                .stream()
                .filter(allOfSchema -> allOfSchema.get$ref() == null)
                .map(allOfSchema -> new OpenApiSchemaReader(packageName, schemaName, allOfSchema).read())
                .filter(Objects::nonNull)
                .toList();
        if (nonRefTypes.isEmpty()) {
            var className = Names.toClassName(schemaName);
            var classDefinition = new JavaClassDefinition(packageName, className, schema.getDescription(), refFieldDefinitions);
            return new JavaType(className, classDefinition);
        } else if (nonRefTypes.size() == 1) {
            var nonRefInnerType = nonRefTypes.get(0);
            var nonRefInnerTypeDefinition = nonRefInnerType.getDefinition();
            if (nonRefInnerTypeDefinition instanceof JavaClassDefinition classDefinition && !nonRefInnerType.isWrapped()) {
                return new JavaType(classDefinition.getName(), classDefinition.addFields(refFieldDefinitions));
            } else {
                throw new IllegalStateException("Only a non-$ref schema of type 'object' is supported with 'allOf'");
            }
        }
        throw new IllegalStateException("Only zero or one non-$ref schema is supported with 'allOf', but got " + nonRefTypes.size());
    }

    private JavaType readOneOf(Discriminator discriminator) {
        var mapping = Optional
                .ofNullable(discriminator.getMapping())
                .orElseGet(Map::of)
                .entrySet()
                .stream()
                .collect(toMap(Map.Entry::getKey, e -> refToTypeFullName(e.getValue())));
        if (mapping.isEmpty()) {
            throw new IllegalStateException("Only explicit mapping is supported with 'oneOf'");
        }
        var className = Names.toClassName(schemaName);
        var interfaceDefinition = new JavaInterfaceDefinition(packageName, className, schema.getDescription(), discriminator.getPropertyName(), mapping);
        return new JavaType(className, interfaceDefinition);
    }

}
