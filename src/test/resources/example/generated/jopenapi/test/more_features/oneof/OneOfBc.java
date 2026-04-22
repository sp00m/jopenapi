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

@JsonSubTypes({ @JsonSubTypes.Type(name = "category_b", value = jopenapi.test.more_features.oneof.B.class), @JsonSubTypes.Type(name = "category_c", value = jopenapi.test.more_features.oneof.C.class) })
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.EXISTING_PROPERTY, property = "category_bc", visible = true)
public sealed interface OneOfBc permits jopenapi.test.more_features.oneof.B, jopenapi.test.more_features.oneof.C {
}
