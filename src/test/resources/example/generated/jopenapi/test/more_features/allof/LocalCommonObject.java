package jopenapi.test.more_features.allof;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.extern.jackson.Jacksonized;

@Jacksonized()
@Builder(toBuilder = true)
public record LocalCommonObject(@JsonProperty(value = "name") String name) {
}
