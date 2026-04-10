package jopenapi.test.arrays;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import java.util.Collections;

public record UniqueItemsArrayWithMinMax(@JsonValue() java.util.Set<Integer> value) {

    @JsonCreator()
    public UniqueItemsArrayWithMinMax {
        value = value == null ? java.util.Set.of() : java.util.Collections.unmodifiableSet(value);
    }
}
