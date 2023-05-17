package com.github.sp00m.jopenapi.read;

import io.swagger.v3.oas.models.media.Schema;
import lombok.Value;

@Value
public class OpenApiComponent {

    String name;
    Schema<?> schema;

}
