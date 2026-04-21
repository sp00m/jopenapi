package jopenapi.test.more_features.javadoc;

import com.fasterxml.jackson.annotation.*;
import com.github.jopenapi.support.*;
import jakarta.validation.constraints.*;
import java.util.*;
import java.util.function.*;
import java.util.stream.*;
import lombok.Builder;
import lombok.With;

/**
 * Javadoc on the class.
 * @param documentedField Javadoc on the field.
 * @param documentedInnerType Javadoc on the inner type.
 */
@With()
@Builder(toBuilder = true)
public record DocumentedClass(@JsonProperty(value = "documented_field") Optional<Integer> documentedField, @JsonProperty(value = "documented_inner_type") Optional<DocumentedInnerType> documentedInnerType) {

    /**
     * Javadoc on the inner type.
     */
    @With()
    @Builder(toBuilder = true)
    public record DocumentedInnerType(@JsonProperty(value = "i") Optional<Integer> i) {

        public DocumentedInnerType {
            i = Objects.requireNonNullElse(i, Optional.empty());
        }

        @JsonCreator()
        static DocumentedInnerType create(@JsonProperty(value = "i") Integer i) {
            return new DocumentedInnerType(Optional.ofNullable(i));
        }
    }

    public DocumentedClass {
        documentedField = Objects.requireNonNullElse(documentedField, Optional.empty());
        documentedInnerType = Objects.requireNonNullElse(documentedInnerType, Optional.empty());
    }

    @JsonCreator()
    static DocumentedClass create(@JsonProperty(value = "documented_field") Integer documentedField, @JsonProperty(value = "documented_inner_type") DocumentedInnerType documentedInnerType) {
        return new DocumentedClass(Optional.ofNullable(documentedField), Optional.ofNullable(documentedInnerType));
    }
}
