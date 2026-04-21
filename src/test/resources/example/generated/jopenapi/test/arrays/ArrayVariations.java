package jopenapi.test.arrays;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Size;
import java.util.Collections;
import lombok.Builder;
import lombok.With;

@With()
@Builder(toBuilder = true)
public record ArrayVariations(@JsonProperty(value = "untyped_array") java.util.List<Object> untypedArray, @JsonProperty(value = "nested_array") java.util.List<java.util.List<Integer>> nestedArray, @JsonProperty(value = "custom_typed_array") java.util.List<CustomTypedArray> customTypedArray, @JsonProperty(value = "custom_typed_nested_array") java.util.List<java.util.List<CustomTypedNestedArray>> customTypedNestedArray, @JsonProperty(value = "optional_array") java.util.List<Integer> optionalArray, @JsonProperty(value = "unique_items_array") java.util.Set<Integer> uniqueItemsArray, @JsonProperty(value = "unique_items_optional_array") java.util.Set<Integer> uniqueItemsOptionalArray, @Size(min = 1, max = 2147483647) @JsonProperty(value = "array_with_min") java.util.List<Integer> arrayWithMin, @Size(min = 1, max = 2147483647) @JsonProperty(value = "optional_array_with_min") java.util.List<Integer> optionalArrayWithMin, @Size(min = 0, max = 5) @JsonProperty(value = "array_with_max") java.util.List<Integer> arrayWithMax, @Size(min = 1, max = 5) @JsonProperty(value = "unique_items_array_with_min_max") java.util.Set<Integer> uniqueItemsArrayWithMinMax) {

    @With()
    @Builder(toBuilder = true)
    public record CustomTypedArray(@JsonProperty(value = "i") int i) {

        @JsonCreator()
        static CustomTypedArray create(@JsonProperty(value = "i") Integer i) {
            if (i == null) {
                throw new IllegalArgumentException("Property 'i' is required");
            }
            return new CustomTypedArray(i);
        }
    }

    @With()
    @Builder(toBuilder = true)
    public record CustomTypedNestedArray(@JsonProperty(value = "i") int i) {

        @JsonCreator()
        static CustomTypedNestedArray create(@JsonProperty(value = "i") Integer i) {
            if (i == null) {
                throw new IllegalArgumentException("Property 'i' is required");
            }
            return new CustomTypedNestedArray(i);
        }
    }

    public ArrayVariations {
        untypedArray = untypedArray == null ? java.util.Collections.emptyList() : java.util.Collections.unmodifiableList(untypedArray);
        nestedArray = nestedArray == null ? java.util.Collections.emptyList() : java.util.Collections.unmodifiableList(nestedArray);
        customTypedArray = customTypedArray == null ? java.util.Collections.emptyList() : java.util.Collections.unmodifiableList(customTypedArray);
        customTypedNestedArray = customTypedNestedArray == null ? java.util.Collections.emptyList() : java.util.Collections.unmodifiableList(customTypedNestedArray);
        optionalArray = optionalArray == null ? java.util.Collections.emptyList() : java.util.Collections.unmodifiableList(optionalArray);
        uniqueItemsArray = uniqueItemsArray == null ? java.util.Collections.emptySet() : java.util.Collections.unmodifiableSet(uniqueItemsArray);
        uniqueItemsOptionalArray = uniqueItemsOptionalArray == null ? java.util.Collections.emptySet() : java.util.Collections.unmodifiableSet(uniqueItemsOptionalArray);
        arrayWithMin = arrayWithMin == null ? java.util.Collections.emptyList() : java.util.Collections.unmodifiableList(arrayWithMin);
        optionalArrayWithMin = optionalArrayWithMin == null ? java.util.Collections.emptyList() : java.util.Collections.unmodifiableList(optionalArrayWithMin);
        arrayWithMax = arrayWithMax == null ? java.util.Collections.emptyList() : java.util.Collections.unmodifiableList(arrayWithMax);
        uniqueItemsArrayWithMinMax = uniqueItemsArrayWithMinMax == null ? java.util.Collections.emptySet() : java.util.Collections.unmodifiableSet(uniqueItemsArrayWithMinMax);
    }

    @JsonCreator()
    static ArrayVariations create(@JsonProperty(value = "untyped_array") java.util.List<Object> untypedArray, @JsonProperty(value = "nested_array") java.util.List<java.util.List<Integer>> nestedArray, @JsonProperty(value = "custom_typed_array") java.util.List<CustomTypedArray> customTypedArray, @JsonProperty(value = "custom_typed_nested_array") java.util.List<java.util.List<CustomTypedNestedArray>> customTypedNestedArray, @JsonProperty(value = "optional_array") java.util.List<Integer> optionalArray, @JsonProperty(value = "unique_items_array") java.util.Set<Integer> uniqueItemsArray, @JsonProperty(value = "unique_items_optional_array") java.util.Set<Integer> uniqueItemsOptionalArray, @JsonProperty(value = "array_with_min") java.util.List<Integer> arrayWithMin, @JsonProperty(value = "optional_array_with_min") java.util.List<Integer> optionalArrayWithMin, @JsonProperty(value = "array_with_max") java.util.List<Integer> arrayWithMax, @JsonProperty(value = "unique_items_array_with_min_max") java.util.Set<Integer> uniqueItemsArrayWithMinMax) {
        return new ArrayVariations(untypedArray, nestedArray, customTypedArray, customTypedNestedArray, optionalArray, uniqueItemsArray, uniqueItemsOptionalArray, arrayWithMin, optionalArrayWithMin, arrayWithMax, uniqueItemsArrayWithMinMax);
    }
}
