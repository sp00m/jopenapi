package com.github.sp00m.jopenapi.read.vo;

import io.swagger.v3.oas.models.media.Schema;

public record OpenApiComponent(
    String name,
    Schema<?> schema
) {

}
