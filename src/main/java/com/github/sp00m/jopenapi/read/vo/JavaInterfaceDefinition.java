package com.github.sp00m.jopenapi.read.vo;

import java.util.Collections;
import java.util.Map;

/**
 * Represents a {@code oneOf} composition as a sealed interface.
 *
 * @param propertyName the discriminator property name (used for {@code @JsonTypeInfo})
 * @param mapping      discriminator value → fully-qualified implementing type name
 */
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
