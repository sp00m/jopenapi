package jopenapi.test.more_features.anyof;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.With;

@With()
@Builder(toBuilder = true)
public record LocalCommonObject(@JsonProperty(value = "name") String name) {

    @JsonCreator()
    static LocalCommonObject create(@JsonProperty(value = "name") String name) {
        if (name == null) {
            throw new IllegalArgumentException("Property 'name' is required");
        }
        return new LocalCommonObject(name);
    }
}
