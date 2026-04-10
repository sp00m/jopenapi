package jopenapi.test.more_features.anyof;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.With;
import lombok.extern.jackson.Jacksonized;

@Jacksonized()
@With()
@Builder(toBuilder = true)
public record LocalCommonObject(@JsonProperty(value = "name") String name) {
}
