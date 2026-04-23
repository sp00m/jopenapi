package com.github.sp00m.jopenapi.read.vo;

import com.github.sp00m.jopenapi.generate.CompilationUnitFactory;
import com.github.sp00m.jopenapi.read.JavaPropertyAnnotator;
import com.github.sp00m.jopenapi.read.OpenApiReader;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Value;
import lombok.experimental.Accessors;

import java.util.Collections;
import java.util.Set;
import java.util.TreeSet;
import java.util.function.UnaryOperator;

/**
 * Immutable type descriptor built during the read phase and consumed by the generate phase.
 *
 * <p>This is intentionally a "fat" value object — it carries everything needed to generate
 * a field declaration: the Java type name, how to wrap/unwrap defaults, which annotations
 * to add, and whether the type is a collection. The fluent builder methods ({@link #list()},
 * {@link #set()}, {@link #map()}, {@link #string()}, {@link #number()}, etc.) return new
 * instances because Lombok's {@code @Value} makes all fields final.
 *
 * <p>Key design decisions:
 * <ul>
 *   <li>{@code defaultValue} stores the <em>raw</em> default (e.g. {@code "2020-06-30"}).
 *       {@code defaultValueDecorator} wraps it into a valid Java expression (e.g.
 *       {@code LocalDate.parse("2020-06-30")}). They are kept separate because the decorator
 *       is set by the type (readString, readEnum) while the raw value comes from the schema
 *       and may be set later (in {@link OpenApiReader#link}).</li>
 *   <li>{@code unmodifier} is only set for collections — it wraps the field value with
 *       {@code Collections.unmodifiableX()} in the compact constructor.</li>
 *   <li>The constructor strips common {@code java.*} package prefixes from the full name
 *       (e.g. {@code java.lang.String → String}) because wildcard imports in
 *       {@link CompilationUnitFactory} already cover them.</li>
 * </ul>
 */
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
