package jopenapi.test.more_features.defaults;

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
import lombok.RequiredArgsConstructor;
import lombok.With;
import lombok.extern.slf4j.Slf4j;

@With()
@Builder(toBuilder = true)
public record DefaultedValues(@JsonProperty(value = "defaulted_int") int defaultedInt, @JsonProperty(value = "defaulted_long") long defaultedLong, @JsonProperty(value = "defaulted_float") float defaultedFloat, @JsonProperty(value = "defaulted_double") double defaultedDouble, @JsonProperty(value = "defaulted_number") Number defaultedNumber, @JsonProperty(value = "defaulted_boolean") boolean defaultedBoolean, @JsonProperty(value = "defaulted_string") String defaultedString, @JsonProperty(value = "defaulted_date") java.time.LocalDate defaultedDate, @JsonProperty(value = "defaulted_datetime") java.time.OffsetDateTime defaultedDatetime, @JsonProperty(value = "defaulted_uuid") java.util.UUID defaultedUuid, @JsonProperty(value = "defaulted_uri") java.net.URI defaultedUri, @JsonProperty(value = "defaulted_internal_enum") DefaultedInternalEnum defaultedInternalEnum) {

    @RequiredArgsConstructor()
    @Slf4j()
    public enum DefaultedInternalEnum {

        FOO("foo"), BAR("bar");

        private static final Map<String, DefaultedInternalEnum> BY_VALUE = Stream.of(values()).collect(Collectors.toUnmodifiableMap(DefaultedInternalEnum::get, Function.identity()));

        private final String value;

        @JsonValue()
        public String value() {
            return value;
        }

        @JsonCreator()
        public static DefaultedInternalEnum findByValue(String value) {
            return Optional.ofNullable(value).map(BY_VALUE::get).orElseGet(() -> {
                log.warn("No DefaultedInternalEnum with value {}", value);
                return DefaultedInternalEnum.BAR;
            });
        }
    }

    @JsonCreator()
    static DefaultedValues create(@JsonProperty(value = "defaulted_int") Integer defaultedInt, @JsonProperty(value = "defaulted_long") Long defaultedLong, @JsonProperty(value = "defaulted_float") Float defaultedFloat, @JsonProperty(value = "defaulted_double") Double defaultedDouble, @JsonProperty(value = "defaulted_number") Number defaultedNumber, @JsonProperty(value = "defaulted_boolean") Boolean defaultedBoolean, @JsonProperty(value = "defaulted_string") String defaultedString, @JsonProperty(value = "defaulted_date") java.time.LocalDate defaultedDate, @JsonProperty(value = "defaulted_datetime") java.time.OffsetDateTime defaultedDatetime, @JsonProperty(value = "defaulted_uuid") java.util.UUID defaultedUuid, @JsonProperty(value = "defaulted_uri") java.net.URI defaultedUri, @JsonProperty(value = "defaulted_internal_enum") DefaultedInternalEnum defaultedInternalEnum) {
        return new DefaultedValues(Objects.requireNonNullElse(defaultedInt, 1), Objects.requireNonNullElse(defaultedLong, 2L), Objects.requireNonNullElse(defaultedFloat, 3.3F), Objects.requireNonNullElse(defaultedDouble, 4.4D), Objects.requireNonNullElse(defaultedNumber, new BigDecimal("5.5")), Objects.requireNonNullElse(defaultedBoolean, true), Objects.requireNonNullElse(defaultedString, "hello world"), Objects.requireNonNullElse(defaultedDate, LocalDate.parse("2020-06-30")), Objects.requireNonNullElse(defaultedDatetime, OffsetDateTime.parse("2020-06-30T15:30:50.123+01:00")), Objects.requireNonNullElse(defaultedUuid, UUID.fromString("cb855a07-11d3-432f-936f-cd79590482df")), Objects.requireNonNullElse(defaultedUri, URI.create("https://github.com/sp00m/jopenapi")), Objects.requireNonNullElse(defaultedInternalEnum, DefaultedInternalEnum.BAR));
    }
}
