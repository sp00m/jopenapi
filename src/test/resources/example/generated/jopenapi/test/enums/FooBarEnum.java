package jopenapi.test.enums;

import com.fasterxml.jackson.annotation.*;
import com.github.jopenapi.support.*;
import jakarta.validation.constraints.*;
import java.math.*;
import java.net.*;
import java.time.*;
import java.util.*;
import java.util.function.*;
import java.util.stream.*;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor()
public enum FooBarEnum {

    FOO("foo"), BAR("bar");

    private static final Map<String, FooBarEnum> BY_VALUE = Stream.of(values()).collect(Collectors.toUnmodifiableMap(FooBarEnum::value, Function.identity()));

    private final String value;

    @JsonValue()
    public String value() {
        return value;
    }

    @JsonCreator()
    public static FooBarEnum findByValue(String value) {
        return Optional.ofNullable(value).map(BY_VALUE::get).orElseThrow(() -> new InvalidPropertyException("FooBarEnum", value));
    }
}
