package jopenapi.test.more_features.refs;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotNull;
import java.util.Optional;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Builder.Default;
import lombok.Getter;
import lombok.Value;
import lombok.extern.jackson.Jacksonized;

@Value()
@Jacksonized()
@Getter(AccessLevel.NONE)
@Builder(toBuilder = true)
public class RefVariations {

    @JsonProperty(value = "mandatory_ref", access = JsonProperty.Access.AUTO)
    @NotNull()
    jopenapi.test.more_features.refs.LocalCommonObject mandatoryRef;

    public jopenapi.test.more_features.refs.LocalCommonObject getMandatoryRef() {
        return mandatoryRef;
    }

    @JsonProperty(value = "optional_ref", access = JsonProperty.Access.AUTO)
    jopenapi.test.more_features.refs.LocalCommonObject optionalRef;

    public Optional<jopenapi.test.more_features.refs.LocalCommonObject> getOptionalRef() {
        return Optional.ofNullable(optionalRef);
    }

    @Default()
    @JsonProperty(value = "ref_array", access = JsonProperty.Access.AUTO)
    @NotNull()
    java.util.List<jopenapi.test.more_features.refs.LocalCommonObject> refArray = java.util.List.of();

    public java.util.List<jopenapi.test.more_features.refs.LocalCommonObject> getRefArray() {
        return refArray;
    }

    @Default()
    @JsonProperty(value = "ref_map", access = JsonProperty.Access.AUTO)
    @NotNull()
    java.util.Map<String, jopenapi.test.more_features.refs.LocalCommonObject> refMap = java.util.Map.of();

    public java.util.Map<String, jopenapi.test.more_features.refs.LocalCommonObject> getRefMap() {
        return refMap;
    }
}
