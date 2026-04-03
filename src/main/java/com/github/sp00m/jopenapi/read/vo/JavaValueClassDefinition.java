package com.github.sp00m.jopenapi.read.vo;

import lombok.Value;

@Value
public class JavaValueClassDefinition implements JavaTypeDefinition {

    String packageName;
    String name;
    String description;
    JavaFieldDefinition field;

}
