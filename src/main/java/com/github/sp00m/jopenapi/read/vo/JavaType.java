package com.github.sp00m.jopenapi.read.vo;

import com.github.sp00m.jopenapi.read.JavaPropertyAnnotator;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Value;
import lombok.experimental.Accessors;

import java.util.Collections;
import java.util.Set;
import java.util.TreeSet;
import java.util.function.UnaryOperator;

@Value
@Accessors(fluent = true)
public class JavaType {

    String fullName;
    JavaTypeDefinition definition;
    Set<JavaPropertyAnnotator> propertyAnnotators;
    @Getter(AccessLevel.NONE)
    String defaultValue;
    @Getter(AccessLevel.NONE)
    UnaryOperator<String> defaultValueDecorator;
    String decoratedDefaultValue;
    UnaryOperator<String> unmodifier;
    boolean collection;
    String description;

    private JavaType(
            String fullName,
            JavaTypeDefinition definition,
            Set<JavaPropertyAnnotator> propertyAnnotators,
            String defaultValue,
            UnaryOperator<String> defaultValueDecorator,
            UnaryOperator<String> unmodifier,
            boolean collection,
            String description
    ) {
        this.fullName = fullName.replaceFirst("^java\\.(?:lang|util|time|net)\\.([^.]+)$", "$1");
        this.definition = definition;
        this.propertyAnnotators = Collections.unmodifiableSet(new TreeSet<>(propertyAnnotators));
        this.defaultValue = defaultValue;
        this.defaultValueDecorator = defaultValueDecorator;
        this.decoratedDefaultValue = defaultValue == null ? null : defaultValueDecorator.apply(defaultValue);
        this.unmodifier = unmodifier;
        this.collection = collection;
        this.description = description;
    }

    public JavaType(String fullName, JavaTypeDefinition definition) {
        this(fullName, definition, Collections.emptySet(), null, UnaryOperator.identity(), UnaryOperator.identity(), false, null);
    }

    public JavaType(String fullName) {
        this(fullName, null);
    }

    public JavaType(Class<?> fullName) {
        this(fullName.getName());
    }

    public JavaType number() {
        var updatedPropertyAnnotators = new TreeSet<>(propertyAnnotators);
        updatedPropertyAnnotators.add(JavaPropertyAnnotator.MIN);
        updatedPropertyAnnotators.add(JavaPropertyAnnotator.MAX);
        updatedPropertyAnnotators.add(JavaPropertyAnnotator.MULTIPLE_OF);
        return new JavaType(fullName, definition, updatedPropertyAnnotators, defaultValue, defaultValueDecorator, unmodifier, collection, description);
    }

    public JavaType string() {
        var updatedPropertyAnnotators = new TreeSet<>(propertyAnnotators);
        updatedPropertyAnnotators.add(JavaPropertyAnnotator.PATTERN);
        updatedPropertyAnnotators.add(JavaPropertyAnnotator.SIZE);
        return new JavaType(fullName, definition, updatedPropertyAnnotators, defaultValue, defaultValueDecorator, unmodifier, collection, description);
    }

    public JavaType set() {
        var updatedFullName = "Set<%s>".formatted(fullName);
        var updatedPropertyAnnotators = new TreeSet<>(propertyAnnotators);
        updatedPropertyAnnotators.add(JavaPropertyAnnotator.SIZE);
        return new JavaType(updatedFullName, definition, updatedPropertyAnnotators, "Collections.emptySet()", UnaryOperator.identity(), "Collections.unmodifiableSet(%s)"::formatted, true, description);
    }

    public JavaType list() {
        var updatedFullName = "List<%s>".formatted(fullName);
        var updatedPropertyAnnotators = new TreeSet<>(propertyAnnotators);
        updatedPropertyAnnotators.add(JavaPropertyAnnotator.SIZE);
        return new JavaType(updatedFullName, definition, updatedPropertyAnnotators, "Collections.emptyList()", UnaryOperator.identity(), "Collections.unmodifiableList(%s)"::formatted, true, description);
    }

    public JavaType map() {
        var updatedFullName = "Map<String, %s>".formatted(fullName);
        var updatedPropertyAnnotators = new TreeSet<>(propertyAnnotators);
        updatedPropertyAnnotators.add(JavaPropertyAnnotator.SIZE);
        return new JavaType(updatedFullName, definition, updatedPropertyAnnotators, "Collections.emptyMap()", UnaryOperator.identity(), "Collections.unmodifiableMap(%s)"::formatted, true, description);
    }

    public JavaType jsonProperty() {
        var updatedPropertyAnnotators = new TreeSet<>(propertyAnnotators);
        updatedPropertyAnnotators.add(JavaPropertyAnnotator.JSON_PROPERTY);
        return new JavaType(fullName, definition, updatedPropertyAnnotators, defaultValue, defaultValueDecorator, unmodifier, collection, description);
    }

    public JavaType jsonUnwrapped() {
        var updatedPropertyAnnotators = new TreeSet<>(propertyAnnotators);
        updatedPropertyAnnotators.add(JavaPropertyAnnotator.JSON_UNWRAPPED);
        return new JavaType(fullName, definition, updatedPropertyAnnotators, defaultValue, defaultValueDecorator, unmodifier, collection, description);
    }

    public JavaType description(String description) {
        return new JavaType(fullName, definition, propertyAnnotators, defaultValue, defaultValueDecorator, unmodifier, collection, description);
    }

    public JavaType defaultValue(String defaultValue) {
        return new JavaType(fullName, definition, propertyAnnotators, defaultValue, defaultValueDecorator, unmodifier, collection, description);
    }

    public JavaType defaultValueDecorator(UnaryOperator<String> defaultValueDecorator) {
        return new JavaType(fullName, definition, propertyAnnotators, defaultValue, defaultValueDecorator, unmodifier, collection, description);
    }

}
