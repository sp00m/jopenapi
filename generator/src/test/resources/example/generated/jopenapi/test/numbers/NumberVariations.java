package jopenapi.test.numbers;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.Optional;
import lombok.Builder;
import lombok.extern.jackson.Jacksonized;

@Jacksonized()
@Builder(toBuilder = true)
public record NumberVariations(@JsonProperty(value = "number_with_min_inclusive") Number numberWithMinInclusive,
		@JsonProperty(value = "number_with_min_exclusive") Number numberWithMinExclusive,
		@JsonProperty(value = "number_with_max_inclusive") Number numberWithMaxInclusive,
		@JsonProperty(value = "number_with_max_exclusive") Number numberWithMaxExclusive,
		@JsonProperty(value = "number_with_min_max") Number numberWithMinMax,
		@JsonProperty(value = "mandatory_float") float mandatoryFloat,
		@JsonProperty(value = "optional_float") Optional<Float> optionalFloat,
		@JsonProperty(value = "mandatory_double") double mandatoryDouble,
		@JsonProperty(value = "optional_double") Optional<Double> optionalDouble) {

	public NumberVariations {
		optionalFloat = optionalFloat == null ? Optional.empty() : optionalFloat;
		optionalDouble = optionalDouble == null ? Optional.empty() : optionalDouble;
	}
}
