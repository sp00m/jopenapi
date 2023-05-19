package com.github.sp00m.jopenapi.read.vo;

import com.github.sp00m.jopenapi.read.JavaFieldAnnotator;
import lombok.Value;

import java.util.Set;
import java.util.TreeSet;

@Value
public class JavaType {

    String fullName;
    JavaTypeDefinition definition;
    Set<JavaFieldAnnotator> fieldAnnotators;
    String defaultIfNull;

    private JavaType(String fullName, JavaTypeDefinition definition, Set<JavaFieldAnnotator> fieldAnnotators, String defaultIfNull) {
        this.fullName = fullName.replaceFirst("^java\\.lang\\.([^.]+)$", "$1");
        this.definition = definition;
        this.fieldAnnotators = fieldAnnotators;
        this.defaultIfNull = defaultIfNull;
    }

    public JavaType(String fullName, JavaTypeDefinition definition) {
        this(fullName, definition, Set.of(), null);
    }

    public JavaType(String fullName) {
        this(fullName, null);
    }

    public JavaType(Class<?> fullName) {
        this(fullName.getName());
    }

    public JavaType number() {
        var updatedFieldAnnotators = new TreeSet<>(fieldAnnotators);
        updatedFieldAnnotators.add(JavaFieldAnnotator.MIN);
        updatedFieldAnnotators.add(JavaFieldAnnotator.MAX);
        return new JavaType(fullName, definition, updatedFieldAnnotators, defaultIfNull);
    }

    public JavaType string() {
        var updatedFieldAnnotators = new TreeSet<>(fieldAnnotators);
        updatedFieldAnnotators.add(JavaFieldAnnotator.PATTERN);
        updatedFieldAnnotators.add(JavaFieldAnnotator.SIZE);
        return new JavaType(fullName, definition, updatedFieldAnnotators, defaultIfNull);
    }

    public JavaType set() {
        var updatedFullName = "java.util.Set<%s>".formatted(fullName);
        var updatedFieldAnnotators = new TreeSet<>(fieldAnnotators);
        updatedFieldAnnotators.add(JavaFieldAnnotator.SIZE);
        return new JavaType(updatedFullName, definition, updatedFieldAnnotators, "java.util.Set.of()");
    }

    public JavaType list() {
        var updatedFullName = "java.util.List<%s>".formatted(fullName);
        var updatedFieldAnnotators = new TreeSet<>(fieldAnnotators);
        updatedFieldAnnotators.add(JavaFieldAnnotator.SIZE);
        return new JavaType(updatedFullName, definition, updatedFieldAnnotators, "java.util.List.of()");
    }

    public JavaType map() {
        var updatedFullName = "java.util.Map<String, %s>".formatted(fullName);
        var updatedFieldAnnotators = new TreeSet<>(fieldAnnotators);
        updatedFieldAnnotators.add(JavaFieldAnnotator.SIZE);
        return new JavaType(updatedFullName, definition, updatedFieldAnnotators, "java.util.Map.of()");
    }

    public JavaType jsonProperty() {
        var updatedFieldAnnotators = new TreeSet<>(fieldAnnotators);
        updatedFieldAnnotators.add(JavaFieldAnnotator.JSON_PROPERTY);
        return new JavaType(fullName, definition, updatedFieldAnnotators, defaultIfNull);
    }

    public JavaType jsonUnwrapped() {
        var updatedFieldAnnotators = new TreeSet<>(fieldAnnotators);
        updatedFieldAnnotators.add(JavaFieldAnnotator.JSON_UNWRAPPED);
        return new JavaType(fullName, definition, updatedFieldAnnotators, defaultIfNull);
    }

}
