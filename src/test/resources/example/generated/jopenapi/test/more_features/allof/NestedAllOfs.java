package jopenapi.test.more_features.allof;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonUnwrapped;
import lombok.Builder;
import lombok.extern.jackson.Jacksonized;

@Jacksonized()
@Builder(toBuilder = true)
public record NestedAllOfs(@JsonProperty(value = "only_refs") OnlyRefs onlyRefs, @JsonProperty(value = "one_custom_object") OneCustomObject oneCustomObject) {

    @Jacksonized()
    @Builder(toBuilder = true)
    public record OnlyRefs(@JsonUnwrapped() jopenapi.test.more_features.allof.LocalCommonObject localCommonObject, @JsonUnwrapped() jopenapi.test.common.CommonObject commonObject) {
    }

    @Jacksonized()
    @Builder(toBuilder = true)
    public record OneCustomObject(@JsonProperty(value = "active") boolean active, @JsonUnwrapped() jopenapi.test.more_features.allof.LocalCommonObject localCommonObject) {
    }
}
