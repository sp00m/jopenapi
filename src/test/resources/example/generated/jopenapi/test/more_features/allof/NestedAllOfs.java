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
        public static OnlyRefs create(@JsonUnwrapped() jopenapi.test.more_features.allof.LocalCommonObject localCommonObject, @JsonUnwrapped() jopenapi.test.common.CommonObject commonObject) {
            if (localCommonObject == null) {
                throw new IllegalArgumentException("Property 'localCommonObject' is required");
            }
            if (commonObject == null) {
                throw new IllegalArgumentException("Property 'commonObject' is required");
            }
            return new OnlyRefs(localCommonObject, commonObject);
        }
    }

    @With()
    @Builder(toBuilder = true)
    public record OneCustomObject(@JsonProperty(value = "active") boolean active, @JsonUnwrapped() jopenapi.test.more_features.allof.LocalCommonObject localCommonObject) {

        @JsonCreator()
        public static OneCustomObject create(@JsonProperty(value = "active") Boolean active, @JsonUnwrapped() jopenapi.test.more_features.allof.LocalCommonObject localCommonObject) {
            if (active == null) {
                throw new IllegalArgumentException("Property 'active' is required");
            }
            if (localCommonObject == null) {
                throw new IllegalArgumentException("Property 'localCommonObject' is required");
            }
            return new OneCustomObject(active, localCommonObject);
        }
    }

    @JsonCreator()
    public static NestedAllOfs create(@JsonProperty(value = "only_refs") OnlyRefs onlyRefs, @JsonProperty(value = "one_custom_object") OneCustomObject oneCustomObject) {
        if (onlyRefs == null) {
            throw new IllegalArgumentException("Property 'only_refs' is required");
        }
        if (oneCustomObject == null) {
            throw new IllegalArgumentException("Property 'one_custom_object' is required");
        }
        return new NestedAllOfs(onlyRefs, oneCustomObject);
    }
}
