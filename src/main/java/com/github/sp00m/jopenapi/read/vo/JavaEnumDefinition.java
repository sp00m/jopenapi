package com.github.sp00m.jopenapi.read.vo;

import lombok.Value;

import java.util.List;

@Value
public class JavaEnumDefinition implements JavaTypeDefinition {

    String packageName;
    String name;
    String description;
    List<String> values;

}
