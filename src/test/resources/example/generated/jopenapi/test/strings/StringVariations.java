package jopenapi.test.strings;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import java.util.Optional;
import lombok.Builder;
import lombok.With;
import lombok.extern.jackson.Jacksonized;

@Jacksonized()
@With()
@Builder(toBuilder = true)
public record StringVariations(@JsonProperty(value = "as_date") java.time.LocalDate asDate, @JsonProperty(value = "as_date_time") java.time.OffsetDateTime asDateTime, @JsonProperty(value = "as_uuid") java.util.UUID asUuid, @JsonProperty(value = "as_uri") java.net.URI asUri, @Pattern(regexp = "^\\d+$") @JsonProperty(value = "string_with_pattern") String stringWithPattern, @Size(min = 1, max = 2147483647) @JsonProperty(value = "string_with_min") String stringWithMin, @Size(min = 0, max = 5) @JsonProperty(value = "string_with_max") String stringWithMax, @Size(min = 1, max = 5) @Pattern(regexp = "^\\d+$") @JsonProperty(value = "string_with_pattern_min_max") String stringWithPatternMinMax, @JsonProperty(value = "optional_string") Optional<String> optionalString) {

    public StringVariations {
        optionalString = optionalString == null ? Optional.empty() : optionalString;
    }
}
