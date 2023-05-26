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
public class CommonAb {

    @JsonProperty(value = "category_ab", access = JsonProperty.Access.AUTO)
    @NotNull()
    String categoryAb;

    public String getCategoryAb() {
        return categoryAb;
    }
}
