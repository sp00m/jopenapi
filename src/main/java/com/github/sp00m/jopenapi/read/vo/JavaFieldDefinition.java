package com.github.sp00m.jopenapi.read.vo;

public record JavaFieldDefinition(
        OpenApiProperty property,
        String name,
        JavaType type
) {

}
