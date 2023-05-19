package jopenapi.test.example;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Value;

@Value()
@Getter(AccessLevel.NONE)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class SomeNumber {

    @NotNull()
    Number value;

    @JsonValue()
    public Number get() {
        return value;
    }

    @JsonCreator()
    public static SomeNumber of(Number value) {
        return new SomeNumber(value);
    }
}
