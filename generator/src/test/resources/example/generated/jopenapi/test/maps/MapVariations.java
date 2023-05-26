package jopenapi.test.maps;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
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
public class MapVariations {

    @Default()
    @JsonProperty(value = "untyped_map", access = JsonProperty.Access.AUTO)
    @NotNull()
    java.util.Map<String, Object> untypedMap = java.util.Map.of();

    public java.util.Map<String, Object> getUntypedMap() {
        return untypedMap;
    }

    @Default()
    @JsonProperty(value = "nested_map", access = JsonProperty.Access.AUTO)
    @NotNull()
    java.util.Map<String, java.util.Map<String, Integer>> nestedMap = java.util.Map.of();

    public java.util.Map<String, java.util.Map<String, Integer>> getNestedMap() {
        return nestedMap;
    }

    @Value()
    @Jacksonized()
    @Getter(AccessLevel.NONE)
    @Builder(toBuilder = true)
    public static class CustomTypedMap {

        @JsonProperty(value = "i", access = JsonProperty.Access.AUTO)
        @NotNull()
        Integer i;

        public int getI() {
            return i;
        }
    }

    @Default()
    @JsonProperty(value = "custom_typed_map", access = JsonProperty.Access.AUTO)
    @NotNull()
    java.util.Map<String, CustomTypedMap> customTypedMap = java.util.Map.of();

    public java.util.Map<String, CustomTypedMap> getCustomTypedMap() {
        return customTypedMap;
    }

    @Value()
    @Jacksonized()
    @Getter(AccessLevel.NONE)
    @Builder(toBuilder = true)
    public static class CustomTypedNestedMap {

        @JsonProperty(value = "i", access = JsonProperty.Access.AUTO)
        @NotNull()
        Integer i;

        public int getI() {
            return i;
        }
    }

    @Default()
    @JsonProperty(value = "custom_typed_nested_map", access = JsonProperty.Access.AUTO)
    @NotNull()
    java.util.Map<String, java.util.Map<String, CustomTypedNestedMap>> customTypedNestedMap = java.util.Map.of();

    public java.util.Map<String, java.util.Map<String, CustomTypedNestedMap>> getCustomTypedNestedMap() {
        return customTypedNestedMap;
    }

    @Default()
    @JsonProperty(value = "optional_map", access = JsonProperty.Access.AUTO)
    java.util.Map<String, Integer> optionalMap = java.util.Map.of();

    public java.util.Map<String, Integer> getOptionalMap() {
        return optionalMap == null ? java.util.Map.of() : optionalMap;
    }

    @Default()
    @Size(min = 1, max = 2147483647)
    @JsonProperty(value = "map_with_min", access = JsonProperty.Access.AUTO)
    @NotNull()
    java.util.Map<String, Integer> mapWithMin = java.util.Map.of();

    public java.util.Map<String, Integer> getMapWithMin() {
        return mapWithMin;
    }

    @Default()
    @Size(min = 1, max = 2147483647)
    @JsonProperty(value = "optional_map_with_min", access = JsonProperty.Access.AUTO)
    @NotNull()
    java.util.Map<String, Integer> optionalMapWithMin = java.util.Map.of();

    public java.util.Map<String, Integer> getOptionalMapWithMin() {
        return optionalMapWithMin;
    }

    @Default()
    @Size(min = 0, max = 5)
    @JsonProperty(value = "map_with_max", access = JsonProperty.Access.AUTO)
    @NotNull()
    java.util.Map<String, Integer> mapWithMax = java.util.Map.of();

    public java.util.Map<String, Integer> getMapWithMax() {
        return mapWithMax;
    }

    @Default()
    @Size(min = 1, max = 5)
    @JsonProperty(value = "map_with_min_max", access = JsonProperty.Access.AUTO)
    @NotNull()
    java.util.Map<String, Integer> mapWithMinMax = java.util.Map.of();

    public java.util.Map<String, Integer> getMapWithMinMax() {
        return mapWithMinMax;
    }
}
