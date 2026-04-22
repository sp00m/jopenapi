package jopenapi.test.more_features.refs;

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
import lombok.extern.slf4j.Slf4j;

@RequiredArgsConstructor()
@Slf4j()
public enum LocalEnumWithDefault {

    FOO("foo"), BAR("bar");

    private static final Map<String, LocalEnumWithDefault> BY_VALUE = Stream.of(values()).collect(Collectors.toUnmodifiableMap(LocalEnumWithDefault::get, Function.identity()));

    private final String value;

    @JsonValue()
    public String value() {
        return value;
    }

    @JsonCreator()
    public static LocalEnumWithDefault findByValue(String value) {
        return Optional.ofNullable(value).map(BY_VALUE::get).orElseGet(() -> {
            log.warn("No LocalEnumWithDefault with value {}", value);
            return LocalEnumWithDefault.FOO;
        });
    }
}
