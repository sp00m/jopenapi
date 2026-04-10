package jopenapi.test.more_features.refs;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public record SimpleExternalRef(@JsonValue() jopenapi.test.common.CommonObject value) {

    @JsonCreator()
    public SimpleExternalRef {
    }
}
