package jopenapi.test.more_features.not;

import lombok.Builder;
import lombok.With;
import lombok.extern.jackson.Jacksonized;

@Jacksonized()
@With()
@Builder(toBuilder = true)
public record NestedNotString() {
}
