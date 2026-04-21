package jopenapi.test.more_features.allof;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonUnwrapped;
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
                throw new com.github.jopenapi.support.MissingPropertyException("localCommonObject");
            }
            if (commonObject == null) {
                throw new com.github.jopenapi.support.MissingPropertyException("commonObject");
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
                throw new com.github.jopenapi.support.MissingPropertyException("active");
            }
            if (localCommonObject == null) {
                throw new com.github.jopenapi.support.MissingPropertyException("localCommonObject");
            }
            return new OneCustomObject(active, localCommonObject);
        }
    }

    @JsonCreator()
    static NestedAllOfs create(@JsonProperty(value = "only_refs") OnlyRefs onlyRefs, @JsonProperty(value = "one_custom_object") OneCustomObject oneCustomObject) {
        if (onlyRefs == null) {
            throw new com.github.jopenapi.support.MissingPropertyException("only_refs");
        }
        if (oneCustomObject == null) {
            throw new com.github.jopenapi.support.MissingPropertyException("one_custom_object");
        }
        return new NestedAllOfs(onlyRefs, oneCustomObject);
    }
}
