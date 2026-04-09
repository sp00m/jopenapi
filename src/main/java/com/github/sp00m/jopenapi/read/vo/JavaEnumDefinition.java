package com.github.sp00m.jopenapi.read.vo;

import java.util.List;

public record JavaEnumDefinition(
    String packageName,
    String name,
    String description,
    List<String> values
) implements JavaTypeDefinition {

}
