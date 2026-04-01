package jopenapi.test.booleans;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.Optional;
import lombok.Builder;
import lombok.extern.jackson.Jacksonized;

@Jacksonized()
@Builder(toBuilder = true)
public record BooleanVariations(@JsonProperty(value = "mandatory_boolean") boolean mandatoryBoolean,
		@JsonProperty(value = "is_prefixed_mandatory_boolean") boolean isPrefixedMandatoryBoolean,
		@JsonProperty(value = "optional_boolean") Optional<Boolean> optionalBoolean) {

	public BooleanVariations {
		optionalBoolean = optionalBoolean == null ? Optional.empty() : optionalBoolean;
	}
}
