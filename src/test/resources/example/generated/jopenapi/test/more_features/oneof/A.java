package jopenapi.test.more_features.oneof;

import com.fasterxml.jackson.annotation.*;
import com.github.jopenapi.support.*;
import jakarta.validation.constraints.*;
import java.math.*;
import java.net.*;
import java.time.*;
import java.util.*;
import java.util.function.*;
import java.util.stream.*;
import lombok.Builder;
import lombok.With;

@With()
@Builder(toBuilder = true)
public record A(@JsonProperty(value = "a") boolean a, @JsonUnwrapped() jopenapi.test.more_features.oneof.CommonAb commonAb) implements jopenapi.test.more_features.oneof.OneOfAb {

    @JsonCreator()
    static A create(@JsonProperty(value = "a") Boolean a, @JsonUnwrapped() jopenapi.test.more_features.oneof.CommonAb commonAb) {
        if (a == null) {
            throw new MissingPropertyException("a");
        }
        if (commonAb == null) {
            throw new MissingPropertyException("commonAb");
        }
        return new A(a, commonAb);
    }
}
