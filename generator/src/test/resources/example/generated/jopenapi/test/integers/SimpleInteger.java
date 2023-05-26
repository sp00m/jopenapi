package jopenapi.test.integers;

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
public class SimpleInteger {

    @NotNull()
    Integer value;

    @JsonValue()
    public int get() {
        return value;
    }

    @JsonCreator()
    public static SimpleInteger of(Integer value) {
        return new SimpleInteger(value);
    }
}
