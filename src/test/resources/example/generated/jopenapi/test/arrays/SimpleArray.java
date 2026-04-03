package jopenapi.test.arrays;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import java.util.Collections;

public record SimpleArray(@JsonValue() java.util.List<Integer> value) {

    public SimpleArray {
        value = value == null ? java.util.List.of() : java.util.Collections.unmodifiableList(value);
    }

    @JsonCreator()
    public static SimpleArray of(java.util.List<Integer> value) {
        return new SimpleArray(value);
    }
}
