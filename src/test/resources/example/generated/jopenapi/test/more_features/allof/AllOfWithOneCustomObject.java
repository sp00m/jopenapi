package jopenapi.test.more_features.allof;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonUnwrapped;
import lombok.Builder;
import lombok.With;

@With()
@Builder(toBuilder = true)
public record AllOfWithOneCustomObject(@JsonProperty(value = "active") boolean active, @JsonUnwrapped() jopenapi.test.more_features.allof.LocalCommonObject localCommonObject) {

    @JsonCreator()
    public static AllOfWithOneCustomObject create(@JsonProperty(value = "active") Boolean active, @JsonUnwrapped() jopenapi.test.more_features.allof.LocalCommonObject localCommonObject) {
        if (active == null) {
            throw new IllegalArgumentException("Property 'active' is required");
        }
        if (localCommonObject == null) {
            throw new IllegalArgumentException("Property 'localCommonObject' is required");
        }
        return new AllOfWithOneCustomObject(active, localCommonObject);
    }
}
