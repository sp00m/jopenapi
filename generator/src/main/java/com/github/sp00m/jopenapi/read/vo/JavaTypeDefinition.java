package com.github.sp00m.jopenapi.read.vo;

public interface JavaTypeDefinition {

    String getPackageName();

    String getName();

    String getDescription();

    default String getFullName() {
        return "%s.%s".formatted(getPackageName(), getName());
    }

}
