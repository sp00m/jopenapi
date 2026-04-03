package jopenapi.test.more_features.allof;

import com.fasterxml.jackson.annotation.JsonUnwrapped;
import lombok.Builder;
import lombok.extern.jackson.Jacksonized;

@Jacksonized()
@Builder(toBuilder = true)
public record AllOfWithOnlyRefs(@JsonUnwrapped() jopenapi.test.more_features.allof.LocalCommonObject localCommonObject, @JsonUnwrapped() jopenapi.test.common.CommonObject commonObject) {
}
