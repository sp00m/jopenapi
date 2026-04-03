package jopenapi.test.more_features.oneof;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

@JsonSubTypes({ @JsonSubTypes.Type(name = "category_b", value = jopenapi.test.more_features.oneof.B.class), @JsonSubTypes.Type(name = "category_c", value = jopenapi.test.more_features.oneof.C.class) })
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.EXISTING_PROPERTY, property = "category_bc", visible = true)
public interface OneOfBc {
}
