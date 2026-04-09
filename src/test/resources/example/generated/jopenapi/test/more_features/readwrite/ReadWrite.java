package jopenapi.test.more_features.readwrite;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.Optional;
import lombok.Builder;
import lombok.extern.jackson.Jacksonized;

@Jacksonized()
@Builder(toBuilder = true)
public record ReadWrite(@JsonProperty(value = "readonly", access = JsonProperty.Access.READ_ONLY) Optional<String> readonly, @JsonProperty(value = "writeonly", access = JsonProperty.Access.WRITE_ONLY) Optional<String> writeonly, @JsonProperty(value = "readwrite") Optional<String> readwrite) {

    public ReadWrite {
        readonly = readonly == null ? Optional.empty() : readonly;
        writeonly = writeonly == null ? Optional.empty() : writeonly;
        readwrite = readwrite == null ? Optional.empty() : readwrite;
    }
}
