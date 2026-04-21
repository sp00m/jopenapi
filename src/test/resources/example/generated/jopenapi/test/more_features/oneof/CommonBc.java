package jopenapi.test.more_features.oneof;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.With;

@With()
@Builder(toBuilder = true)
public record CommonBc(@JsonProperty(value = "category_bc") String categoryBc) {

    @JsonCreator()
    static CommonBc create(@JsonProperty(value = "category_bc") String categoryBc) {
        if (categoryBc == null) {
            throw new IllegalArgumentException("Property 'category_bc' is required");
        }
        return new CommonBc(categoryBc);
    }
}
