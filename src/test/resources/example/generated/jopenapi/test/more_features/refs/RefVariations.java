package jopenapi.test.more_features.refs;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.Collections;
import java.util.Optional;
import lombok.Builder;
import lombok.With;
import lombok.extern.jackson.Jacksonized;

@Jacksonized()
@With()
@Builder(toBuilder = true)
public record RefVariations(@JsonProperty(value = "mandatory_ref") jopenapi.test.more_features.refs.LocalCommonObject mandatoryRef, @JsonProperty(value = "optional_ref") Optional<jopenapi.test.more_features.refs.LocalCommonObject> optionalRef, @JsonProperty(value = "ref_array") java.util.List<jopenapi.test.more_features.refs.LocalCommonObject> refArray, @JsonProperty(value = "ref_map") java.util.Map<String, jopenapi.test.more_features.refs.LocalCommonObject> refMap) {

    public RefVariations {
        optionalRef = optionalRef == null ? Optional.empty() : optionalRef;
        refArray = refArray == null ? java.util.List.of() : java.util.Collections.unmodifiableList(refArray);
        refMap = refMap == null ? java.util.Map.of() : java.util.Collections.unmodifiableMap(refMap);
    }
}
