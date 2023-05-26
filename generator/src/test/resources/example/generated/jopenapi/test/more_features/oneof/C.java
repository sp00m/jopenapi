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
public class C implements jopenapi.test.more_features.oneof.OneOfBc {

    @JsonProperty(value = "c", access = JsonProperty.Access.AUTO)
    @NotNull()
    Boolean c;

    public boolean isC() {
        return c;
    }

    @JsonUnwrapped()
    @NotNull()
    jopenapi.test.more_features.oneof.CommonBc commonBc;

    public jopenapi.test.more_features.oneof.CommonBc getCommonBc() {
        return commonBc;
    }
}
