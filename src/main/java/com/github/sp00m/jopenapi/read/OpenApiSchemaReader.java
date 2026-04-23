package com.github.sp00m.jopenapi.read;

import com.github.sp00m.jopenapi.Names;
import com.github.sp00m.jopenapi.read.vo.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import javax.annotation.Nullable;
import java.net.URI;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.*;

import static java.util.stream.Collectors.toMap;

/**
 * Recursive schema reader that converts an OpenAPI {@code Schema} into a {@link JavaType}.
 *
 * <p>This is the heart of the read phase. It handles every OpenAPI type ({@code string},
 * {@code integer}, {@code boolean}, {@code array}, {@code object}), compositions
 * ({@code allOf}, {@code oneOf}), {@code $ref} resolution, and inline enums.
 *
 * <p>Each {@code readXxx()} method returns a {@link JavaType} that captures:
 * <ul>
 *   <li>The Java type name (e.g. {@code String}, {@code List<Integer>})</li>
 *   <li>A {@code defaultValueDecorator} — a function that turns a raw default string into
 *       a valid Java expression (e.g. wrapping a date string with {@code LocalDate.parse("...")})</li>
 *   <li>Which {@link JavaPropertyAnnotator}s apply (validation constraints, Jackson annotations)</li>
 * </ul>
 */
@RequiredArgsConstructor
@Slf4j
final class OpenApiSchemaReader {

    // swagger-parser deserializes date defaults as java.util.Date; we need to format
    // them back to ISO strings for the generated Java code.
    private static final DateFormat DATE_FORMATTER = new SimpleDateFormat("yyyy-MM-dd");

    private final String packageName;
    private final String schemaName;
    private final OpenApiSchema schema;

    @Nullable
    public JavaType read() {
        try {
            var type = Objects
                    .requireNonNull(readOrThrow())
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
                ? DATE_FORMATTER.format(date)
                : defaultValue.toString();
    }

    @Nullable
    private JavaType readOrThrow() {
        if (schema.getNot() != null) {
            throw new IllegalStateException("'not' not supported");
        }
        var anyOf = schema.getAnyOf();
        var allOf = schema.getAllOf();
        var oneOf = schema.getOneOf();
        var enumValues = schema.getEnum();
        var discriminator = schema.getDiscriminator();
        // Priority: enum > discriminator > anyOf > allOf > oneOf > $ref > primitive type.
        // Enum must come first because a schema can have both type+enum.
        if (!enumValues.isEmpty()) {
            return readEnum(enumValues, schema.getDefault());
        } else if (oneOf.isEmpty() && discriminator != null) {
            log.warn("'discriminator' without 'oneOf', defaulting to 'oneOf'");
            return readOneOf(discriminator);
        } else if (!anyOf.isEmpty()) {
            throw new IllegalStateException("'anyOf' not supported");
        } else if (!allOf.isEmpty()) {
            return readAllOf(allOf);
        } else if (!oneOf.isEmpty()) {
            return readOneOf(discriminator);
        } else if (schema.get$ref() != null) {
            return readRef();
        }
        var type = schema.getType();
        if (type == null) {
            log.warn("null type, defaulting to 'object'");
            return readObject();
        }
        return switch (type) {
            case "string" -> readString();
            case "number" -> readNumber();
            case "integer" -> readInteger();
            case "boolean" -> readBoolean();
            case "array" -> readArray();
            case "object" -> readObject();
            default -> {
                log.warn("Unknown type '{}', defaulting to 'object'", type);
                yield readObject();
            }
        };
    }

    /**
     * Resolves a {@code $ref} to a fully-qualified Java type name. Handles cross-file
     * references by walking the relative path segments to compute the target package.
     */
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
        return switch (schema.getFormat()) {
            case "date" -> new JavaType(LocalDate.class)
                    .defaultValueDecorator("LocalDate.parse(\"%s\")"::formatted);
            case "date-time" -> new JavaType(OffsetDateTime.class)
                    .defaultValueDecorator("OffsetDateTime.parse(\"%s\")"::formatted);
            case "uuid" -> new JavaType(UUID.class)
                    .defaultValueDecorator("UUID.fromString(\"%s\")"::formatted);
            case "uri" -> new JavaType(URI.class)
                    .defaultValueDecorator("URI.create(\"%s\")"::formatted);
            default -> new JavaType(String.class)
                    .string()
                    .defaultValueDecorator("\"%s\""::formatted);
        };
    }

    private JavaType readEnum(List<String> enumValues, String defaultValue) {
        var enumName = Names.toClassName(schemaName);
        var enumDefinition = new JavaEnumDefinition(packageName, enumName, schema, enumValues, defaultValue);
        return new JavaType(enumName, enumDefinition).defaultValueDecorator(enumDefinition::decorateDefaultValue);
    }

    private JavaType readNumber() {
        var field = switch (schema.getFormat()) {
            case "float" -> new JavaType(Float.class).defaultValueDecorator("%sF"::formatted);
            case "double" -> new JavaType(Double.class).defaultValueDecorator("%sD"::formatted);
            default -> new JavaType(Number.class).defaultValueDecorator("new BigDecimal(\"%s\")"::formatted);
        };
        return field.number();
    }

    private JavaType readInteger() {
        var field = switch (schema.getFormat()) {
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
        var type = new JavaType(itemsType.fullName(), itemsType.definition());
        return schema.getUniqueItems() ? type.set() : type.list();
    }

    @Nullable
    private JavaType readObject() {
        var additionalProperties = schema.getAdditionalProperties();
        if (additionalProperties instanceof OpenApiSchema additionalPropertiesSchema) {
            return readAdditionalPropertiesSchema(additionalPropertiesSchema);
        } else if (additionalProperties instanceof Boolean additionalPropertiesBoolean && additionalPropertiesBoolean) {
            return new JavaType(Object.class).map();
        }
        return readStandardObject();
    }

    @Nullable
    private JavaType readAdditionalPropertiesSchema(OpenApiSchema additionalPropertiesSchema) {
        var additionalPropertiesType = new OpenApiSchemaReader(packageName, schemaName, additionalPropertiesSchema).read();
        if (additionalPropertiesType == null) {
            return null;
        }
        return new JavaType(additionalPropertiesType.fullName(), additionalPropertiesType.definition()).map();
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
        var recordDefinition = new JavaRecordDefinition(packageName, className, schema.getDescription(), fieldDefinitions);
        return new JavaType(className, recordDefinition);
    }

    private JavaFieldDefinition toFieldDefinition(String propertyName, OpenApiSchema propertySchema, List<String> requiredProperties) {
        var optional = isPropertyOptional(propertyName, propertySchema, requiredProperties);
        var property = new OpenApiProperty(propertyName, propertySchema, optional);
        var fieldType = new OpenApiSchemaReader(packageName, propertyName, propertySchema).read();
        if (fieldType == null) {
            return null;
        }
        var fieldName = Names.toFieldName(propertyName);
        return new JavaFieldDefinition(property, fieldName, fieldType.jsonProperty());
    }

    /**
     * Determines if a property should be treated as optional. A property is optional when:
     * <ul>
     *   <li>It is nullable (explicit {@code nullable: true} or {@code type: [T, null]})</li>
     *   <li>It is not in the {@code required} list</li>
     *   <li>It is read-only (read-only fields are excluded from the factory, so they must be optional)</li>
     * </ul>
     * Note: {@code minItems}/{@code minProperties} constraints do <em>not</em> affect optionality.
     * They constrain the <em>content</em> of a collection when it is present, not its presence
     * itself (e.g. "either absent or an array with 3+ items" is a valid combination). These
     * constraints are enforced at validation time via the {@code @Size} annotation added by
     * {@link JavaPropertyAnnotator#SIZE}.
     */
    private boolean isPropertyOptional(String propertyName, OpenApiSchema propertySchema, List<String> requiredProperties) {
        var isNullable = propertySchema.getNullable();
        var isOptional = !requiredProperties.contains(propertyName);
        var isReadOnly = propertySchema.getReadOnly();
        return isNullable || isOptional || isReadOnly;
    }

    /**
     * Handles {@code allOf} composition. {@code $ref} entries become {@code @JsonUnwrapped} fields
     * (flattened into the record), while at most one inline {@code object} schema provides the
     * record's own fields. This allows combining inherited types with local properties.
     */
    private JavaType readAllOf(List<OpenApiSchema> allOf) {
        var refFieldDefinitions = allOf
                .stream()
                .filter(allOfSchema -> allOfSchema.get$ref() != null)
                .map(allOfSchema -> {
                    var refType = new OpenApiSchemaReader(packageName, schemaName, allOfSchema).read();
                    if (refType == null) {
                        return null;
                    }
                    var refTypeName = refType.fullName().substring(refType.fullName().lastIndexOf('.'));
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
            var recordDefinition = new JavaRecordDefinition(packageName, className, schema.getDescription(), refFieldDefinitions);
            return new JavaType(className, recordDefinition);
        } else if (nonRefTypes.size() == 1) {
            var nonRefInnerType = nonRefTypes.get(0);
            var nonRefInnerTypeDefinition = nonRefInnerType.definition();
            if (nonRefInnerTypeDefinition instanceof JavaRecordDefinition recordDefinition && !nonRefInnerType.collection()) {
                return new JavaType(recordDefinition.name(), recordDefinition.addFields(refFieldDefinitions));
            } else {
                throw new IllegalStateException("Only a non-$ref schema of type 'object' is supported with 'allOf'");
            }
        }
        throw new IllegalStateException("Only zero or one non-$ref schema is supported with 'allOf', but got " + nonRefTypes.size());
    }

    /**
     * Handles {@code oneOf} by generating a sealed interface. Requires an explicit
     * {@code discriminator.mapping} — implicit mapping is not supported because we need
     * to know the exact discriminator values upfront for {@code @JsonSubTypes}.
     */
    private JavaType readOneOf(OpenApiDiscriminator discriminator) {
        var mapping = discriminator
                .getMapping()
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
