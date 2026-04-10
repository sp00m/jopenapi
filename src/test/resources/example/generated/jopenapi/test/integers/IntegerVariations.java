package jopenapi.test.integers;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import java.util.Optional;
import lombok.Builder;
import lombok.With;
import lombok.extern.jackson.Jacksonized;

@Jacksonized()
@With()
@Builder(toBuilder = true)
public record IntegerVariations(@DecimalMin(value = "1", inclusive = true) @JsonProperty(value = "integer_with_min_inclusive") int integerWithMinInclusive, @DecimalMin(value = "1", inclusive = false) @JsonProperty(value = "integer_with_min_exclusive") int integerWithMinExclusive, @DecimalMax(value = "5", inclusive = true) @JsonProperty(value = "integer_with_max_inclusive") int integerWithMaxInclusive, @DecimalMax(value = "5", inclusive = false) @JsonProperty(value = "integer_with_max_exclusive") int integerWithMaxExclusive, @DecimalMin(value = "1", inclusive = true) @DecimalMax(value = "5", inclusive = true) @JsonProperty(value = "integer_with_min_max") int integerWithMinMax, @JsonProperty(value = "mandatory_integer") int mandatoryInteger, @JsonProperty(value = "optional_integer") Optional<Integer> optionalInteger, @JsonProperty(value = "mandatory_long") long mandatoryLong, @JsonProperty(value = "optional_long") Optional<Long> optionalLong) {

    public IntegerVariations {
        optionalInteger = optionalInteger == null ? Optional.empty() : optionalInteger;
        optionalLong = optionalLong == null ? Optional.empty() : optionalLong;
    }
}
