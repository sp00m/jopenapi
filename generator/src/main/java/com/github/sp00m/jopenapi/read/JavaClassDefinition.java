package com.github.sp00m.jopenapi.read;

import lombok.Value;

import java.util.List;

@Value
public class JavaClassDefinition implements JavaInnerTypeDefinition {

    OpenApiComponent component;
    String name;
    List<JavaFieldDefinition> fields;

}
