package jopenapi.test.arrays;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import java.util.Collections;

public record SimpleArray(@JsonValue() java.util.List<Integer> value) {

    @JsonCreator()
    public SimpleArray {
        value = value == null ? java.util.Collections.emptyList() : java.util.Collections.unmodifiableList(value);
    }
}
