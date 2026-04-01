package jopenapi.test.more_features.oneof;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonUnwrapped;
import lombok.Builder;
import lombok.extern.jackson.Jacksonized;

@Jacksonized()
@Builder(toBuilder = true)
public record A(@JsonProperty(value = "a") boolean a,
		@JsonUnwrapped() jopenapi.test.more_features.oneof.CommonAb commonAb)
		implements jopenapi.test.more_features.oneof.OneOfAb {
}
