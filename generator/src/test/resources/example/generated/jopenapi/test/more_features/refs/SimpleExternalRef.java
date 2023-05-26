package jopenapi.test.more_features.refs;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Value;

@Value()
@Getter(AccessLevel.NONE)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class SimpleExternalRef {

    @NotNull()
    jopenapi.test.common.CommonObject value;

    @JsonValue()
    public jopenapi.test.common.CommonObject get() {
        return value;
    }

    @JsonCreator()
    public static SimpleExternalRef of(jopenapi.test.common.CommonObject value) {
        return new SimpleExternalRef(value);
    }
}
