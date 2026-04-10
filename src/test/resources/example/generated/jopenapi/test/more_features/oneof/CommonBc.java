package jopenapi.test.more_features.oneof;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.With;
import lombok.extern.jackson.Jacksonized;

@Jacksonized()
@With()
@Builder(toBuilder = true)
public record CommonBc(@JsonProperty(value = "category_bc") String categoryBc) {
}
