package jopenapi.test.common;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.With;

@With()
@Builder(toBuilder = true)
public record CommonObject(@JsonProperty(value = "id") int id) {

    @JsonCreator()
    static CommonObject create(@JsonProperty(value = "id") Integer id) {
        if (id == null) {
            throw new IllegalArgumentException("Property 'id' is required");
        }
        return new CommonObject(id);
    }
}
