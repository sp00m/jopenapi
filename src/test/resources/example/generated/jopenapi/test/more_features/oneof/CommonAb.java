package jopenapi.test.more_features.oneof;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.extern.jackson.Jacksonized;

@Jacksonized()
@Builder(toBuilder = true)
public record CommonAb(@JsonProperty(value = "category_ab") String categoryAb) {
}
