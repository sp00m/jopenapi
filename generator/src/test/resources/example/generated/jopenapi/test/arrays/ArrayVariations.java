package jopenapi.test.arrays;

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
public class ArrayVariations {

    @Default()
    @JsonProperty(value = "untyped_array", access = JsonProperty.Access.AUTO)
    @NotNull()
    java.util.List<Object> untypedArray = java.util.List.of();

    public java.util.List<Object> getUntypedArray() {
        return untypedArray;
    }

    @Default()
    @JsonProperty(value = "nested_array", access = JsonProperty.Access.AUTO)
    @NotNull()
    java.util.List<java.util.List<Integer>> nestedArray = java.util.List.of();

    public java.util.List<java.util.List<Integer>> getNestedArray() {
        return nestedArray;
    }

    @Value()
    @Jacksonized()
    @Getter(AccessLevel.NONE)
    @Builder(toBuilder = true)
    public static class CustomTypedArray {

        @JsonProperty(value = "i", access = JsonProperty.Access.AUTO)
        @NotNull()
        Integer i;

        public int getI() {
            return i;
        }
    }

    @Default()
    @JsonProperty(value = "custom_typed_array", access = JsonProperty.Access.AUTO)
    @NotNull()
    java.util.List<CustomTypedArray> customTypedArray = java.util.List.of();

    public java.util.List<CustomTypedArray> getCustomTypedArray() {
        return customTypedArray;
    }

    @Value()
    @Jacksonized()
    @Getter(AccessLevel.NONE)
    @Builder(toBuilder = true)
    public static class CustomTypedNestedArray {

        @JsonProperty(value = "i", access = JsonProperty.Access.AUTO)
        @NotNull()
        Integer i;

        public int getI() {
            return i;
        }
    }

    @Default()
    @JsonProperty(value = "custom_typed_nested_array", access = JsonProperty.Access.AUTO)
    @NotNull()
    java.util.List<java.util.List<CustomTypedNestedArray>> customTypedNestedArray = java.util.List.of();

    public java.util.List<java.util.List<CustomTypedNestedArray>> getCustomTypedNestedArray() {
        return customTypedNestedArray;
    }

    @Default()
    @JsonProperty(value = "optional_array", access = JsonProperty.Access.AUTO)
    java.util.List<Integer> optionalArray = java.util.List.of();

    public java.util.List<Integer> getOptionalArray() {
        return optionalArray == null ? java.util.List.of() : optionalArray;
    }

    @Default()
    @JsonProperty(value = "unique_items_array", access = JsonProperty.Access.AUTO)
    @NotNull()
    java.util.Set<Integer> uniqueItemsArray = java.util.Set.of();

    public java.util.Set<Integer> getUniqueItemsArray() {
        return uniqueItemsArray;
    }

    @Default()
    @JsonProperty(value = "unique_items_optional_array", access = JsonProperty.Access.AUTO)
    java.util.Set<Integer> uniqueItemsOptionalArray = java.util.Set.of();

    public java.util.Set<Integer> getUniqueItemsOptionalArray() {
        return uniqueItemsOptionalArray == null ? java.util.Set.of() : uniqueItemsOptionalArray;
    }

    @Default()
    @Size(min = 1, max = 2147483647)
    @JsonProperty(value = "array_with_min", access = JsonProperty.Access.AUTO)
    @NotNull()
    java.util.List<Integer> arrayWithMin = java.util.List.of();

    public java.util.List<Integer> getArrayWithMin() {
        return arrayWithMin;
    }

    @Default()
    @Size(min = 1, max = 2147483647)
    @JsonProperty(value = "optional_array_with_min", access = JsonProperty.Access.AUTO)
    @NotNull()
    java.util.List<Integer> optionalArrayWithMin = java.util.List.of();

    public java.util.List<Integer> getOptionalArrayWithMin() {
        return optionalArrayWithMin;
    }

    @Default()
    @Size(min = 0, max = 5)
    @JsonProperty(value = "array_with_max", access = JsonProperty.Access.AUTO)
    @NotNull()
    java.util.List<Integer> arrayWithMax = java.util.List.of();

    public java.util.List<Integer> getArrayWithMax() {
        return arrayWithMax;
    }

    @Default()
    @Size(min = 1, max = 5)
    @JsonProperty(value = "unique_items_array_with_min_max", access = JsonProperty.Access.AUTO)
    @NotNull()
    java.util.Set<Integer> uniqueItemsArrayWithMinMax = java.util.Set.of();

    public java.util.Set<Integer> getUniqueItemsArrayWithMinMax() {
        return uniqueItemsArrayWithMinMax;
    }
}
