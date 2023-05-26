package jopenapi.test.integers;

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
public class IntegerVariations {

    @DecimalMin(value = "1", inclusive = true)
    @JsonProperty(value = "integer_with_min_inclusive", access = JsonProperty.Access.AUTO)
    @NotNull()
    Integer integerWithMinInclusive;

    public int getIntegerWithMinInclusive() {
        return integerWithMinInclusive;
    }

    @DecimalMin(value = "1", inclusive = false)
    @JsonProperty(value = "integer_with_min_exclusive", access = JsonProperty.Access.AUTO)
    @NotNull()
    Integer integerWithMinExclusive;

    public int getIntegerWithMinExclusive() {
        return integerWithMinExclusive;
    }

    @DecimalMax(value = "5", inclusive = true)
    @JsonProperty(value = "integer_with_max_inclusive", access = JsonProperty.Access.AUTO)
    @NotNull()
    Integer integerWithMaxInclusive;

    public int getIntegerWithMaxInclusive() {
        return integerWithMaxInclusive;
    }

    @DecimalMax(value = "5", inclusive = false)
    @JsonProperty(value = "integer_with_max_exclusive", access = JsonProperty.Access.AUTO)
    @NotNull()
    Integer integerWithMaxExclusive;

    public int getIntegerWithMaxExclusive() {
        return integerWithMaxExclusive;
    }

    @DecimalMin(value = "1", inclusive = true)
    @DecimalMax(value = "5", inclusive = true)
    @JsonProperty(value = "integer_with_min_max", access = JsonProperty.Access.AUTO)
    @NotNull()
    Integer integerWithMinMax;

    public int getIntegerWithMinMax() {
        return integerWithMinMax;
    }

    @JsonProperty(value = "mandatory_integer", access = JsonProperty.Access.AUTO)
    @NotNull()
    Integer mandatoryInteger;

    public int getMandatoryInteger() {
        return mandatoryInteger;
    }

    @JsonProperty(value = "optional_integer", access = JsonProperty.Access.AUTO)
    Integer optionalInteger;

    public Optional<Integer> getOptionalInteger() {
        return Optional.ofNullable(optionalInteger);
    }

    @JsonProperty(value = "mandatory_long", access = JsonProperty.Access.AUTO)
    @NotNull()
    Long mandatoryLong;

    public long getMandatoryLong() {
        return mandatoryLong;
    }

    @JsonProperty(value = "optional_long", access = JsonProperty.Access.AUTO)
    Long optionalLong;

    public Optional<Long> getOptionalLong() {
        return Optional.ofNullable(optionalLong);
    }
}
