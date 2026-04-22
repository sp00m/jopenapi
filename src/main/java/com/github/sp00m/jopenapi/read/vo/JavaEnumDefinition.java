package com.github.sp00m.jopenapi.read.vo;

import com.github.sp00m.jopenapi.Names;
import io.swagger.v3.oas.models.media.Schema;

import java.util.Collections;
import java.util.List;

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

    public String decorateDefaultValue(String defaultValue) {
        return "%s.%s".formatted(name, Names.toEnumValue(defaultValue));
    }

    public String decorateDefaultValue() {
        return decorateDefaultValue(defaultValue);
    }

}
