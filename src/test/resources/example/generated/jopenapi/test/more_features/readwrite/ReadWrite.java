package jopenapi.test.more_features.readwrite;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.Objects;
import java.util.Optional;
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
    public static ReadWrite create(@JsonProperty(value = "writeonly", access = JsonProperty.Access.WRITE_ONLY) String writeonly, @JsonProperty(value = "readwrite") String readwrite) {
        return new ReadWrite(Optional.empty(), Optional.ofNullable(writeonly), Optional.ofNullable(readwrite));
    }
}
