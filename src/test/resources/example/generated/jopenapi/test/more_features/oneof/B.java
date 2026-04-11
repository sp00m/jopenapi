package jopenapi.test.more_features.oneof;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonUnwrapped;
import lombok.Builder;
import lombok.With;

@With()
@Builder(toBuilder = true)
public record B(@JsonProperty(value = "b") boolean b, @JsonUnwrapped() jopenapi.test.more_features.oneof.CommonAb commonAb, @JsonUnwrapped() jopenapi.test.more_features.oneof.CommonBc commonBc) implements jopenapi.test.more_features.oneof.OneOfAb, jopenapi.test.more_features.oneof.OneOfBc {

    @JsonCreator()
    public static B create(@JsonProperty(value = "b") Boolean b, @JsonUnwrapped() jopenapi.test.more_features.oneof.CommonAb commonAb, @JsonUnwrapped() jopenapi.test.more_features.oneof.CommonBc commonBc) {
        if (b == null) {
            throw new IllegalArgumentException("Property 'b' is required");
        }
        if (commonAb == null) {
            throw new IllegalArgumentException("Property 'commonAb' is required");
        }
        if (commonBc == null) {
            throw new IllegalArgumentException("Property 'commonBc' is required");
        }
        return new B(b, commonAb, commonBc);
    }
}
