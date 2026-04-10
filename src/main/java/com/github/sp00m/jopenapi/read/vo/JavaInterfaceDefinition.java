package com.github.sp00m.jopenapi.read.vo;

import java.util.Collections;
import java.util.Map;

public record JavaInterfaceDefinition(
        String packageName,
        String name,
        String description,
        String propertyName,
        Map<String, String> mapping
) implements JavaTypeDefinition {

    public JavaInterfaceDefinition {
        mapping = Collections.unmodifiableMap(mapping);
    }

}
