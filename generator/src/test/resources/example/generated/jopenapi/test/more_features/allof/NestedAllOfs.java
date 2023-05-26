package jopenapi.test.more_features.allof;

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
public class NestedAllOfs {

    @Value()
    @Jacksonized()
    @Getter(AccessLevel.NONE)
    @Builder(toBuilder = true)
    public static class OnlyRefs {

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

    @JsonProperty(value = "only_refs", access = JsonProperty.Access.AUTO)
    @NotNull()
    OnlyRefs onlyRefs;

    public OnlyRefs getOnlyRefs() {
        return onlyRefs;
    }

    @Value()
    @Jacksonized()
    @Getter(AccessLevel.NONE)
    @Builder(toBuilder = true)
    public static class OneCustomObject {

        @JsonProperty(value = "active", access = JsonProperty.Access.AUTO)
        @NotNull()
        Boolean active;

        public boolean isActive() {
            return active;
        }

        @JsonUnwrapped()
        @NotNull()
        jopenapi.test.more_features.allof.LocalCommonObject localCommonObject;

        public jopenapi.test.more_features.allof.LocalCommonObject getLocalCommonObject() {
            return localCommonObject;
        }
    }

    @JsonProperty(value = "one_custom_object", access = JsonProperty.Access.AUTO)
    @NotNull()
    OneCustomObject oneCustomObject;

    public OneCustomObject getOneCustomObject() {
        return oneCustomObject;
    }
}
