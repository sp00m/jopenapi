package com.github.sp00m.jopenapi.read.vo;

import io.swagger.v3.oas.models.media.Schema;

public record OpenApiProperty(
        String name,
        Schema<?> schema,
        boolean optional
) {

    public boolean readOnly() {
        return Boolean.TRUE.equals(schema.getReadOnly());
    }

    public boolean writeOnly() {
        return Boolean.TRUE.equals(schema.getWriteOnly());
    }

}
