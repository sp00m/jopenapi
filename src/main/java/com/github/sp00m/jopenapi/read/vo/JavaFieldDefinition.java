package com.github.sp00m.jopenapi.read.vo;

import lombok.With;

@With
public record JavaFieldDefinition(
        OpenApiProperty property,
        String name,
        JavaType type
) {

}
