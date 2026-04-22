package jopenapi.test.arrays;

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
public record ArrayVariations(@JsonProperty(value = "untyped_array") List<Object> untypedArray, @JsonProperty(value = "nested_array") List<List<Integer>> nestedArray, @JsonProperty(value = "custom_typed_array") List<CustomTypedArray> customTypedArray, @JsonProperty(value = "custom_typed_nested_array") List<List<CustomTypedNestedArray>> customTypedNestedArray, @JsonProperty(value = "optional_array") List<Integer> optionalArray, @JsonProperty(value = "unique_items_array") Set<Integer> uniqueItemsArray, @JsonProperty(value = "unique_items_optional_array") Set<Integer> uniqueItemsOptionalArray, @Size(min = 1, max = 2147483647) @JsonProperty(value = "array_with_min") List<Integer> arrayWithMin, @Size(min = 1, max = 2147483647) @JsonProperty(value = "optional_array_with_min") List<Integer> optionalArrayWithMin, @Size(min = 0, max = 5) @JsonProperty(value = "array_with_max") List<Integer> arrayWithMax, @Size(min = 1, max = 5) @JsonProperty(value = "unique_items_array_with_min_max") Set<Integer> uniqueItemsArrayWithMinMax) {

    @With()
    @Builder(toBuilder = true)
    public record CustomTypedArray(@JsonProperty(value = "i") int i) {

        @JsonCreator()
        static CustomTypedArray create(@JsonProperty(value = "i") Integer i) {
            if (i == null) {
                throw new MissingPropertyException("i");
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
                throw new MissingPropertyException("i");
            }
            return new CustomTypedNestedArray(i);
        }
    }

    public ArrayVariations {
        untypedArray = untypedArray == null ? Collections.emptyList() : Collections.unmodifiableList(untypedArray);
        nestedArray = nestedArray == null ? Collections.emptyList() : Collections.unmodifiableList(nestedArray);
        customTypedArray = customTypedArray == null ? Collections.emptyList() : Collections.unmodifiableList(customTypedArray);
        customTypedNestedArray = customTypedNestedArray == null ? Collections.emptyList() : Collections.unmodifiableList(customTypedNestedArray);
        optionalArray = optionalArray == null ? Collections.emptyList() : Collections.unmodifiableList(optionalArray);
        uniqueItemsArray = uniqueItemsArray == null ? Collections.emptySet() : Collections.unmodifiableSet(uniqueItemsArray);
        uniqueItemsOptionalArray = uniqueItemsOptionalArray == null ? Collections.emptySet() : Collections.unmodifiableSet(uniqueItemsOptionalArray);
        arrayWithMin = arrayWithMin == null ? Collections.emptyList() : Collections.unmodifiableList(arrayWithMin);
        optionalArrayWithMin = optionalArrayWithMin == null ? Collections.emptyList() : Collections.unmodifiableList(optionalArrayWithMin);
        arrayWithMax = arrayWithMax == null ? Collections.emptyList() : Collections.unmodifiableList(arrayWithMax);
        uniqueItemsArrayWithMinMax = uniqueItemsArrayWithMinMax == null ? Collections.emptySet() : Collections.unmodifiableSet(uniqueItemsArrayWithMinMax);
    }

    @JsonCreator()
    static ArrayVariations create(@JsonProperty(value = "untyped_array") List<Object> untypedArray, @JsonProperty(value = "nested_array") List<List<Integer>> nestedArray, @JsonProperty(value = "custom_typed_array") List<CustomTypedArray> customTypedArray, @JsonProperty(value = "custom_typed_nested_array") List<List<CustomTypedNestedArray>> customTypedNestedArray, @JsonProperty(value = "optional_array") List<Integer> optionalArray, @JsonProperty(value = "unique_items_array") Set<Integer> uniqueItemsArray, @JsonProperty(value = "unique_items_optional_array") Set<Integer> uniqueItemsOptionalArray, @JsonProperty(value = "array_with_min") List<Integer> arrayWithMin, @JsonProperty(value = "optional_array_with_min") List<Integer> optionalArrayWithMin, @JsonProperty(value = "array_with_max") List<Integer> arrayWithMax, @JsonProperty(value = "unique_items_array_with_min_max") Set<Integer> uniqueItemsArrayWithMinMax) {
        return new ArrayVariations(untypedArray, nestedArray, customTypedArray, customTypedNestedArray, optionalArray, uniqueItemsArray, uniqueItemsOptionalArray, arrayWithMin, optionalArrayWithMin, arrayWithMax, uniqueItemsArrayWithMinMax);
    }
}
