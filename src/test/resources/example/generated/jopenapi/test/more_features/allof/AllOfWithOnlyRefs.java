package jopenapi.test.more_features.allof;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonUnwrapped;
import lombok.Builder;
import lombok.With;

@With()
@Builder(toBuilder = true)
public record AllOfWithOnlyRefs(@JsonUnwrapped() jopenapi.test.more_features.allof.LocalCommonObject localCommonObject, @JsonUnwrapped() jopenapi.test.common.CommonObject commonObject) {

    @JsonCreator()
    static AllOfWithOnlyRefs create(@JsonUnwrapped() jopenapi.test.more_features.allof.LocalCommonObject localCommonObject, @JsonUnwrapped() jopenapi.test.common.CommonObject commonObject) {
        if (localCommonObject == null) {
            throw new com.github.jopenapi.support.MissingPropertyException("localCommonObject");
        }
        if (commonObject == null) {
            throw new com.github.jopenapi.support.MissingPropertyException("commonObject");
        }
        return new AllOfWithOnlyRefs(localCommonObject, commonObject);
    }
}
