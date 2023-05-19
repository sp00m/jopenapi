package com.github.sp00m.jopenapi.read.vo;

public interface JavaTypeDefinition {

    String getPackageName();

    String getName();

    default String getFullName() {
        return "%s.%s".formatted(getPackageName(), getName());
    }

}
