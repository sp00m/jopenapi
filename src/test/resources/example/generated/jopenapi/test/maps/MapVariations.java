package jopenapi.test.maps;

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
public record MapVariations(@JsonProperty(value = "untyped_map") Map<String, Object> untypedMap, @JsonProperty(value = "nested_map") Map<String, Map<String, Integer>> nestedMap, @JsonProperty(value = "custom_typed_map") Map<String, CustomTypedMap> customTypedMap, @JsonProperty(value = "custom_typed_nested_map") Map<String, Map<String, CustomTypedNestedMap>> customTypedNestedMap, @JsonProperty(value = "optional_map") Map<String, Integer> optionalMap, @Size(min = 1, max = 2147483647) @JsonProperty(value = "map_with_min") Map<String, Integer> mapWithMin, @Size(min = 1, max = 2147483647) @JsonProperty(value = "optional_map_with_min") Map<String, Integer> optionalMapWithMin, @Size(min = 0, max = 5) @JsonProperty(value = "map_with_max") Map<String, Integer> mapWithMax, @Size(min = 1, max = 5) @JsonProperty(value = "map_with_min_max") Map<String, Integer> mapWithMinMax) {

    @With()
    @Builder(toBuilder = true)
    public record CustomTypedMap(@JsonProperty(value = "i") int i) {

        @JsonCreator()
        static CustomTypedMap create(@JsonProperty(value = "i") Integer i) {
            if (i == null) {
                throw new MissingPropertyException("i");
            }
            return new CustomTypedMap(i);
        }
    }

    @With()
    @Builder(toBuilder = true)
    public record CustomTypedNestedMap(@JsonProperty(value = "i") int i) {

        @JsonCreator()
        static CustomTypedNestedMap create(@JsonProperty(value = "i") Integer i) {
            if (i == null) {
                throw new MissingPropertyException("i");
            }
            return new CustomTypedNestedMap(i);
        }
    }

    public MapVariations {
        untypedMap = untypedMap == null ? Collections.emptyMap() : Collections.unmodifiableMap(untypedMap);
        nestedMap = nestedMap == null ? Collections.emptyMap() : Collections.unmodifiableMap(nestedMap);
        customTypedMap = customTypedMap == null ? Collections.emptyMap() : Collections.unmodifiableMap(customTypedMap);
        customTypedNestedMap = customTypedNestedMap == null ? Collections.emptyMap() : Collections.unmodifiableMap(customTypedNestedMap);
        optionalMap = optionalMap == null ? Collections.emptyMap() : Collections.unmodifiableMap(optionalMap);
        mapWithMin = mapWithMin == null ? Collections.emptyMap() : Collections.unmodifiableMap(mapWithMin);
        optionalMapWithMin = optionalMapWithMin == null ? Collections.emptyMap() : Collections.unmodifiableMap(optionalMapWithMin);
        mapWithMax = mapWithMax == null ? Collections.emptyMap() : Collections.unmodifiableMap(mapWithMax);
        mapWithMinMax = mapWithMinMax == null ? Collections.emptyMap() : Collections.unmodifiableMap(mapWithMinMax);
    }

    @JsonCreator()
    static MapVariations create(@JsonProperty(value = "untyped_map") Map<String, Object> untypedMap, @JsonProperty(value = "nested_map") Map<String, Map<String, Integer>> nestedMap, @JsonProperty(value = "custom_typed_map") Map<String, CustomTypedMap> customTypedMap, @JsonProperty(value = "custom_typed_nested_map") Map<String, Map<String, CustomTypedNestedMap>> customTypedNestedMap, @JsonProperty(value = "optional_map") Map<String, Integer> optionalMap, @JsonProperty(value = "map_with_min") Map<String, Integer> mapWithMin, @JsonProperty(value = "optional_map_with_min") Map<String, Integer> optionalMapWithMin, @JsonProperty(value = "map_with_max") Map<String, Integer> mapWithMax, @JsonProperty(value = "map_with_min_max") Map<String, Integer> mapWithMinMax) {
        return new MapVariations(untypedMap, nestedMap, customTypedMap, customTypedNestedMap, optionalMap, mapWithMin, optionalMapWithMin, mapWithMax, mapWithMinMax);
    }
}
