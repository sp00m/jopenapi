package com.github.sp00m.jopenapi.read.vo;

public interface JavaTypeDefinition extends Comparable<JavaTypeDefinition> {

    String packageName();

    String name();

    String description();

    default String fullName() {
        return "%s.%s".formatted(packageName(), name());
    }

    @Override
    default int compareTo(JavaTypeDefinition o) {
        return fullName().compareTo(o.fullName());
    }

}
