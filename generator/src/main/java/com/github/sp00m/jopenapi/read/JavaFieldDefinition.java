package com.github.sp00m.jopenapi.read;

import lombok.Value;

import java.util.Set;
import java.util.TreeSet;

@Value
public class JavaFieldDefinition {

    OpenApiProperty property;
    String name;
    Type type;

    @Value
    public static class Type {

        String rawType;
        JavaTypeDefinition innerType;
        Set<JavaFieldAnnotator> annotators;
        String defaultIfNull;

        private Type(String rawType, JavaTypeDefinition innerType, Set<JavaFieldAnnotator> annotators, String defaultIfNull) {
            this.rawType = rawType;
            this.innerType = innerType;
            this.annotators = annotators;
            this.defaultIfNull = defaultIfNull;
        }

        public Type(String rawType, JavaTypeDefinition innerType) {
            this(rawType, innerType, Set.of(JavaFieldAnnotator.JSON_PROPERTY), null);
        }

        public Type(String rawType) {
            this(rawType, null);
        }

        public Type(Class<?> rawType) {
            this(rawType.getName());
        }

        public Type number() {
            var updatedAnnotators = new TreeSet<>(annotators);
            updatedAnnotators.add(JavaFieldAnnotator.MIN);
            updatedAnnotators.add(JavaFieldAnnotator.MAX);
            return new Type(rawType, innerType, updatedAnnotators, null);
        }

        public Type string() {
            var updatedAnnotators = new TreeSet<>(annotators);
            updatedAnnotators.add(JavaFieldAnnotator.PATTERN);
            updatedAnnotators.add(JavaFieldAnnotator.SIZE);
            return new Type(rawType, innerType, updatedAnnotators, null);
        }

        public Type set() {
            var updatedRawType = "java.util.Set<%s>".formatted(rawType);
            var updatedAnnotators = new TreeSet<>(annotators);
            updatedAnnotators.add(JavaFieldAnnotator.SIZE);
            return new Type(updatedRawType, innerType, updatedAnnotators, "java.util.Set.of()");
        }

        public Type list() {
            var updatedRawType = "java.util.List<%s>".formatted(rawType);
            var updatedAnnotators = new TreeSet<>(annotators);
            updatedAnnotators.add(JavaFieldAnnotator.SIZE);
            return new Type(updatedRawType, innerType, updatedAnnotators, "java.util.List.of()");
        }

    }
}
