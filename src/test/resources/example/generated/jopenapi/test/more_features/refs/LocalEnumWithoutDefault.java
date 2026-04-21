package jopenapi.test.more_features.refs;

import com.fasterxml.jackson.annotation.*;
import com.github.jopenapi.support.*;
import jakarta.validation.constraints.*;
import java.util.*;
import java.util.function.*;
import java.util.stream.*;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor()
public enum LocalEnumWithoutDefault {

    FOO("foo"), BAR("bar");

    private static final Map<String, LocalEnumWithoutDefault> BY_VALUE = Stream.of(values()).collect(Collectors.toUnmodifiableMap(LocalEnumWithoutDefault::get, Function.identity()));

    private final String value;

    @JsonValue()
    public String value() {
        return value;
    }

    @JsonCreator()
    public static LocalEnumWithoutDefault findByValue(String value) {
        return Optional.ofNullable(value).map(BY_VALUE::get).orElseThrow(() -> new IllegalArgumentException("No LocalEnumWithoutDefault with value " + value));
    }
}
