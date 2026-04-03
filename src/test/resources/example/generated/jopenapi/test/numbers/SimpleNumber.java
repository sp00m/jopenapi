package jopenapi.test.numbers;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public record SimpleNumber(@JsonValue() Number value) {

    @JsonCreator()
    public static SimpleNumber of(Number value) {
        return new SimpleNumber(value);
    }
}
