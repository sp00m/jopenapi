package jopenapi.test.strings;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public record SimpleString(@JsonValue() String value) {

    @JsonCreator()
    public SimpleString {
    }
}
