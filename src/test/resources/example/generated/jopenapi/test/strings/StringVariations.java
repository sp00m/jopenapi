package jopenapi.test.strings;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.Optional;
import lombok.Builder;
import lombok.extern.jackson.Jacksonized;

@Jacksonized()
@Builder(toBuilder = true)
public record StringVariations(@JsonProperty(value = "as_date") java.time.LocalDate asDate, @JsonProperty(value = "as_date_time") java.time.OffsetDateTime asDateTime, @JsonProperty(value = "as_uuid") java.util.UUID asUuid, @JsonProperty(value = "as_uri") java.net.URI asUri, @JsonProperty(value = "string_with_pattern") String stringWithPattern, @JsonProperty(value = "string_with_min") String stringWithMin, @JsonProperty(value = "string_with_max") String stringWithMax, @JsonProperty(value = "string_with_pattern_min_max") String stringWithPatternMinMax, @JsonProperty(value = "optional_string") Optional<String> optionalString) {

    public StringVariations {
        optionalString = optionalString == null ? Optional.empty() : optionalString;
    }
}
