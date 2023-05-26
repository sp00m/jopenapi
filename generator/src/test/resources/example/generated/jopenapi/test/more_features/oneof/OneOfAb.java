package jopenapi.test.more_features.oneof;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

@JsonSubTypes({ @JsonSubTypes.Type(name = "category_a", value = jopenapi.test.more_features.oneof.A.class), @JsonSubTypes.Type(name = "category_b", value = jopenapi.test.more_features.oneof.B.class) })
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.EXISTING_PROPERTY, property = "category_ab", visible = true)
public interface OneOfAb {
}
