package com.github.sp00m.jopenapi.read.vo;

public record JavaValueRecordDefinition(
        String packageName,
        String name,
        String description,
        JavaFieldDefinition field
) implements JavaTypeDefinition {

}
