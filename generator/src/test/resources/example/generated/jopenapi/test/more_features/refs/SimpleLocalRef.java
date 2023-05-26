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
public class SimpleLocalRef {

    @NotNull()
    jopenapi.test.more_features.refs.LocalCommonObject value;

    @JsonValue()
    public jopenapi.test.more_features.refs.LocalCommonObject get() {
        return value;
    }

    @JsonCreator()
    public static SimpleLocalRef of(jopenapi.test.more_features.refs.LocalCommonObject value) {
        return new SimpleLocalRef(value);
    }
}
