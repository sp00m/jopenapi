package jopenapi.test.maps;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import java.util.Collections;

public record MapWithMinMax(@JsonValue() java.util.Map<String, Integer> value) {

    @JsonCreator()
    public MapWithMinMax {
        value = value == null ? java.util.Collections.emptyMap() : java.util.Collections.unmodifiableMap(value);
    }
}
