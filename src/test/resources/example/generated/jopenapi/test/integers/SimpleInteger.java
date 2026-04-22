package jopenapi.test.integers;

import com.fasterxml.jackson.annotation.*;
import com.github.jopenapi.support.*;
import jakarta.validation.constraints.*;
import java.math.*;
import java.net.*;
import java.time.*;
import java.util.*;
import java.util.function.*;
import java.util.stream.*;

public record SimpleInteger(@JsonValue() int value) {

    @JsonCreator()
    public SimpleInteger {
    }
}
