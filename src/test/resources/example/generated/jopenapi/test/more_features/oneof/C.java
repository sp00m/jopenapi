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
public record C(@JsonProperty(value = "c") boolean c, @JsonUnwrapped() jopenapi.test.more_features.oneof.CommonBc commonBc) implements jopenapi.test.more_features.oneof.OneOfBc {

    @JsonCreator()
    static C create(@JsonProperty(value = "c") Boolean c, @JsonUnwrapped() jopenapi.test.more_features.oneof.CommonBc commonBc) {
        if (c == null) {
            throw new MissingPropertyException("c");
        }
        if (commonBc == null) {
            throw new MissingPropertyException("commonBc");
        }
        return new C(c, commonBc);
    }
}
