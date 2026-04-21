package jopenapi.test.more_features.oneof;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.With;

@With()
@Builder(toBuilder = true)
public record CommonAb(@JsonProperty(value = "category_ab") String categoryAb) {

    @JsonCreator()
    static CommonAb create(@JsonProperty(value = "category_ab") String categoryAb) {
        if (categoryAb == null) {
            throw new com.github.jopenapi.support.MissingPropertyException("category_ab");
        }
        return new CommonAb(categoryAb);
    }
}
