package jopenapi.test.arrays;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder.Default;
import lombok.Getter;
import lombok.Value;

@Value()
@Getter(AccessLevel.NONE)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class UniqueItemsArrayWithMinMax {

    @Default()
    @Size(min = 1, max = 5)
    @NotNull()
    java.util.Set<Integer> value = java.util.Set.of();

    @JsonValue()
    public java.util.Set<Integer> get() {
        return value;
    }

    @JsonCreator()
    public static UniqueItemsArrayWithMinMax of(java.util.Set<Integer> value) {
        return new UniqueItemsArrayWithMinMax(value);
    }
}
