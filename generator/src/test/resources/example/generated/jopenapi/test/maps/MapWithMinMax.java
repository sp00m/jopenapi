package jopenapi.test.maps;

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
public class MapWithMinMax {

    @Default()
    @Size(min = 1, max = 5)
    @NotNull()
    java.util.Map<String, Integer> value = java.util.Map.of();

    @JsonValue()
    public java.util.Map<String, Integer> get() {
        return value;
    }

    @JsonCreator()
    public static MapWithMinMax of(java.util.Map<String, Integer> value) {
        return new MapWithMinMax(value);
    }
}
