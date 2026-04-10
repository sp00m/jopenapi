package com.github.sp00m.jopenapi.read.vo;

import com.github.sp00m.jopenapi.Names;

import java.util.Collections;
import java.util.List;

public record JavaEnumDefinition(
        String packageName,
        String name,
        String description,
        List<String> values,
        String defaultValue
) implements JavaTypeDefinition {

    public JavaEnumDefinition {
        values = Collections.unmodifiableList(values);
    }

    public String decorateDefaultValue(String defaultValue) {
        return "%s.%s".formatted(name, Names.toEnumValue(defaultValue));
    }

    public String decorateDefaultValue() {
        return decorateDefaultValue(defaultValue);
    }

}
