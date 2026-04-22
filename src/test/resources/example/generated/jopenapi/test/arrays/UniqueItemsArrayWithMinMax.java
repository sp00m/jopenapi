package jopenapi.test.arrays;

import com.fasterxml.jackson.annotation.*;
import com.github.jopenapi.support.*;
import jakarta.validation.constraints.*;
import java.math.*;
import java.net.*;
import java.time.*;
import java.util.*;
import java.util.function.*;
import java.util.stream.*;

public record UniqueItemsArrayWithMinMax(@JsonValue() Set<Integer> value) {

    @JsonCreator()
    public UniqueItemsArrayWithMinMax {
        value = value == null ? Collections.emptySet() : Collections.unmodifiableSet(value);
    }
}
