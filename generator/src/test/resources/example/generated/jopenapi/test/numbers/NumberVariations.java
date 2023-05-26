package jopenapi.test.numbers;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import java.util.Optional;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.Value;
import lombok.extern.jackson.Jacksonized;

@Value()
@Jacksonized()
@Getter(AccessLevel.NONE)
@Builder(toBuilder = true)
public class NumberVariations {

    @DecimalMin(value = "1", inclusive = true)
    @JsonProperty(value = "number_with_min_inclusive", access = JsonProperty.Access.AUTO)
    @NotNull()
    Number numberWithMinInclusive;

    public Number getNumberWithMinInclusive() {
        return numberWithMinInclusive;
    }

    @DecimalMin(value = "1", inclusive = false)
    @JsonProperty(value = "number_with_min_exclusive", access = JsonProperty.Access.AUTO)
    @NotNull()
    Number numberWithMinExclusive;

    public Number getNumberWithMinExclusive() {
        return numberWithMinExclusive;
    }

    @DecimalMax(value = "5", inclusive = true)
    @JsonProperty(value = "number_with_max_inclusive", access = JsonProperty.Access.AUTO)
    @NotNull()
    Number numberWithMaxInclusive;

    public Number getNumberWithMaxInclusive() {
        return numberWithMaxInclusive;
    }

    @DecimalMax(value = "5", inclusive = false)
    @JsonProperty(value = "number_with_max_exclusive", access = JsonProperty.Access.AUTO)
    @NotNull()
    Number numberWithMaxExclusive;

    public Number getNumberWithMaxExclusive() {
        return numberWithMaxExclusive;
    }

    @DecimalMin(value = "1", inclusive = true)
    @DecimalMax(value = "5", inclusive = true)
    @JsonProperty(value = "number_with_min_max", access = JsonProperty.Access.AUTO)
    @NotNull()
    Number numberWithMinMax;

    public Number getNumberWithMinMax() {
        return numberWithMinMax;
    }

    @JsonProperty(value = "mandatory_float", access = JsonProperty.Access.AUTO)
    @NotNull()
    Float mandatoryFloat;

    public float getMandatoryFloat() {
        return mandatoryFloat;
    }

    @JsonProperty(value = "optional_float", access = JsonProperty.Access.AUTO)
    Float optionalFloat;

    public Optional<Float> getOptionalFloat() {
        return Optional.ofNullable(optionalFloat);
    }

    @JsonProperty(value = "mandatory_double", access = JsonProperty.Access.AUTO)
    @NotNull()
    Double mandatoryDouble;

    public double getMandatoryDouble() {
        return mandatoryDouble;
    }

    @JsonProperty(value = "optional_double", access = JsonProperty.Access.AUTO)
    Double optionalDouble;

    public Optional<Double> getOptionalDouble() {
        return Optional.ofNullable(optionalDouble);
    }
}
