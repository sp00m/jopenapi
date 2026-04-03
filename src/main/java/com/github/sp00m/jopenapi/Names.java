package com.github.sp00m.jopenapi;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import javax.lang.model.SourceVersion;
import java.math.BigDecimal;
import java.util.Map;
import java.util.regex.Pattern;
import java.util.stream.Stream;

import static java.util.stream.Collectors.collectingAndThen;
import static java.util.stream.Collectors.joining;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class Names {

    public static String toClassName(String name) {
        return toWords(name)
                .map(string -> string.substring(0, 1).toUpperCase() + string.substring(1))
                .collect(collectingAndThen(joining(), Names::ensureValid));
    }

    public static String toFieldName(String name) {
        var className = toClassName(name);
        var output = className.substring(0, 1).toLowerCase() + className.substring(1);
        return ensureValid(output);
    }

    public static String toEnumValue(String name) {
        return toWords(name)
                .map(String::toUpperCase)
                .collect(collectingAndThen(joining("_"), Names::ensureValid));
    }

    public static String toPackageName(String name) {
        return toWords(name.replaceAll("\\.yml$", ""))
                .map(String::toLowerCase)
                .collect(collectingAndThen(joining("_"), Names::ensureValid));
    }

    private static String ensureValid(String name) {
        return SourceVersion.isName(name) ? name : name + "Value";
    }

    private static Stream<String> toWords(String name) {
        try {
            new BigDecimal(name);
            return name
                    .chars()
                    .mapToObj(i -> (char) i)
                    .map(NUMBER_CHARS::get);
        } catch (NumberFormatException e) {
            var words = Stream
                    .of(clean(name).split("([_\\W]|(?<=[a-z])(?=[A-Z]))", -1))
                    .filter(word -> !word.isBlank())
                    .map(String::toLowerCase)
                    .toList();
            return words.isEmpty()
                    ? Stream.of("unknown")
                    : words.stream();
        }
    }

    private static String clean(String name) {
        if (name.length() == 1) {
            if (name.matches("[a-zA-Z]")) {
                return name;
            }
            return NUMBER_CHARS.getOrDefault(name.charAt(0), Character.getName(name.charAt(0)));
        }
        var cleaned = name.replaceFirst("^[_\\W]+", "");
        var matcher = Pattern.compile("^(\\d+)(.*)").matcher(cleaned);
        if (matcher.find()) {
            var replacement = matcher
                    .group(1)
                    .chars()
                    .mapToObj(i -> (char) i)
                    .map(NUMBER_CHARS::get)
                    .collect(joining(" "));
            cleaned = matcher.replaceFirst(replacement + " $2");
        }
        return cleaned;
    }

    private static final Map<Character, String> NUMBER_CHARS = Map.ofEntries(
            Map.entry('0', "zero"),
            Map.entry('1', "one"),
            Map.entry('2', "two"),
            Map.entry('3', "three"),
            Map.entry('4', "four"),
            Map.entry('5', "five"),
            Map.entry('6', "six"),
            Map.entry('7', "seven"),
            Map.entry('8', "eight"),
            Map.entry('9', "nine"),
            Map.entry('+', "plus"),
            Map.entry('-', "minus"),
            Map.entry('.', "point"),
            Map.entry('E', "e")
    );

}
