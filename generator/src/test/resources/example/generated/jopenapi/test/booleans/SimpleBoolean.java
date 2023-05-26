package jopenapi.test.booleans;

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
public class SimpleBoolean {

    @NotNull()
    Boolean value;

    @JsonValue()
    public boolean get() {
        return value;
    }

    @JsonCreator()
    public static SimpleBoolean of(Boolean value) {
        return new SimpleBoolean(value);
    }
}
