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

@JsonSubTypes({ @JsonSubTypes.Type(name = "category_a", value = jopenapi.test.more_features.oneof.A.class), @JsonSubTypes.Type(name = "category_b", value = jopenapi.test.more_features.oneof.B.class) })
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.EXISTING_PROPERTY, property = "category_ab", visible = true)
public sealed interface OneOfAb permits jopenapi.test.more_features.oneof.A, jopenapi.test.more_features.oneof.B {
}
