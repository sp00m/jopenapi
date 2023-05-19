package com.github.sp00m.jopenapi.read.vo;

import lombok.Value;

@Value
public class JavaFieldDefinition {

    OpenApiProperty property;
    String name;
    JavaType type;

}
