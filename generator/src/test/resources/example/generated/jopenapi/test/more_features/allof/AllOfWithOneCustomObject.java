package jopenapi.test.more_features.allof;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonUnwrapped;
import lombok.Builder;
import lombok.extern.jackson.Jacksonized;

@Jacksonized()
@Builder(toBuilder = true)
public record AllOfWithOneCustomObject(@JsonProperty(value = "active") boolean active,
		@JsonUnwrapped() jopenapi.test.more_features.allof.LocalCommonObject localCommonObject) {
}
