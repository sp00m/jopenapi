package jopenapi.test.more_features.allof;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonUnwrapped;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.Value;
import lombok.extern.jackson.Jacksonized;

@Value()
@Jacksonized()
@Getter(AccessLevel.NONE)
@Builder(toBuilder = true)
public class AllOfWithOneCustomObject {

    @JsonProperty(value = "active", access = JsonProperty.Access.AUTO)
    @NotNull()
    Boolean active;

    public boolean isActive() {
        return active;
    }

    @JsonUnwrapped()
    @NotNull()
    jopenapi.test.more_features.allof.LocalCommonObject localCommonObject;

    public jopenapi.test.more_features.allof.LocalCommonObject getLocalCommonObject() {
        return localCommonObject;
    }
}
