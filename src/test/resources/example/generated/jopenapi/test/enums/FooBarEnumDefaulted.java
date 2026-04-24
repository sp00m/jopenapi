package jopenapi.test.enums;

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
public record FooBarEnumDefaulted(@JsonProperty(value = "foobar") jopenapi.test.enums.FooBarEnum foobar) {

    @JsonCreator()
    static FooBarEnumDefaulted create(@JsonProperty(value = "foobar") jopenapi.test.enums.FooBarEnum foobar) {
        return new FooBarEnumDefaulted(Objects.requireNonNullElse(foobar, FooBarEnum.BAR));
    }
}
