package jopenapi.test.common;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.With;
import lombok.extern.jackson.Jacksonized;

@Jacksonized()
@With()
@Builder(toBuilder = true)
public record CommonObject(@JsonProperty(value = "id") int id) {
}
