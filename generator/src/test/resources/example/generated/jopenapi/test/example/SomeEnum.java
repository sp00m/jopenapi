package jopenapi.test.example;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor()
public enum SomeEnum {

    VALUE_1("value 1"), VALUE_2("value 2");

    private static final java.util.Map<String, SomeEnum> BY_VALUE = java.util.stream.Stream.of(values()).collect(java.util.stream.Collectors.toUnmodifiableMap(SomeEnum::getValue, java.util.function.Function.identity()));

    private final String value;

    @JsonValue()
    public String get() {
        return value;
    }

    @JsonCreator()
    public static SomeEnum get(String value) {
        return java.util.Optional.ofNullable(BY_VALUE.get(value)).orElseThrow(() -> new IllegalArgumentException("No SomeEnum with value " + value));
    }
}
