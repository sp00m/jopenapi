package jopenapi.test.numbers;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import java.util.Objects;
import java.util.Optional;
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
    public static NumberVariations create(@JsonProperty(value = "number_with_min_inclusive") Number numberWithMinInclusive, @JsonProperty(value = "number_with_min_exclusive") Number numberWithMinExclusive, @JsonProperty(value = "number_with_max_inclusive") Number numberWithMaxInclusive, @JsonProperty(value = "number_with_max_exclusive") Number numberWithMaxExclusive, @JsonProperty(value = "number_with_min_max") Number numberWithMinMax, @JsonProperty(value = "mandatory_float") Float mandatoryFloat, @JsonProperty(value = "optional_float") Float optionalFloat, @JsonProperty(value = "mandatory_double") Double mandatoryDouble, @JsonProperty(value = "optional_double") Double optionalDouble) {
        if (numberWithMinInclusive == null) {
            throw new IllegalArgumentException("Property 'number_with_min_inclusive' is required");
        }
        if (numberWithMinExclusive == null) {
            throw new IllegalArgumentException("Property 'number_with_min_exclusive' is required");
        }
        if (numberWithMaxInclusive == null) {
            throw new IllegalArgumentException("Property 'number_with_max_inclusive' is required");
        }
        if (numberWithMaxExclusive == null) {
            throw new IllegalArgumentException("Property 'number_with_max_exclusive' is required");
        }
        if (numberWithMinMax == null) {
            throw new IllegalArgumentException("Property 'number_with_min_max' is required");
        }
        if (mandatoryFloat == null) {
            throw new IllegalArgumentException("Property 'mandatory_float' is required");
        }
        if (mandatoryDouble == null) {
            throw new IllegalArgumentException("Property 'mandatory_double' is required");
        }
        return new NumberVariations(numberWithMinInclusive, numberWithMinExclusive, numberWithMaxInclusive, numberWithMaxExclusive, numberWithMinMax, mandatoryFloat, Optional.ofNullable(optionalFloat), mandatoryDouble, Optional.ofNullable(optionalDouble));
    }
}
