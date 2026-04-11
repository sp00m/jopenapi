package jopenapi.test.strings;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import java.util.Objects;
import java.util.Optional;
import lombok.Builder;
import lombok.With;

@With()
@Builder(toBuilder = true)
public record StringVariations(@JsonProperty(value = "as_date") java.time.LocalDate asDate, @JsonProperty(value = "as_date_time") java.time.OffsetDateTime asDateTime, @JsonProperty(value = "as_uuid") java.util.UUID asUuid, @JsonProperty(value = "as_uri") java.net.URI asUri, @Pattern(regexp = "^\\d+$") @JsonProperty(value = "string_with_pattern") String stringWithPattern, @Size(min = 1, max = 2147483647) @JsonProperty(value = "string_with_min") String stringWithMin, @Size(min = 0, max = 5) @JsonProperty(value = "string_with_max") String stringWithMax, @Size(min = 1, max = 5) @Pattern(regexp = "^\\d+$") @JsonProperty(value = "string_with_pattern_min_max") String stringWithPatternMinMax, @JsonProperty(value = "optional_string") Optional<String> optionalString) {

    public StringVariations {
        optionalString = Objects.requireNonNullElse(optionalString, Optional.empty());
    }

    @JsonCreator()
    public static StringVariations create(@JsonProperty(value = "as_date") java.time.LocalDate asDate, @JsonProperty(value = "as_date_time") java.time.OffsetDateTime asDateTime, @JsonProperty(value = "as_uuid") java.util.UUID asUuid, @JsonProperty(value = "as_uri") java.net.URI asUri, @JsonProperty(value = "string_with_pattern") String stringWithPattern, @JsonProperty(value = "string_with_min") String stringWithMin, @JsonProperty(value = "string_with_max") String stringWithMax, @JsonProperty(value = "string_with_pattern_min_max") String stringWithPatternMinMax, @JsonProperty(value = "optional_string") String optionalString) {
        if (asDate == null) {
            throw new IllegalArgumentException("Property 'as_date' is required");
        }
        if (asDateTime == null) {
            throw new IllegalArgumentException("Property 'as_date_time' is required");
        }
        if (asUuid == null) {
            throw new IllegalArgumentException("Property 'as_uuid' is required");
        }
        if (asUri == null) {
            throw new IllegalArgumentException("Property 'as_uri' is required");
        }
        if (stringWithPattern == null) {
            throw new IllegalArgumentException("Property 'string_with_pattern' is required");
        }
        if (stringWithMin == null) {
            throw new IllegalArgumentException("Property 'string_with_min' is required");
        }
        if (stringWithMax == null) {
            throw new IllegalArgumentException("Property 'string_with_max' is required");
        }
        if (stringWithPatternMinMax == null) {
            throw new IllegalArgumentException("Property 'string_with_pattern_min_max' is required");
        }
        return new StringVariations(asDate, asDateTime, asUuid, asUri, stringWithPattern, stringWithMin, stringWithMax, stringWithPatternMinMax, Optional.ofNullable(optionalString));
    }
}
