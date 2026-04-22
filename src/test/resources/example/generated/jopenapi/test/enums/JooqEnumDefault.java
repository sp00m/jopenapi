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
import org.jooq.*;
import org.jooq.impl.DSL;

@RequiredArgsConstructor()
public enum JooqEnumDefault implements EnumType {

    FOO("foo"), BAR("bar");

    private static final Map<String, JooqEnumDefault> BY_VALUE = Stream.of(values()).collect(Collectors.toUnmodifiableMap(JooqEnumDefault::value, Function.identity()));

    private final String value;

    @JsonValue()
    public String value() {
        return value;
    }

    @JsonCreator()
    public static JooqEnumDefault findByValue(String value) {
        return Optional.ofNullable(value).map(BY_VALUE::get).orElseThrow(() -> new InvalidPropertyException("JooqEnumDefault", value));
    }

    @Override()
    public String getLiteral() {
        return value;
    }

    @Override()
    public String getName() {
        return null;
    }
}
