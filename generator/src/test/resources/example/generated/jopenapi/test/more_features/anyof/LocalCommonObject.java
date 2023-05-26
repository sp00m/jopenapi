package jopenapi.test.more_features.anyof;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.Value;
import lombok.extern.jackson.Jacksonized;

@Value()
@Jacksonized()
@Getter(AccessLevel.NONE)
@Builder(toBuilder = true)
public class LocalCommonObject {

    @JsonProperty(value = "name", access = JsonProperty.Access.AUTO)
    @NotNull()
    String name;

    public String getName() {
        return name;
    }
}
