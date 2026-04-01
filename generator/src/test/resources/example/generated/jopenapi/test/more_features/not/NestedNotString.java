package jopenapi.test.more_features.not;

import lombok.Builder;
import lombok.extern.jackson.Jacksonized;

@Jacksonized()
@Builder(toBuilder = true)
public record NestedNotString() {
}
