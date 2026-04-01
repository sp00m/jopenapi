package jopenapi.test.more_features.javadoc;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.Optional;
import lombok.Builder;
import lombok.extern.jackson.Jacksonized;

/**
 * Javadoc on the class.
 * @param documentedField Javadoc on the field.
 * @param documentedInnerType Javadoc on the inner type.
 */
@Jacksonized()
@Builder(toBuilder = true)
public record DocumentedClass(@JsonProperty(value = "documented_field") Optional<Integer> documentedField,
		@JsonProperty(value = "documented_inner_type") Optional<DocumentedInnerType> documentedInnerType) {

	/**
	 * Javadoc on the inner type.
	 */
	@Jacksonized()
	@Builder(toBuilder = true)
	public record DocumentedInnerType(@JsonProperty(value = "i") Optional<Integer> i) {

		public DocumentedInnerType {
			i = i == null ? Optional.empty() : i;
		}
	}

	public DocumentedClass {
		documentedField = documentedField == null ? Optional.empty() : documentedField;
		documentedInnerType = documentedInnerType == null ? Optional.empty() : documentedInnerType;
	}
}
