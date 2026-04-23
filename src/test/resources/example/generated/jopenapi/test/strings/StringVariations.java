package jopenapi.test.strings;

import com.fasterxml.jackson.annotation.*;
import com.github.jopenapi.support.*;
import jakarta.validation.constraints.*;
import java.math.*;
import java.net.*;
import java.time.*;
import java.util.*;
import java.util.function.*;
import java.util.stream.*;
import lombok.Builder;
import lombok.With;

@With()
@Builder(toBuilder = true)
public record StringVariations(@JsonProperty(value = "as_date") LocalDate asDate, @JsonProperty(value = "as_date_time") OffsetDateTime asDateTime, @JsonProperty(value = "as_uuid") UUID asUuid, @JsonProperty(value = "as_uri") URI asUri, @Pattern(regexp = "^\\d+$") @JsonProperty(value = "string_with_pattern") String stringWithPattern, @Size(min = 1) @JsonProperty(value = "string_with_min") String stringWithMin, @Size(max = 5) @JsonProperty(value = "string_with_max") String stringWithMax, @Size(min = 1, max = 5) @Pattern(regexp = "^\\d+$") @JsonProperty(value = "string_with_pattern_min_max") String stringWithPatternMinMax, @JsonProperty(value = "optional_string") Optional<String> optionalString) {

    public StringVariations {
        optionalString = Objects.requireNonNullElse(optionalString, Optional.empty());
    }

    @JsonCreator()
    static StringVariations create(@JsonProperty(value = "as_date") LocalDate asDate, @JsonProperty(value = "as_date_time") OffsetDateTime asDateTime, @JsonProperty(value = "as_uuid") UUID asUuid, @JsonProperty(value = "as_uri") URI asUri, @JsonProperty(value = "string_with_pattern") String stringWithPattern, @JsonProperty(value = "string_with_min") String stringWithMin, @JsonProperty(value = "string_with_max") String stringWithMax, @JsonProperty(value = "string_with_pattern_min_max") String stringWithPatternMinMax, @JsonProperty(value = "optional_string") String optionalString) {
        if (asDate == null) {
            throw new MissingPropertyException("as_date");
        }
        if (asDateTime == null) {
            throw new MissingPropertyException("as_date_time");
        }
        if (asUuid == null) {
            throw new MissingPropertyException("as_uuid");
        }
        if (asUri == null) {
            throw new MissingPropertyException("as_uri");
        }
        if (stringWithPattern == null) {
            throw new MissingPropertyException("string_with_pattern");
        }
        if (stringWithMin == null) {
            throw new MissingPropertyException("string_with_min");
        }
        if (stringWithMax == null) {
            throw new MissingPropertyException("string_with_max");
        }
        if (stringWithPatternMinMax == null) {
            throw new MissingPropertyException("string_with_pattern_min_max");
        }
        return new StringVariations(asDate, asDateTime, asUuid, asUri, stringWithPattern, stringWithMin, stringWithMax, stringWithPatternMinMax, Optional.ofNullable(optionalString));
    }
}
