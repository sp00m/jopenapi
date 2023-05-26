package jopenapi.test.more_features.oneof;

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
public class B implements jopenapi.test.more_features.oneof.OneOfAb, jopenapi.test.more_features.oneof.OneOfBc {

    @JsonProperty(value = "b", access = JsonProperty.Access.AUTO)
    @NotNull()
    Boolean b;

    public boolean isB() {
        return b;
    }

    @JsonUnwrapped()
    @NotNull()
    jopenapi.test.more_features.oneof.CommonAb commonAb;

    public jopenapi.test.more_features.oneof.CommonAb getCommonAb() {
        return commonAb;
    }

    @JsonUnwrapped()
    @NotNull()
    jopenapi.test.more_features.oneof.CommonBc commonBc;

    public jopenapi.test.more_features.oneof.CommonBc getCommonBc() {
        return commonBc;
    }
}
