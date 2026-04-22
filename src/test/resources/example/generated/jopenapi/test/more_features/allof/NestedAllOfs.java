package jopenapi.test.more_features.allof;

import com.fasterxml.jackson.annotation.*;
import com.github.jopenapi.support.*;
import jakarta.validation.constraints.*;
import java.math.*;
import java.net.*;
import java.time.*;
import java.util.*;
import java.util.function.*;
import java.util.stream.*;
import lombok.Builder;
import lombok.With;

@With()
@Builder(toBuilder = true)
public record NestedAllOfs(@JsonProperty(value = "only_refs") OnlyRefs onlyRefs, @JsonProperty(value = "one_custom_object") OneCustomObject oneCustomObject) {

    @With()
    @Builder(toBuilder = true)
    public record OnlyRefs(@JsonUnwrapped() jopenapi.test.more_features.allof.LocalCommonObject localCommonObject, @JsonUnwrapped() jopenapi.test.common.CommonObject commonObject) {

        @JsonCreator()
        static OnlyRefs create(@JsonUnwrapped() jopenapi.test.more_features.allof.LocalCommonObject localCommonObject, @JsonUnwrapped() jopenapi.test.common.CommonObject commonObject) {
            if (localCommonObject == null) {
                throw new MissingPropertyException("localCommonObject");
            }
            if (commonObject == null) {
                throw new MissingPropertyException("commonObject");
            }
            return new OnlyRefs(localCommonObject, commonObject);
        }
    }

    @With()
    @Builder(toBuilder = true)
    public record OneCustomObject(@JsonProperty(value = "active") boolean active, @JsonUnwrapped() jopenapi.test.more_features.allof.LocalCommonObject localCommonObject) {

        @JsonCreator()
        static OneCustomObject create(@JsonProperty(value = "active") Boolean active, @JsonUnwrapped() jopenapi.test.more_features.allof.LocalCommonObject localCommonObject) {
            if (active == null) {
                throw new MissingPropertyException("active");
            }
            if (localCommonObject == null) {
                throw new MissingPropertyException("localCommonObject");
            }
            return new OneCustomObject(active, localCommonObject);
        }
    }

    @JsonCreator()
    static NestedAllOfs create(@JsonProperty(value = "only_refs") OnlyRefs onlyRefs, @JsonProperty(value = "one_custom_object") OneCustomObject oneCustomObject) {
        if (onlyRefs == null) {
            throw new MissingPropertyException("only_refs");
        }
        if (oneCustomObject == null) {
            throw new MissingPropertyException("one_custom_object");
        }
        return new NestedAllOfs(onlyRefs, oneCustomObject);
    }
}
