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
public class A implements jopenapi.test.more_features.oneof.OneOfAb {

    @JsonProperty(value = "a", access = JsonProperty.Access.AUTO)
    @NotNull()
    Boolean a;

    public boolean isA() {
        return a;
    }

    @JsonUnwrapped()
    @NotNull()
    jopenapi.test.more_features.oneof.CommonAb commonAb;

    public jopenapi.test.more_features.oneof.CommonAb getCommonAb() {
        return commonAb;
    }
}
