package jopenapi.test.more_features.refs;

import com.fasterxml.jackson.annotation.*;
import com.github.jopenapi.support.*;
import jakarta.validation.constraints.*;
import java.math.*;
import java.net.*;
import java.time.*;
import java.util.*;
import java.util.function.*;
import java.util.stream.*;

public record SimpleExternalRef(@JsonValue() jopenapi.test.common.CommonObject value) {

    @JsonCreator()
    public SimpleExternalRef {
    }
}
