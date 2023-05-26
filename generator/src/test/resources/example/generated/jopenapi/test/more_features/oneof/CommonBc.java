package jopenapi.test.more_features.oneof;

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
public class CommonBc {

    @JsonProperty(value = "category_bc", access = JsonProperty.Access.AUTO)
    @NotNull()
    String categoryBc;

    public String getCategoryBc() {
        return categoryBc;
    }
}
