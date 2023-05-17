package com.github.sp00m.jopenapi.read;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.util.stream.Stream;

import static java.util.stream.Collectors.joining;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class Names {

    public static String toClassName(String name) {
        return Stream
                .of(toWords(name))
                .map(string -> string.substring(0, 1).toUpperCase() + string.substring(1))
                .collect(joining());
    }

    public static String toFieldName(String name) {
        var className = toClassName(name);
        return className.substring(0, 1).toLowerCase() + className.substring(1);
    }

    public static String toEnumValue(String name) {
        return Stream
                .of(toWords(name))
                .map(String::toUpperCase)
                .collect(joining("_"));
    }

    private static String[] toWords(String name) {
        return name.split("([_\\W]|(?>=[a-z])(?=[A-Z]))", -1);
    }

}
