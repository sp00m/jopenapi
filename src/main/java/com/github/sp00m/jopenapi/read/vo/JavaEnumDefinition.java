package com.github.sp00m.jopenapi.read.vo;

import com.github.sp00m.jopenapi.Names;
import io.swagger.v3.oas.models.media.Schema;

import java.util.Collections;
import java.util.List;

/**
 * Stores the raw {@code Schema<?>} (mutable swagger-parser object) rather than extracting
 * individual fields because JavaEnumGenerator needs access to {@code getExtensions()}
 * for the {@code x-jooq} integration, and {@code description()} delegates to the schema
 * to avoid duplicating state.
 */
public record JavaEnumDefinition(
        String packageName,
        String name,
        Schema<?> schema,
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

    /** Formats a default value as a Java expression: {@code EnumName.ENUM_VALUE}. */
    public String decorateDefaultValue(String defaultValue) {
        return "%s.%s".formatted(name, Names.toEnumValue(defaultValue));
    }

    public String decorateDefaultValue() {
        return decorateDefaultValue(defaultValue);
    }

}
