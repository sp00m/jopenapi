package jopenapi.test.more_features.oneof;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonUnwrapped;
import lombok.Builder;
import lombok.extern.jackson.Jacksonized;

@Jacksonized()
@Builder(toBuilder = true)
public record B(@JsonProperty(value = "b") boolean b,
		@JsonUnwrapped() jopenapi.test.more_features.oneof.CommonAb commonAb,
		@JsonUnwrapped() jopenapi.test.more_features.oneof.CommonBc commonBc)
		implements jopenapi.test.more_features.oneof.OneOfAb, jopenapi.test.more_features.oneof.OneOfBc {
}
