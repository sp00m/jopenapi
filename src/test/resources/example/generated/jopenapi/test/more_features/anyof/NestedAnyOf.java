package jopenapi.test.more_features.anyof;

import com.fasterxml.jackson.annotation.JsonCreator;
import lombok.Builder;
import lombok.With;

@With()
@Builder(toBuilder = true)
public record NestedAnyOf() {

    @JsonCreator()
    public static NestedAnyOf create() {
        return new NestedAnyOf();
    }
}
