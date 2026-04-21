package jopenapi.test.more_features.oneof;

import com.fasterxml.jackson.annotation.*;
import com.github.jopenapi.support.*;
import jakarta.validation.constraints.*;
import java.util.*;
import java.util.function.*;
import java.util.stream.*;
import lombok.Builder;
import lombok.With;

@With()
@Builder(toBuilder = true)
public record CommonBc(@JsonProperty(value = "category_bc") String categoryBc) {

    @JsonCreator()
    static CommonBc create(@JsonProperty(value = "category_bc") String categoryBc) {
        if (categoryBc == null) {
            throw new MissingPropertyException("category_bc");
        }
        return new CommonBc(categoryBc);
    }
}
