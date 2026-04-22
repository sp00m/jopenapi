package jopenapi.test.more_features.oneof;

import com.fasterxml.jackson.annotation.*;
import com.github.jopenapi.support.*;
import jakarta.validation.constraints.*;
import java.math.*;
import java.net.*;
import java.time.*;
import java.util.*;
import java.util.function.*;
import java.util.stream.*;
import lombok.Builder;
import lombok.With;

@With()
@Builder(toBuilder = true)
public record CommonAb(@JsonProperty(value = "category_ab") String categoryAb) {

    @JsonCreator()
    static CommonAb create(@JsonProperty(value = "category_ab") String categoryAb) {
        if (categoryAb == null) {
            throw new MissingPropertyException("category_ab");
        }
        return new CommonAb(categoryAb);
    }
}
