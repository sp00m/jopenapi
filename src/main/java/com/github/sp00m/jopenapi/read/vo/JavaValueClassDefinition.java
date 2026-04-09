package com.github.sp00m.jopenapi.read.vo;

public record JavaValueClassDefinition(
    String packageName,
    String name,
    String description,
    JavaFieldDefinition field
) implements JavaTypeDefinition {

}
