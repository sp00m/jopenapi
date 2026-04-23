package com.github.sp00m.jopenapi.read.vo;

public record OpenApiProperty(
        String name,
        OpenApiSchema schema,
        boolean optional
) {

}
