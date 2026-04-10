package jopenapi.test.more_features.refs;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;
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
