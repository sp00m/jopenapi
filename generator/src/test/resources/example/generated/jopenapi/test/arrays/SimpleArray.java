package jopenapi.test.arrays;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder.Default;
import lombok.Getter;
import lombok.Value;

@Value()
@Getter(AccessLevel.NONE)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class SimpleArray {

    @Default()
    @NotNull()
    java.util.List<Integer> value = java.util.List.of();

    @JsonValue()
    public java.util.List<Integer> get() {
        return value;
    }

    @JsonCreator()
    public static SimpleArray of(java.util.List<Integer> value) {
        return new SimpleArray(value);
    }
}
