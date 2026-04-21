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
    static B create(@JsonProperty(value = "b") Boolean b, @JsonUnwrapped() jopenapi.test.more_features.oneof.CommonAb commonAb, @JsonUnwrapped() jopenapi.test.more_features.oneof.CommonBc commonBc) {
        if (b == null) {
            throw new com.github.jopenapi.support.MissingPropertyException("b");
        }
        if (commonAb == null) {
            throw new com.github.jopenapi.support.MissingPropertyException("commonAb");
        }
        if (commonBc == null) {
            throw new com.github.jopenapi.support.MissingPropertyException("commonBc");
        }
        return new B(b, commonAb, commonBc);
    }
}
