package jopenapi.test.more_features.anyof;

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
public record LocalCommonObject(@JsonProperty(value = "name") String name) {

    @JsonCreator()
    static LocalCommonObject create(@JsonProperty(value = "name") String name) {
        if (name == null) {
            throw new MissingPropertyException("name");
        }
        return new LocalCommonObject(name);
    }
}
