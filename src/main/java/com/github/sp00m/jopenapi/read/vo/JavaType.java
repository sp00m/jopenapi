package com.github.sp00m.jopenapi.read.vo;

import com.github.sp00m.jopenapi.read.JavaFieldAnnotator;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Value;

import java.util.Collections;
import java.util.Set;
import java.util.TreeSet;
import java.util.function.UnaryOperator;

/**
 * Immutable representation of a Java type derived from an OpenAPI schema, carrying all the metadata
 * needed to generate the corresponding Java source code.
 */
@Value
public class JavaType {

    /**
     * Fully qualified Java type name used to declare fields in the generated code
     * (e.g.&nbsp;{@code "String"}, {@code "java.util.List<Integer>"}).
     */
    String fullName;

    /**
     * Optional structural definition of this type (a {@link JavaRecordDefinition},
     * {@link JavaEnumDefinition}, {@link JavaValueRecordDefinition}, or
     * {@link JavaInterfaceDefinition}) used to generate a dedicated Java source file.
     *
     * <p>{@code null} for primitive/simple types (e.g.&nbsp;{@code String}, {@code Integer}) and
     * for OpenAPI {@code $ref} references, whose definitions are read from their own schema
     * components independently.
     */
    JavaTypeDefinition definition;

    /**
     * Set of {@link JavaFieldAnnotator} strategies that will add annotations to the generated
     * record parameter (e.g.&nbsp;{@code @DecimalMin}, {@code @Size}, {@code @Pattern},
     * {@code @JsonProperty}, {@code @JsonUnwrapped}).
     */
    Set<JavaFieldAnnotator> fieldAnnotators;

    /**
     * Raw default value originating from the OpenAPI schema's {@code default} keyword, stored as a
     * plain string (e.g.&nbsp;{@code "hello"}, {@code "42"}, {@code "2024-01-01"}).
     *
     * <p>Use {@link #getDefaultValue()} to obtain the value transformed into valid Java source code
     * through the {@link #defaultValueDecorator}.
     */
    String defaultValue;

    /**
     * Transformation function that converts the raw {@link #defaultValue} into a valid Java source
     * code expression. For example, a string type's decorator wraps the value in quotes
     * ({@code "\"%s\""}), a {@code LocalDate} decorator produces
     * {@code LocalDate.parse("…")}, and a {@code long} decorator appends {@code L}.
     */
    @Getter(AccessLevel.NONE)
    UnaryOperator<String> defaultValueDecorator;

    /**
     * Indicates whether this type is a collection ({@code List}, {@code Set}, or {@code Map}).
     */
    boolean collection;

    /**
     * OpenAPI schema description, used to generate JavaDoc on the corresponding type or field in
     * the generated Java source.
     */
    String description;

    /**
     * Returns the {@link #defaultValue} transformed by the {@link #defaultValueDecorator} into a
     * valid Java source code expression, or {@code null} if no default value is set.
     */
    public String getDefaultValue() {
        return defaultValue == null ? null : defaultValueDecorator.apply(defaultValue);
    }

    private JavaType(
            String fullName,
            JavaTypeDefinition definition,
            Set<JavaFieldAnnotator> fieldAnnotators,
            String defaultValue,
            UnaryOperator<String> defaultValueDecorator,
            boolean collection,
            String description
    ) {
        this.fullName = fullName.replaceFirst("^java\\.lang\\.([^.]+)$", "$1");
        this.definition = definition;
        this.fieldAnnotators = Collections.unmodifiableSet(new TreeSet<>(fieldAnnotators));
        this.defaultValue = defaultValue;
        this.defaultValueDecorator = defaultValueDecorator;
        this.collection = collection;
        this.description = description;
    }

    public JavaType(String fullName, JavaTypeDefinition definition) {
        this(fullName, definition, Collections.emptySet(), null, UnaryOperator.identity(), false, null);
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
        return new JavaType(fullName, definition, updatedFieldAnnotators, defaultValue, defaultValueDecorator, collection, description);
    }

    public JavaType string() {
        var updatedFieldAnnotators = new TreeSet<>(fieldAnnotators);
        updatedFieldAnnotators.add(JavaFieldAnnotator.PATTERN);
        updatedFieldAnnotators.add(JavaFieldAnnotator.SIZE);
        return new JavaType(fullName, definition, updatedFieldAnnotators, defaultValue, defaultValueDecorator, collection, description);
    }

    public JavaType set() {
        var updatedFullName = "java.util.Set<%s>".formatted(fullName);
        var updatedFieldAnnotators = new TreeSet<>(fieldAnnotators);
        updatedFieldAnnotators.add(JavaFieldAnnotator.SIZE);
        return new JavaType(updatedFullName, definition, updatedFieldAnnotators, "java.util.Collections.emptySet()", UnaryOperator.identity(), true, description);
    }

    public JavaType list() {
        var updatedFullName = "java.util.List<%s>".formatted(fullName);
        var updatedFieldAnnotators = new TreeSet<>(fieldAnnotators);
        updatedFieldAnnotators.add(JavaFieldAnnotator.SIZE);
        return new JavaType(updatedFullName, definition, updatedFieldAnnotators, "java.util.Collections.emptyList()", UnaryOperator.identity(), true, description);
    }

    public JavaType map() {
        var updatedFullName = "java.util.Map<String, %s>".formatted(fullName);
        var updatedFieldAnnotators = new TreeSet<>(fieldAnnotators);
        updatedFieldAnnotators.add(JavaFieldAnnotator.SIZE);
        return new JavaType(updatedFullName, definition, updatedFieldAnnotators, "java.util.Collections.emptyMap()", UnaryOperator.identity(), true, description);
    }

    public JavaType jsonProperty() {
        var updatedFieldAnnotators = new TreeSet<>(fieldAnnotators);
        updatedFieldAnnotators.add(JavaFieldAnnotator.JSON_PROPERTY);
        return new JavaType(fullName, definition, updatedFieldAnnotators, defaultValue, defaultValueDecorator, collection, description);
    }

    public JavaType jsonUnwrapped() {
        var updatedFieldAnnotators = new TreeSet<>(fieldAnnotators);
        updatedFieldAnnotators.add(JavaFieldAnnotator.JSON_UNWRAPPED);
        return new JavaType(fullName, definition, updatedFieldAnnotators, defaultValue, defaultValueDecorator, collection, description);
    }

    public JavaType description(String description) {
        return new JavaType(fullName, definition, fieldAnnotators, defaultValue, defaultValueDecorator, collection, description);
    }

    public JavaType defaultValue(String defaultValue) {
        return new JavaType(fullName, definition, fieldAnnotators, defaultValue, defaultValueDecorator, collection, description);
    }

    public JavaType defaultValueDecorator(UnaryOperator<String> defaultValueDecorator) {
        return new JavaType(fullName, definition, fieldAnnotators, defaultValue, defaultValueDecorator, collection, description);
    }

}
