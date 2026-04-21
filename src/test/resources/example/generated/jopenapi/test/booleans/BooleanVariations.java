package jopenapi.test.booleans;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.Objects;
import java.util.Optional;
import lombok.Builder;
import lombok.With;

@With()
@Builder(toBuilder = true)
public record BooleanVariations(@JsonProperty(value = "mandatory_boolean") boolean mandatoryBoolean, @JsonProperty(value = "is_prefixed_mandatory_boolean") boolean isPrefixedMandatoryBoolean, @JsonProperty(value = "optional_boolean") Optional<Boolean> optionalBoolean) {

    public BooleanVariations {
        optionalBoolean = Objects.requireNonNullElse(optionalBoolean, Optional.empty());
    }

    @JsonCreator()
    static BooleanVariations create(@JsonProperty(value = "mandatory_boolean") Boolean mandatoryBoolean, @JsonProperty(value = "is_prefixed_mandatory_boolean") Boolean isPrefixedMandatoryBoolean, @JsonProperty(value = "optional_boolean") Boolean optionalBoolean) {
        if (mandatoryBoolean == null) {
            throw new com.github.jopenapi.support.MissingPropertyException("mandatory_boolean");
        }
        if (isPrefixedMandatoryBoolean == null) {
            throw new com.github.jopenapi.support.MissingPropertyException("is_prefixed_mandatory_boolean");
        }
        return new BooleanVariations(mandatoryBoolean, isPrefixedMandatoryBoolean, Optional.ofNullable(optionalBoolean));
    }
}
