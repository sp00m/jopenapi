package com.github.sp00m.jopenapi.read;

import lombok.Value;

import java.util.List;

@Value
public class JavaEnumDefinition implements JavaInnerTypeDefinition {

    String name;
    List<String> values;

}
