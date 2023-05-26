package jopenapi.test.strings;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import java.util.Optional;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.Value;
import lombok.extern.jackson.Jacksonized;

@Value()
@Jacksonized()
@Getter(AccessLevel.NONE)
@Builder(toBuilder = true)
public class StringVariations {

    @JsonProperty(value = "as_date", access = JsonProperty.Access.AUTO)
    @NotNull()
    java.time.LocalDate asDate;

    public java.time.LocalDate getAsDate() {
        return asDate;
    }

    @JsonProperty(value = "as_date_time", access = JsonProperty.Access.AUTO)
    @NotNull()
    java.time.OffsetDateTime asDateTime;

    public java.time.OffsetDateTime getAsDateTime() {
        return asDateTime;
    }

    @JsonProperty(value = "as_uuid", access = JsonProperty.Access.AUTO)
    @NotNull()
    java.util.UUID asUuid;

    public java.util.UUID getAsUuid() {
        return asUuid;
    }

    @JsonProperty(value = "as_uri", access = JsonProperty.Access.AUTO)
    @NotNull()
    java.net.URI asUri;

    public java.net.URI getAsUri() {
        return asUri;
    }

    @Pattern(regexp = "^\\d+$")
    @JsonProperty(value = "string_with_pattern", access = JsonProperty.Access.AUTO)
    @NotNull()
    String stringWithPattern;

    public String getStringWithPattern() {
        return stringWithPattern;
    }

    @Size(min = 1, max = 2147483647)
    @JsonProperty(value = "string_with_min", access = JsonProperty.Access.AUTO)
    @NotNull()
    String stringWithMin;

    public String getStringWithMin() {
        return stringWithMin;
    }

    @Size(min = 0, max = 5)
    @JsonProperty(value = "string_with_max", access = JsonProperty.Access.AUTO)
    @NotNull()
    String stringWithMax;

    public String getStringWithMax() {
        return stringWithMax;
    }

    @Size(min = 1, max = 5)
    @Pattern(regexp = "^\\d+$")
    @JsonProperty(value = "string_with_pattern_min_max", access = JsonProperty.Access.AUTO)
    @NotNull()
    String stringWithPatternMinMax;

    public String getStringWithPatternMinMax() {
        return stringWithPatternMinMax;
    }

    @JsonProperty(value = "optional_string", access = JsonProperty.Access.AUTO)
    String optionalString;

    public Optional<String> getOptionalString() {
        return Optional.ofNullable(optionalString);
    }
}
