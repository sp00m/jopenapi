package jopenapi.test.more_features.allof;

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
public class AllOfWithOnlyRefs {

    @JsonUnwrapped()
    @NotNull()
    jopenapi.test.more_features.allof.LocalCommonObject localCommonObject;

    public jopenapi.test.more_features.allof.LocalCommonObject getLocalCommonObject() {
        return localCommonObject;
    }

    @JsonUnwrapped()
    @NotNull()
    jopenapi.test.common.CommonObject commonObject;

    public jopenapi.test.common.CommonObject getCommonObject() {
        return commonObject;
    }
}
