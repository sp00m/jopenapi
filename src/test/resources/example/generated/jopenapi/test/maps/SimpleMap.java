package jopenapi.test.maps;

import com.fasterxml.jackson.annotation.*;
import com.github.jopenapi.support.*;
import jakarta.validation.constraints.*;
import java.math.*;
import java.net.*;
import java.time.*;
import java.util.*;
import java.util.function.*;
import java.util.stream.*;

public record SimpleMap(@JsonValue() Map<String, Integer> value) {

    @JsonCreator()
    public SimpleMap {
        value = value == null ? Collections.emptyMap() : Collections.unmodifiableMap(value);
    }
}
