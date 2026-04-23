package com.github.sp00m.jopenapi.read.vo;

import io.swagger.v3.oas.models.media.Schema;
import lombok.RequiredArgsConstructor;

import java.math.BigDecimal;
import java.util.*;

import static java.util.stream.Collectors.collectingAndThen;
import static java.util.stream.Collectors.toMap;

/**
 * Anti-corruption wrapper around Swagger's {@link Schema}. Centralizes all null-handling
 * ({@code Boolean.TRUE.equals}, {@code Optional.ofNullable}…) and OpenAPI 3.1 quirks
 * (e.g. {@code types} array → single type) so that the rest of the codebase never touches
 * the raw Swagger model directly. Getters return safe defaults: empty lists, empty strings,
 * {@code false} — callers never need to deal with {@code null}.
 */
@RequiredArgsConstructor
public final class OpenApiSchema {

    private final Schema<?> schema;

    public String getDescription() {
        return schema.getDescription();
    }

    public <T> T getDefault() {
        return (T) schema.getDefault();
    }

    public String get$ref() {
        return schema.get$ref();
    }

    public String getType() {
        if (schema.getType() != null) {
            return schema.getType();
        }
        return Optional
                .ofNullable(schema.getTypes())
                .orElseGet(Collections::emptySet)
                .stream()
                .filter(t -> !"null".equals(t))
                .findFirst()
                .orElse(null);
    }

    public Map<String, OpenApiSchema> getProperties() {
        return Optional
                .ofNullable(schema.getProperties())
                .orElseGet(Collections::emptyMap)
                .entrySet()
                .stream()
                .collect(collectingAndThen(
                        toMap(Map.Entry::getKey, e -> wrap(e.getValue()), (a, b) -> a, LinkedHashMap::new),
                        Collections::unmodifiableMap
                ));
    }

    /**
     * Returns the format, or an empty string if unset (allows direct use in {@code switch}).
     */
    public String getFormat() {
        var format = schema.getFormat();
        return format == null ? "" : format;
    }

    public OpenApiDiscriminator getDiscriminator() {
        var discriminator = schema.getDiscriminator();
        return discriminator == null ? null : new OpenApiDiscriminator(discriminator);
    }

    public OpenApiSchema getNot() {
        return wrap(schema.getNot());
    }

    public List<OpenApiSchema> getAnyOf() {
        return wrap(schema.getAnyOf());
    }

    public List<String> getRequired() {
        return Optional
                .ofNullable(schema.getRequired())
                .map(Collections::unmodifiableList)
                .orElseGet(Collections::emptyList);
    }

    public List<OpenApiSchema> getAllOf() {
        return wrap(schema.getAllOf());
    }

    public List<OpenApiSchema> getOneOf() {
        return wrap(schema.getOneOf());
    }

    public OpenApiSchema getItems() {
        return wrap(schema.getItems());
    }

    public Object getAdditionalProperties() {
        var additionalProperties = schema.getAdditionalProperties();
        if (additionalProperties instanceof Schema<?> additionalPropertiesSchema) {
            return wrap(additionalPropertiesSchema);
        } else if (additionalProperties instanceof Boolean additionalPropertiesBoolean) {
            return additionalPropertiesBoolean;
        }
        return null;
    }

    public boolean isUniqueItems() {
        return Boolean.TRUE.equals(schema.getUniqueItems());
    }

    public List<String> getEnum() {
        return Optional
                .ofNullable(schema.getEnum())
                .orElseGet(Collections::emptyList)
                .stream()
                .filter(Objects::nonNull)
                .map(Object::toString)
                .toList();
    }

    public boolean isNullable() {
        var enumValues = schema.getEnum();
        var hasNullType = Optional.ofNullable(schema.getTypes()).orElseGet(Collections::emptySet).contains("null");
        // An enum is only nullable if its values list explicitly includes null
        return (Boolean.TRUE.equals(schema.getNullable()) || hasNullType) && (enumValues.isEmpty() || enumValues.contains(null));
    }

    public String getPattern() {
        return schema.getPattern();
    }

    public Integer getMinSize() {
        return Optional
                .ofNullable(schema.getMinLength())
                .or(() -> Optional.ofNullable(schema.getMinItems()))
                .or(() -> Optional.ofNullable(schema.getMinProperties()))
                .orElse(null);
    }

    public Integer getMaxSize() {
        return Optional
                .ofNullable(schema.getMaxLength())
                .or(() -> Optional.ofNullable(schema.getMaxItems()))
                .or(() -> Optional.ofNullable(schema.getMaxProperties()))
                .orElse(null);
    }

    public BigDecimal getDecimalMin() {
        return schema.getMinimum();
    }

    public BigDecimal getDecimalMax() {
        return schema.getMaximum();
    }

    public boolean isExclusiveMinimum() {
        return Boolean.TRUE.equals(schema.getExclusiveMinimum());
    }

    public boolean isExclusiveMaximum() {
        return Boolean.TRUE.equals(schema.getExclusiveMaximum());
    }

    public Map<String, Object> getExtensions() {
        return Optional
                .ofNullable(schema.getExtensions())
                .map(Collections::unmodifiableMap)
                .orElseGet(Collections::emptyMap);
    }

    public BigDecimal getMultipleOf() {
        return schema.getMultipleOf();
    }

    public boolean isReadOnly() {
        return Boolean.TRUE.equals(schema.getReadOnly());
    }

    public boolean isWriteOnly() {
        return Boolean.TRUE.equals(schema.getWriteOnly());
    }

    private static List<OpenApiSchema> wrap(List<Schema> schemas) {
        return Optional
                .ofNullable(schemas)
                .orElseGet(Collections::emptyList)
                .stream()
                .filter(Objects::nonNull)
                .map(OpenApiSchema::wrap)
                .toList();
    }

    private static OpenApiSchema wrap(Schema<?> schema) {
        return schema == null ? null : new OpenApiSchema(schema);
    }

}
