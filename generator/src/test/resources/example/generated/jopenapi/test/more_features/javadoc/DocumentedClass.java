package jopenapi.test.more_features.javadoc;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.Optional;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.Value;
import lombok.extern.jackson.Jacksonized;

/**
 * Javadoc on the class.
 */
@Value()
@Jacksonized()
@Getter(AccessLevel.NONE)
@Builder(toBuilder = true)
public class DocumentedClass {

    /**
     * Javadoc on the field.
     */
    @JsonProperty(value = "documented_field", access = JsonProperty.Access.AUTO)
    Integer documentedField;

    public Optional<Integer> getDocumentedField() {
        return Optional.ofNullable(documentedField);
    }

    /**
     * Javadoc on the inner type.
     */
    @Value()
    @Jacksonized()
    @Getter(AccessLevel.NONE)
    @Builder(toBuilder = true)
    public static class DocumentedInnerType {

        @JsonProperty(value = "i", access = JsonProperty.Access.AUTO)
        Integer i;

        public Optional<Integer> getI() {
            return Optional.ofNullable(i);
        }
    }

    /**
     * Javadoc on the inner type.
     */
    @JsonProperty(value = "documented_inner_type", access = JsonProperty.Access.AUTO)
    DocumentedInnerType documentedInnerType;

    public Optional<DocumentedInnerType> getDocumentedInnerType() {
        return Optional.ofNullable(documentedInnerType);
    }
}
