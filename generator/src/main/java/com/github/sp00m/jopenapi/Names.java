package com.github.sp00m.jopenapi;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.util.stream.Stream;

import static java.util.stream.Collectors.joining;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class Names {

    public static String toClassName(String name) {
        return toWords(name)
                .map(string -> string.substring(0, 1).toUpperCase() + string.substring(1))
                .collect(joining());
    }

    public static String toFieldName(String name) {
        var className = toClassName(name);
        return className.substring(0, 1).toLowerCase() + className.substring(1);
    }

    public static String toEnumValue(String name) {
        String value = toWords(name)
                .map(String::toUpperCase)
                .collect(joining("_"));
        return value.isEmpty() ? "UNKNOWN" : value;
    }

    public static String toPackageName(String name) {
        return toWords(name.replaceAll("\\.yml$", ""))
                .map(String::toLowerCase)
                .collect(joining("_"));
    }

    private static Stream<String> toWords(String name) {
        return Stream
                .of(name.split("([_\\W]|(?>=[a-z])(?=[A-Z]))", -1))
                .filter(word -> !word.isBlank());
    }

}
