package jopenapi.test.common;

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
public record CommonObject(@JsonProperty(value = "id") int id) {

    @JsonCreator()
    static CommonObject create(@JsonProperty(value = "id") Integer id) {
        if (id == null) {
            throw new MissingPropertyException("id");
        }
        return new CommonObject(id);
    }
}
