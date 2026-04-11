package jopenapi.test.more_features.refs;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.Collections;
import java.util.Objects;
import java.util.Optional;
import lombok.Builder;
import lombok.With;

@With()
@Builder(toBuilder = true)
public record RefVariations(@JsonProperty(value = "mandatory_ref") jopenapi.test.more_features.refs.LocalCommonObject mandatoryRef, @JsonProperty(value = "optional_ref") Optional<jopenapi.test.more_features.refs.LocalCommonObject> optionalRef, @JsonProperty(value = "ref_array") java.util.List<jopenapi.test.more_features.refs.LocalCommonObject> refArray, @JsonProperty(value = "ref_map") java.util.Map<String, jopenapi.test.more_features.refs.LocalCommonObject> refMap, @JsonProperty(value = "local_enum_without_default") Optional<jopenapi.test.more_features.refs.LocalEnumWithoutDefault> localEnumWithoutDefault, @JsonProperty(value = "local_enum_without_default_defaulted_locally") jopenapi.test.more_features.refs.LocalEnumWithoutDefault localEnumWithoutDefaultDefaultedLocally, @JsonProperty(value = "local_enum_with_default") jopenapi.test.more_features.refs.LocalEnumWithDefault localEnumWithDefault, @JsonProperty(value = "local_enum_with_default_defaulted_locally") jopenapi.test.more_features.refs.LocalEnumWithDefault localEnumWithDefaultDefaultedLocally) {

    public RefVariations {
        optionalRef = Objects.requireNonNullElse(optionalRef, Optional.empty());
        refArray = refArray == null ? java.util.Collections.emptyList() : java.util.Collections.unmodifiableList(refArray);
        refMap = refMap == null ? java.util.Collections.emptyMap() : java.util.Collections.unmodifiableMap(refMap);
        localEnumWithoutDefault = Objects.requireNonNullElse(localEnumWithoutDefault, Optional.empty());
    }

    @JsonCreator()
    public static RefVariations create(@JsonProperty(value = "mandatory_ref") jopenapi.test.more_features.refs.LocalCommonObject mandatoryRef, @JsonProperty(value = "optional_ref") jopenapi.test.more_features.refs.LocalCommonObject optionalRef, @JsonProperty(value = "ref_array") java.util.List<jopenapi.test.more_features.refs.LocalCommonObject> refArray, @JsonProperty(value = "ref_map") java.util.Map<String, jopenapi.test.more_features.refs.LocalCommonObject> refMap, @JsonProperty(value = "local_enum_without_default") jopenapi.test.more_features.refs.LocalEnumWithoutDefault localEnumWithoutDefault, @JsonProperty(value = "local_enum_without_default_defaulted_locally") jopenapi.test.more_features.refs.LocalEnumWithoutDefault localEnumWithoutDefaultDefaultedLocally, @JsonProperty(value = "local_enum_with_default") jopenapi.test.more_features.refs.LocalEnumWithDefault localEnumWithDefault, @JsonProperty(value = "local_enum_with_default_defaulted_locally") jopenapi.test.more_features.refs.LocalEnumWithDefault localEnumWithDefaultDefaultedLocally) {
        if (mandatoryRef == null) {
            throw new IllegalArgumentException("Property 'mandatory_ref' is required");
        }
        return new RefVariations(mandatoryRef, Optional.ofNullable(optionalRef), refArray, refMap, Optional.ofNullable(localEnumWithoutDefault), Objects.requireNonNullElse(localEnumWithoutDefaultDefaultedLocally, LocalEnumWithoutDefault.BAR), Objects.requireNonNullElse(localEnumWithDefault, LocalEnumWithDefault.FOO), Objects.requireNonNullElse(localEnumWithDefaultDefaultedLocally, LocalEnumWithDefault.BAR));
    }
}
