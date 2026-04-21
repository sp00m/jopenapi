package jopenapi.test.more_features.oneof;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonUnwrapped;
import lombok.Builder;
import lombok.With;

@With()
@Builder(toBuilder = true)
public record A(@JsonProperty(value = "a") boolean a, @JsonUnwrapped() jopenapi.test.more_features.oneof.CommonAb commonAb) implements jopenapi.test.more_features.oneof.OneOfAb {

    @JsonCreator()
    static A create(@JsonProperty(value = "a") Boolean a, @JsonUnwrapped() jopenapi.test.more_features.oneof.CommonAb commonAb) {
        if (a == null) {
            throw new IllegalArgumentException("Property 'a' is required");
        }
        if (commonAb == null) {
            throw new IllegalArgumentException("Property 'commonAb' is required");
        }
        return new A(a, commonAb);
    }
}
