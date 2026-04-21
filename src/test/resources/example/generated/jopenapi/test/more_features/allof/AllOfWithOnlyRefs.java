package jopenapi.test.more_features.allof;

import com.fasterxml.jackson.annotation.*;
import com.github.jopenapi.support.*;
import jakarta.validation.constraints.*;
import java.util.*;
import java.util.function.*;
import java.util.stream.*;
import lombok.Builder;
import lombok.With;

@With()
@Builder(toBuilder = true)
public record AllOfWithOnlyRefs(@JsonUnwrapped() jopenapi.test.more_features.allof.LocalCommonObject localCommonObject, @JsonUnwrapped() jopenapi.test.common.CommonObject commonObject) {

    @JsonCreator()
    static AllOfWithOnlyRefs create(@JsonUnwrapped() jopenapi.test.more_features.allof.LocalCommonObject localCommonObject, @JsonUnwrapped() jopenapi.test.common.CommonObject commonObject) {
        if (localCommonObject == null) {
            throw new MissingPropertyException("localCommonObject");
        }
        if (commonObject == null) {
            throw new MissingPropertyException("commonObject");
        }
        return new AllOfWithOnlyRefs(localCommonObject, commonObject);
    }
}
