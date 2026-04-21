package jopenapi.test.more_features.oneof;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonUnwrapped;
import lombok.Builder;
import lombok.With;

@With()
@Builder(toBuilder = true)
public record C(@JsonProperty(value = "c") boolean c, @JsonUnwrapped() jopenapi.test.more_features.oneof.CommonBc commonBc) implements jopenapi.test.more_features.oneof.OneOfBc {

    @JsonCreator()
    static C create(@JsonProperty(value = "c") Boolean c, @JsonUnwrapped() jopenapi.test.more_features.oneof.CommonBc commonBc) {
        if (c == null) {
            throw new com.github.jopenapi.support.MissingPropertyException("c");
        }
        if (commonBc == null) {
            throw new com.github.jopenapi.support.MissingPropertyException("commonBc");
        }
        return new C(c, commonBc);
    }
}
