package com.github.sp00m.jopenapi.read.vo;

import io.swagger.v3.oas.models.media.Schema;

public record OpenApiProperty(
        String name,
        Schema<?> schema,
        boolean optional
) {

}
