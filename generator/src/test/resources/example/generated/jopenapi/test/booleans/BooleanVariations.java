package jopenapi.test.booleans;

import com.fasterxml.jackson.annotation.JsonProperty;
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
public class BooleanVariations {

    @JsonProperty(value = "mandatory_boolean", access = JsonProperty.Access.AUTO)
    @NotNull()
    Boolean mandatoryBoolean;

    public boolean isMandatoryBoolean() {
        return mandatoryBoolean;
    }

    @JsonProperty(value = "is_prefixed_mandatory_boolean", access = JsonProperty.Access.AUTO)
    @NotNull()
    Boolean isPrefixedMandatoryBoolean;

    public boolean isPrefixedMandatoryBoolean() {
        return isPrefixedMandatoryBoolean;
    }

    @JsonProperty(value = "optional_boolean", access = JsonProperty.Access.AUTO)
    Boolean optionalBoolean;

    public Optional<Boolean> getOptionalBoolean() {
        return Optional.ofNullable(optionalBoolean);
    }
}
