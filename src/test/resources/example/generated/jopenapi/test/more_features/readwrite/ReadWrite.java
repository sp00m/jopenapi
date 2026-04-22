package jopenapi.test.more_features.readwrite;

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
public record ReadWrite(@JsonProperty(value = "readonly", access = JsonProperty.Access.READ_ONLY) Optional<String> readonly, @JsonProperty(value = "writeonly", access = JsonProperty.Access.WRITE_ONLY) Optional<String> writeonly, @JsonProperty(value = "readwrite") Optional<String> readwrite) {

    public ReadWrite {
        readonly = Objects.requireNonNullElse(readonly, Optional.empty());
        writeonly = Objects.requireNonNullElse(writeonly, Optional.empty());
        readwrite = Objects.requireNonNullElse(readwrite, Optional.empty());
    }

    @JsonCreator()
    static ReadWrite create(@JsonProperty(value = "writeonly", access = JsonProperty.Access.WRITE_ONLY) String writeonly, @JsonProperty(value = "readwrite") String readwrite) {
        return new ReadWrite(null, Optional.ofNullable(writeonly), Optional.ofNullable(readwrite));
    }
}
