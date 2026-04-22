package jopenapi.test.integers;

import com.fasterxml.jackson.annotation.*;
import com.github.jopenapi.support.*;
import jakarta.validation.constraints.*;
import java.math.*;
import java.net.*;
import java.time.*;
import java.util.*;
import java.util.function.*;
import java.util.stream.*;
import lombok.Builder;
import lombok.With;

@With()
@Builder(toBuilder = true)
public record IntegerVariations(@DecimalMin(value = "1", inclusive = true) @JsonProperty(value = "integer_with_min_inclusive") int integerWithMinInclusive, @DecimalMin(value = "1", inclusive = false) @JsonProperty(value = "integer_with_min_exclusive") int integerWithMinExclusive, @DecimalMax(value = "5", inclusive = true) @JsonProperty(value = "integer_with_max_inclusive") int integerWithMaxInclusive, @DecimalMax(value = "5", inclusive = false) @JsonProperty(value = "integer_with_max_exclusive") int integerWithMaxExclusive, @DecimalMin(value = "1", inclusive = true) @DecimalMax(value = "5", inclusive = true) @JsonProperty(value = "integer_with_min_max") int integerWithMinMax, @JsonProperty(value = "mandatory_integer") int mandatoryInteger, @JsonProperty(value = "optional_integer") Optional<Integer> optionalInteger, @JsonProperty(value = "mandatory_long") long mandatoryLong, @JsonProperty(value = "optional_long") Optional<Long> optionalLong) {

    public IntegerVariations {
        optionalInteger = Objects.requireNonNullElse(optionalInteger, Optional.empty());
        optionalLong = Objects.requireNonNullElse(optionalLong, Optional.empty());
    }

    @JsonCreator()
    static IntegerVariations create(@JsonProperty(value = "integer_with_min_inclusive") Integer integerWithMinInclusive, @JsonProperty(value = "integer_with_min_exclusive") Integer integerWithMinExclusive, @JsonProperty(value = "integer_with_max_inclusive") Integer integerWithMaxInclusive, @JsonProperty(value = "integer_with_max_exclusive") Integer integerWithMaxExclusive, @JsonProperty(value = "integer_with_min_max") Integer integerWithMinMax, @JsonProperty(value = "mandatory_integer") Integer mandatoryInteger, @JsonProperty(value = "optional_integer") Integer optionalInteger, @JsonProperty(value = "mandatory_long") Long mandatoryLong, @JsonProperty(value = "optional_long") Long optionalLong) {
        if (integerWithMinInclusive == null) {
            throw new MissingPropertyException("integer_with_min_inclusive");
        }
        if (integerWithMinExclusive == null) {
            throw new MissingPropertyException("integer_with_min_exclusive");
        }
        if (integerWithMaxInclusive == null) {
            throw new MissingPropertyException("integer_with_max_inclusive");
        }
        if (integerWithMaxExclusive == null) {
            throw new MissingPropertyException("integer_with_max_exclusive");
        }
        if (integerWithMinMax == null) {
            throw new MissingPropertyException("integer_with_min_max");
        }
        if (mandatoryInteger == null) {
            throw new MissingPropertyException("mandatory_integer");
        }
        if (mandatoryLong == null) {
            throw new MissingPropertyException("mandatory_long");
        }
        return new IntegerVariations(integerWithMinInclusive, integerWithMinExclusive, integerWithMaxInclusive, integerWithMaxExclusive, integerWithMinMax, mandatoryInteger, Optional.ofNullable(optionalInteger), mandatoryLong, Optional.ofNullable(optionalLong));
    }
}
