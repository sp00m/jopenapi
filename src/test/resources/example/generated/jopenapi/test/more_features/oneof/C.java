package jopenapi.test.more_features.oneof;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonUnwrapped;
import lombok.Builder;
import lombok.With;
import lombok.extern.jackson.Jacksonized;

@Jacksonized()
@With()
@Builder(toBuilder = true)
public record C(@JsonProperty(value = "c") boolean c, @JsonUnwrapped() jopenapi.test.more_features.oneof.CommonBc commonBc) implements jopenapi.test.more_features.oneof.OneOfBc {
}
