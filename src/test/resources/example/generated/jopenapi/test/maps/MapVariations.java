package jopenapi.test.maps;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.Collections;
import lombok.Builder;
import lombok.extern.jackson.Jacksonized;

@Jacksonized()
@Builder(toBuilder = true)
public record MapVariations(@JsonProperty(value = "untyped_map") java.util.Map<String, Object> untypedMap, @JsonProperty(value = "nested_map") java.util.Map<String, java.util.Map<String, Integer>> nestedMap, @JsonProperty(value = "custom_typed_map") java.util.Map<String, CustomTypedMap> customTypedMap, @JsonProperty(value = "custom_typed_nested_map") java.util.Map<String, java.util.Map<String, CustomTypedNestedMap>> customTypedNestedMap, @JsonProperty(value = "optional_map") java.util.Map<String, Integer> optionalMap, @JsonProperty(value = "map_with_min") java.util.Map<String, Integer> mapWithMin, @JsonProperty(value = "optional_map_with_min") java.util.Map<String, Integer> optionalMapWithMin, @JsonProperty(value = "map_with_max") java.util.Map<String, Integer> mapWithMax, @JsonProperty(value = "map_with_min_max") java.util.Map<String, Integer> mapWithMinMax) {

    @Jacksonized()
    @Builder(toBuilder = true)
    public record CustomTypedMap(@JsonProperty(value = "i") int i) {
    }

    @Jacksonized()
    @Builder(toBuilder = true)
    public record CustomTypedNestedMap(@JsonProperty(value = "i") int i) {
    }

    public MapVariations {
        untypedMap = untypedMap == null ? java.util.Map.of() : java.util.Collections.unmodifiableMap(untypedMap);
        nestedMap = nestedMap == null ? java.util.Map.of() : java.util.Collections.unmodifiableMap(nestedMap);
        customTypedMap = customTypedMap == null ? java.util.Map.of() : java.util.Collections.unmodifiableMap(customTypedMap);
        customTypedNestedMap = customTypedNestedMap == null ? java.util.Map.of() : java.util.Collections.unmodifiableMap(customTypedNestedMap);
        optionalMap = optionalMap == null ? java.util.Map.of() : java.util.Collections.unmodifiableMap(optionalMap);
        mapWithMin = mapWithMin == null ? java.util.Map.of() : java.util.Collections.unmodifiableMap(mapWithMin);
        optionalMapWithMin = optionalMapWithMin == null ? java.util.Map.of() : java.util.Collections.unmodifiableMap(optionalMapWithMin);
        mapWithMax = mapWithMax == null ? java.util.Map.of() : java.util.Collections.unmodifiableMap(mapWithMax);
        mapWithMinMax = mapWithMinMax == null ? java.util.Map.of() : java.util.Collections.unmodifiableMap(mapWithMinMax);
    }
}
