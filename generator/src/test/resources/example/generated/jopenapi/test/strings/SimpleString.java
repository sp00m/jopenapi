package jopenapi.test.strings;

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
public class SimpleString {

    @NotNull()
    String value;

    @JsonValue()
    public String get() {
        return value;
    }

    @JsonCreator()
    public static SimpleString of(String value) {
        return new SimpleString(value);
    }
}
