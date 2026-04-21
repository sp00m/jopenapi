package jopenapi.test.arrays;

import com.fasterxml.jackson.annotation.*;
import com.github.jopenapi.support.*;
import jakarta.validation.constraints.*;
import java.util.*;
import java.util.function.*;
import java.util.stream.*;

public record SimpleArray(@JsonValue() List<Integer> value) {

    @JsonCreator()
    public SimpleArray {
        value = value == null ? Collections.emptyList() : Collections.unmodifiableList(value);
    }
}
