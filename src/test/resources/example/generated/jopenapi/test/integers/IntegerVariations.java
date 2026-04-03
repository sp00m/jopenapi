package jopenapi.test.integers;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.Optional;
import lombok.Builder;
import lombok.extern.jackson.Jacksonized;

@Jacksonized()
@Builder(toBuilder = true)
public record IntegerVariations(@JsonProperty(value = "integer_with_min_inclusive") int integerWithMinInclusive, @JsonProperty(value = "integer_with_min_exclusive") int integerWithMinExclusive, @JsonProperty(value = "integer_with_max_inclusive") int integerWithMaxInclusive, @JsonProperty(value = "integer_with_max_exclusive") int integerWithMaxExclusive, @JsonProperty(value = "integer_with_min_max") int integerWithMinMax, @JsonProperty(value = "mandatory_integer") int mandatoryInteger, @JsonProperty(value = "optional_integer") Optional<Integer> optionalInteger, @JsonProperty(value = "mandatory_long") long mandatoryLong, @JsonProperty(value = "optional_long") Optional<Long> optionalLong) {

    public IntegerVariations {
        optionalInteger = optionalInteger == null ? Optional.empty() : optionalInteger;
        optionalLong = optionalLong == null ? Optional.empty() : optionalLong;
    }
}
