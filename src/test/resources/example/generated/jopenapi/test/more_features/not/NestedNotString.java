package jopenapi.test.more_features.not;

import com.fasterxml.jackson.annotation.JsonCreator;
import lombok.Builder;
import lombok.With;

@With()
@Builder(toBuilder = true)
public record NestedNotString() {

    @JsonCreator()
    static NestedNotString create() {
        return new NestedNotString();
    }
}
