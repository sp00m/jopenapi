package com.github.sp00m.jopenapi.read.vo;

/**
 * Wrapper for top-level schemas that aren't objects/enums/oneOf (e.g. a component that is
 * just {@code type: integer} or {@code type: array}). These become single-field records
 * with {@code @JsonValue} on the field so Jackson can serialize/deserialize them transparently.
 */
public record JavaValueRecordDefinition(
        String packageName,
        String name,
        String description,
        JavaFieldDefinition field
) implements JavaTypeDefinition {

}
