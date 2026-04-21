package jopenapi.test.numbers;

import com.fasterxml.jackson.annotation.*;
import com.github.jopenapi.support.*;
import jakarta.validation.constraints.*;
import java.util.*;
import java.util.function.*;
import java.util.stream.*;

public record SimpleNumber(@JsonValue() Number value) {

    @JsonCreator()
    public SimpleNumber {
    }
}
