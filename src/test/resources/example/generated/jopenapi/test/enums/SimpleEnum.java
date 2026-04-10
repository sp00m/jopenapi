package jopenapi.test.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor()
public enum SimpleEnum {

    VALUE_1("value 1"), VALUE_2("value 2");

    private static final Map<String, SimpleEnum> BY_VALUE = Stream.of(values()).collect(Collectors.toUnmodifiableMap(SimpleEnum::get, Function.identity()));

    private final String value;

    @JsonValue()
    public String value() {
        return value;
    }

    @JsonCreator()
    public static SimpleEnum findByValue(String value) {
        return Optional.ofNullable(value).map(BY_VALUE::get).orElseThrow(() -> new IllegalArgumentException("No SimpleEnum with value " + value));
    }
}
