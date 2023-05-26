package jopenapi.test.maps;

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
public class SimpleMap {

    @Default()
    @NotNull()
    java.util.Map<String, Integer> value = java.util.Map.of();

    @JsonValue()
    public java.util.Map<String, Integer> get() {
        return value;
    }

    @JsonCreator()
    public static SimpleMap of(java.util.Map<String, Integer> value) {
        return new SimpleMap(value);
    }
}
