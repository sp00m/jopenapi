package com.github.sp00m.jopenapi.read.vo;

import com.github.sp00m.jopenapi.Names;

import java.util.Collections;
import java.util.List;

/**
 * Stores an {@link OpenApiSchema} wrapper rather than extracting individual fields because
 * {@code JavaEnumGenerator} needs access to {@code getExtensions()} for the {@code x-jooq}
 * integration, and {@link #description()} delegates to the schema to avoid duplicating state.
 */
public record JavaEnumDefinition(
        String packageName,
        String name,
        OpenApiSchema schema,
        List<String> values,
        String defaultValue
) implements JavaTypeDefinition {

    public JavaEnumDefinition {
        values = Collections.unmodifiableList(values);
    }

    @Override
    public String description() {
        return schema.getDescription();
    }

    /**
     * Formats a default value as a Java expression: {@code EnumName.ENUM_VALUE}.
     */
    public String decorateDefaultValue(String defaultValue) {
        return "%s.%s".formatted(name, Names.toEnumValue(defaultValue));
    }

    public String decorateDefaultValue() {
        return decorateDefaultValue(defaultValue);
    }

}
