package jopenapi.test.integers;

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
public record IntegerVariations(@DecimalMin(value = "1", inclusive = true) @JsonProperty(value = "integer_with_min_inclusive") int integerWithMinInclusive, @DecimalMin(value = "1", inclusive = false) @JsonProperty(value = "integer_with_min_exclusive") int integerWithMinExclusive, @DecimalMax(value = "5", inclusive = true) @JsonProperty(value = "integer_with_max_inclusive") int integerWithMaxInclusive, @DecimalMax(value = "5", inclusive = false) @JsonProperty(value = "integer_with_max_exclusive") int integerWithMaxExclusive, @DecimalMin(value = "1", inclusive = true) @DecimalMax(value = "5", inclusive = true) @JsonProperty(value = "integer_with_min_max") int integerWithMinMax, @JsonProperty(value = "mandatory_integer") int mandatoryInteger, @JsonProperty(value = "optional_integer") Optional<Integer> optionalInteger, @JsonProperty(value = "mandatory_long") long mandatoryLong, @JsonProperty(value = "optional_long") Optional<Long> optionalLong) {

    public IntegerVariations {
        optionalInteger = Objects.requireNonNullElse(optionalInteger, Optional.empty());
        optionalLong = Objects.requireNonNullElse(optionalLong, Optional.empty());
    }

    @JsonCreator()
    public static IntegerVariations create(@JsonProperty(value = "integer_with_min_inclusive") Integer integerWithMinInclusive, @JsonProperty(value = "integer_with_min_exclusive") Integer integerWithMinExclusive, @JsonProperty(value = "integer_with_max_inclusive") Integer integerWithMaxInclusive, @JsonProperty(value = "integer_with_max_exclusive") Integer integerWithMaxExclusive, @JsonProperty(value = "integer_with_min_max") Integer integerWithMinMax, @JsonProperty(value = "mandatory_integer") Integer mandatoryInteger, @JsonProperty(value = "optional_integer") Integer optionalInteger, @JsonProperty(value = "mandatory_long") Long mandatoryLong, @JsonProperty(value = "optional_long") Long optionalLong) {
        if (integerWithMinInclusive == null) {
            throw new IllegalArgumentException("Property 'integer_with_min_inclusive' is required");
        }
        if (integerWithMinExclusive == null) {
            throw new IllegalArgumentException("Property 'integer_with_min_exclusive' is required");
        }
        if (integerWithMaxInclusive == null) {
            throw new IllegalArgumentException("Property 'integer_with_max_inclusive' is required");
        }
        if (integerWithMaxExclusive == null) {
            throw new IllegalArgumentException("Property 'integer_with_max_exclusive' is required");
        }
        if (integerWithMinMax == null) {
            throw new IllegalArgumentException("Property 'integer_with_min_max' is required");
        }
        if (mandatoryInteger == null) {
            throw new IllegalArgumentException("Property 'mandatory_integer' is required");
        }
        if (mandatoryLong == null) {
            throw new IllegalArgumentException("Property 'mandatory_long' is required");
        }
        return new IntegerVariations(integerWithMinInclusive, integerWithMinExclusive, integerWithMaxInclusive, integerWithMaxExclusive, integerWithMinMax, mandatoryInteger, Optional.ofNullable(optionalInteger), mandatoryLong, Optional.ofNullable(optionalLong));
    }
}
