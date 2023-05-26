package com.github.sp00m.jopenapi.read.vo;

import lombok.Value;

import java.util.Map;

@Value
public class JavaInterfaceDefinition implements JavaTypeDefinition {

    String packageName;
    String name;
    String description;
    String propertyName;
    Map<String, String> mapping;

}
