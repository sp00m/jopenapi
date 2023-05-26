package jopenapi.test.common;

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
public class CommonObject {

    @JsonProperty(value = "id", access = JsonProperty.Access.AUTO)
    @NotNull()
    Integer id;

    public int getId() {
        return id;
    }
}
