package jopenapi.test.booleans;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public record SimpleBoolean(@JsonValue() boolean value) {

    @JsonCreator()
    public SimpleBoolean {
    }
}
