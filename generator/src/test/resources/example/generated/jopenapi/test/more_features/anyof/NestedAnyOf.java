package jopenapi.test.more_features.anyof;

import lombok.Builder;
import lombok.extern.jackson.Jacksonized;

@Jacksonized()
@Builder(toBuilder = true)
public record NestedAnyOf() {
}
