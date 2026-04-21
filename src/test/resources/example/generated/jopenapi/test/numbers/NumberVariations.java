package jopenapi.test.numbers;

import com.fasterxml.jackson.annotation.*;
import com.github.jopenapi.support.*;
import jakarta.validation.constraints.*;
import java.util.*;
import java.util.function.*;
import java.util.stream.*;
import lombok.Builder;
import lombok.With;

@With()
@Builder(toBuilder = true)
public record NumberVariations(@DecimalMin(value = "1", inclusive = true) @JsonProperty(value = "number_with_min_inclusive") Number numberWithMinInclusive, @DecimalMin(value = "1", inclusive = false) @JsonProperty(value = "number_with_min_exclusive") Number numberWithMinExclusive, @DecimalMax(value = "5", inclusive = true) @JsonProperty(value = "number_with_max_inclusive") Number numberWithMaxInclusive, @DecimalMax(value = "5", inclusive = false) @JsonProperty(value = "number_with_max_exclusive") Number numberWithMaxExclusive, @DecimalMin(value = "1", inclusive = true) @DecimalMax(value = "5", inclusive = true) @JsonProperty(value = "number_with_min_max") Number numberWithMinMax, @JsonProperty(value = "mandatory_float") float mandatoryFloat, @JsonProperty(value = "optional_float") Optional<Float> optionalFloat, @JsonProperty(value = "mandatory_double") double mandatoryDouble, @JsonProperty(value = "optional_double") Optional<Double> optionalDouble) {

    public NumberVariations {
        optionalFloat = Objects.requireNonNullElse(optionalFloat, Optional.empty());
        optionalDouble = Objects.requireNonNullElse(optionalDouble, Optional.empty());
    }

    @JsonCreator()
    static NumberVariations create(@JsonProperty(value = "number_with_min_inclusive") Number numberWithMinInclusive, @JsonProperty(value = "number_with_min_exclusive") Number numberWithMinExclusive, @JsonProperty(value = "number_with_max_inclusive") Number numberWithMaxInclusive, @JsonProperty(value = "number_with_max_exclusive") Number numberWithMaxExclusive, @JsonProperty(value = "number_with_min_max") Number numberWithMinMax, @JsonProperty(value = "mandatory_float") Float mandatoryFloat, @JsonProperty(value = "optional_float") Float optionalFloat, @JsonProperty(value = "mandatory_double") Double mandatoryDouble, @JsonProperty(value = "optional_double") Double optionalDouble) {
        if (numberWithMinInclusive == null) {
            throw new MissingPropertyException("number_with_min_inclusive");
        }
        if (numberWithMinExclusive == null) {
            throw new MissingPropertyException("number_with_min_exclusive");
        }
        if (numberWithMaxInclusive == null) {
            throw new MissingPropertyException("number_with_max_inclusive");
        }
        if (numberWithMaxExclusive == null) {
            throw new MissingPropertyException("number_with_max_exclusive");
        }
        if (numberWithMinMax == null) {
            throw new MissingPropertyException("number_with_min_max");
        }
        if (mandatoryFloat == null) {
            throw new MissingPropertyException("mandatory_float");
        }
        if (mandatoryDouble == null) {
            throw new MissingPropertyException("mandatory_double");
        }
        return new NumberVariations(numberWithMinInclusive, numberWithMinExclusive, numberWithMaxInclusive, numberWithMaxExclusive, numberWithMinMax, mandatoryFloat, Optional.ofNullable(optionalFloat), mandatoryDouble, Optional.ofNullable(optionalDouble));
    }
}
