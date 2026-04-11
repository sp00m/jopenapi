package jopenapi.test.maps;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Size;
import java.util.Collections;
import lombok.Builder;
import lombok.With;

@With()
@Builder(toBuilder = true)
public record MapVariations(@JsonProperty(value = "untyped_map") java.util.Map<String, Object> untypedMap, @JsonProperty(value = "nested_map") java.util.Map<String, java.util.Map<String, Integer>> nestedMap, @JsonProperty(value = "custom_typed_map") java.util.Map<String, CustomTypedMap> customTypedMap, @JsonProperty(value = "custom_typed_nested_map") java.util.Map<String, java.util.Map<String, CustomTypedNestedMap>> customTypedNestedMap, @JsonProperty(value = "optional_map") java.util.Map<String, Integer> optionalMap, @Size(min = 1, max = 2147483647) @JsonProperty(value = "map_with_min") java.util.Map<String, Integer> mapWithMin, @Size(min = 1, max = 2147483647) @JsonProperty(value = "optional_map_with_min") java.util.Map<String, Integer> optionalMapWithMin, @Size(min = 0, max = 5) @JsonProperty(value = "map_with_max") java.util.Map<String, Integer> mapWithMax, @Size(min = 1, max = 5) @JsonProperty(value = "map_with_min_max") java.util.Map<String, Integer> mapWithMinMax) {

    @With()
    @Builder(toBuilder = true)
    public record CustomTypedMap(@JsonProperty(value = "i") int i) {

        @JsonCreator()
        public static CustomTypedMap create(@JsonProperty(value = "i") Integer i) {
            if (i == null) {
                throw new IllegalArgumentException("Property 'i' is required");
            }
            return new CustomTypedMap(i);
        }
    }

    @With()
    @Builder(toBuilder = true)
    public record CustomTypedNestedMap(@JsonProperty(value = "i") int i) {

        @JsonCreator()
        public static CustomTypedNestedMap create(@JsonProperty(value = "i") Integer i) {
            if (i == null) {
                throw new IllegalArgumentException("Property 'i' is required");
            }
            return new CustomTypedNestedMap(i);
        }
    }

    public MapVariations {
        untypedMap = untypedMap == null ? java.util.Collections.emptyMap() : java.util.Collections.unmodifiableMap(untypedMap);
        nestedMap = nestedMap == null ? java.util.Collections.emptyMap() : java.util.Collections.unmodifiableMap(nestedMap);
        customTypedMap = customTypedMap == null ? java.util.Collections.emptyMap() : java.util.Collections.unmodifiableMap(customTypedMap);
        customTypedNestedMap = customTypedNestedMap == null ? java.util.Collections.emptyMap() : java.util.Collections.unmodifiableMap(customTypedNestedMap);
        optionalMap = optionalMap == null ? java.util.Collections.emptyMap() : java.util.Collections.unmodifiableMap(optionalMap);
        mapWithMin = mapWithMin == null ? java.util.Collections.emptyMap() : java.util.Collections.unmodifiableMap(mapWithMin);
        optionalMapWithMin = optionalMapWithMin == null ? java.util.Collections.emptyMap() : java.util.Collections.unmodifiableMap(optionalMapWithMin);
        mapWithMax = mapWithMax == null ? java.util.Collections.emptyMap() : java.util.Collections.unmodifiableMap(mapWithMax);
        mapWithMinMax = mapWithMinMax == null ? java.util.Collections.emptyMap() : java.util.Collections.unmodifiableMap(mapWithMinMax);
    }

    @JsonCreator()
    public static MapVariations create(@JsonProperty(value = "untyped_map") java.util.Map<String, Object> untypedMap, @JsonProperty(value = "nested_map") java.util.Map<String, java.util.Map<String, Integer>> nestedMap, @JsonProperty(value = "custom_typed_map") java.util.Map<String, CustomTypedMap> customTypedMap, @JsonProperty(value = "custom_typed_nested_map") java.util.Map<String, java.util.Map<String, CustomTypedNestedMap>> customTypedNestedMap, @JsonProperty(value = "optional_map") java.util.Map<String, Integer> optionalMap, @JsonProperty(value = "map_with_min") java.util.Map<String, Integer> mapWithMin, @JsonProperty(value = "optional_map_with_min") java.util.Map<String, Integer> optionalMapWithMin, @JsonProperty(value = "map_with_max") java.util.Map<String, Integer> mapWithMax, @JsonProperty(value = "map_with_min_max") java.util.Map<String, Integer> mapWithMinMax) {
        return new MapVariations(untypedMap, nestedMap, customTypedMap, customTypedNestedMap, optionalMap, mapWithMin, optionalMapWithMin, mapWithMax, mapWithMinMax);
    }
}
