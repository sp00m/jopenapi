package com.github.sp00m.jopenapi.read.vo;

import com.github.sp00m.jopenapi.read.JavaFieldAnnotator;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Value;

import java.util.Set;
import java.util.TreeSet;
import java.util.function.UnaryOperator;

@Value
public class JavaType {

    String fullName;
    JavaTypeDefinition definition;
    Set<JavaFieldAnnotator> fieldAnnotators;
    String defaultValue;
    @Getter(AccessLevel.NONE)
    UnaryOperator<String> defaultValueDecorator;
    boolean wrapped;
    String description;

    public String getDefaultValue() {
        return defaultValue == null ? null : defaultValueDecorator.apply(defaultValue);
    }

    private JavaType(String fullName, JavaTypeDefinition definition, Set<JavaFieldAnnotator> fieldAnnotators, String defaultValue, UnaryOperator<String> defaultValueDecorator, boolean wrapped, String description) {
        this.fullName = fullName.replaceFirst("^java\\.lang\\.([^.]+)$", "$1");
        this.definition = definition;
        this.fieldAnnotators = fieldAnnotators;
        this.defaultValue = defaultValue;
        this.defaultValueDecorator = defaultValueDecorator;
        this.wrapped = wrapped;
        this.description = description;
    }

    public JavaType(String fullName, JavaTypeDefinition definition) {
        this(fullName, definition, Set.of(), null, UnaryOperator.identity(), false, null);
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
        updatedFieldAnnotators.add(JavaFieldAnnotator.MULTIPLE_OF);
        return new JavaType(fullName, definition, updatedFieldAnnotators, defaultValue, defaultValueDecorator, wrapped, description);
    }

    public JavaType string() {
        var updatedFieldAnnotators = new TreeSet<>(fieldAnnotators);
        updatedFieldAnnotators.add(JavaFieldAnnotator.PATTERN);
        updatedFieldAnnotators.add(JavaFieldAnnotator.SIZE);
        return new JavaType(fullName, definition, updatedFieldAnnotators, defaultValue, defaultValueDecorator, wrapped, description);
    }

    public JavaType set() {
        var updatedFullName = "java.util.Set<%s>".formatted(fullName);
        var updatedFieldAnnotators = new TreeSet<>(fieldAnnotators);
        updatedFieldAnnotators.add(JavaFieldAnnotator.SIZE);
        return new JavaType(updatedFullName, definition, updatedFieldAnnotators, "java.util.Set.of()", UnaryOperator.identity(), true, description);
    }

    public JavaType list() {
        var updatedFullName = "java.util.List<%s>".formatted(fullName);
        var updatedFieldAnnotators = new TreeSet<>(fieldAnnotators);
        updatedFieldAnnotators.add(JavaFieldAnnotator.SIZE);
        return new JavaType(updatedFullName, definition, updatedFieldAnnotators, "java.util.List.of()", UnaryOperator.identity(), true, description);
    }

    public JavaType map() {
        var updatedFullName = "java.util.Map<String, %s>".formatted(fullName);
        var updatedFieldAnnotators = new TreeSet<>(fieldAnnotators);
        updatedFieldAnnotators.add(JavaFieldAnnotator.SIZE);
        return new JavaType(updatedFullName, definition, updatedFieldAnnotators, "java.util.Map.of()", UnaryOperator.identity(), true, description);
    }

    public JavaType jsonProperty() {
        var updatedFieldAnnotators = new TreeSet<>(fieldAnnotators);
        updatedFieldAnnotators.add(JavaFieldAnnotator.JSON_PROPERTY);
        return new JavaType(fullName, definition, updatedFieldAnnotators, defaultValue, defaultValueDecorator, wrapped, description);
    }

    public JavaType jsonUnwrapped() {
        var updatedFieldAnnotators = new TreeSet<>(fieldAnnotators);
        updatedFieldAnnotators.add(JavaFieldAnnotator.JSON_UNWRAPPED);
        return new JavaType(fullName, definition, updatedFieldAnnotators, defaultValue, defaultValueDecorator, wrapped, description);
    }

    public JavaType description(String description) {
        return new JavaType(fullName, definition, fieldAnnotators, defaultValue, defaultValueDecorator, wrapped, description);
    }

    public JavaType defaultValue(String defaultValue) {
        return new JavaType(fullName, definition, fieldAnnotators, defaultValue, defaultValueDecorator, wrapped, description);
    }

    public JavaType defaultValueDecorator(UnaryOperator<String> defaultValueDecorator) {
        return new JavaType(fullName, definition, fieldAnnotators, defaultValue, defaultValueDecorator, wrapped, description);
    }

}
