package com.github.sp00m.jopenapi.read.vo;

public interface JavaTypeDefinition {

    String packageName();

    String name();

    String description();

    default String fullName() {
        return "%s.%s".formatted(packageName(), name());
    }

}
