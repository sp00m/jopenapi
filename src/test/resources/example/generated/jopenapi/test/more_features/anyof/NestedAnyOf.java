package jopenapi.test.more_features.anyof;

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
public record NestedAnyOf() {

    @JsonCreator()
    static NestedAnyOf create() {
        return new NestedAnyOf();
    }
}
