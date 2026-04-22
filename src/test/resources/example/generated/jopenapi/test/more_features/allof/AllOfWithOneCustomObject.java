package jopenapi.test.more_features.allof;

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
public record AllOfWithOneCustomObject(@JsonProperty(value = "active") boolean active, @JsonUnwrapped() jopenapi.test.more_features.allof.LocalCommonObject localCommonObject) {

    @JsonCreator()
    static AllOfWithOneCustomObject create(@JsonProperty(value = "active") Boolean active, @JsonUnwrapped() jopenapi.test.more_features.allof.LocalCommonObject localCommonObject) {
        if (active == null) {
            throw new MissingPropertyException("active");
        }
        if (localCommonObject == null) {
            throw new MissingPropertyException("localCommonObject");
        }
        return new AllOfWithOneCustomObject(active, localCommonObject);
    }
}
