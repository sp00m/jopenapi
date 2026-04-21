package jopenapi.test.more_features.oneof;

import com.fasterxml.jackson.annotation.*;
import com.github.jopenapi.support.*;
import jakarta.validation.constraints.*;
import java.util.*;
import java.util.function.*;
import java.util.stream.*;
import lombok.Builder;
import lombok.With;

@With()
@Builder(toBuilder = true)
public record B(@JsonProperty(value = "b") boolean b, @JsonUnwrapped() jopenapi.test.more_features.oneof.CommonAb commonAb, @JsonUnwrapped() jopenapi.test.more_features.oneof.CommonBc commonBc) implements jopenapi.test.more_features.oneof.OneOfAb, jopenapi.test.more_features.oneof.OneOfBc {

    @JsonCreator()
    static B create(@JsonProperty(value = "b") Boolean b, @JsonUnwrapped() jopenapi.test.more_features.oneof.CommonAb commonAb, @JsonUnwrapped() jopenapi.test.more_features.oneof.CommonBc commonBc) {
        if (b == null) {
            throw new MissingPropertyException("b");
        }
        if (commonAb == null) {
            throw new MissingPropertyException("commonAb");
        }
        if (commonBc == null) {
            throw new MissingPropertyException("commonBc");
        }
        return new B(b, commonAb, commonBc);
    }
}
