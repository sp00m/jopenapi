package jopenapi.test.more_features.defaults;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonValue;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import lombok.Builder;
import lombok.RequiredArgsConstructor;
import lombok.With;
import lombok.extern.jackson.Jacksonized;
import lombok.extern.slf4j.Slf4j;

@Jacksonized()
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

    public DefaultedValues {
        defaultedNumber = defaultedNumber == null ? new java.math.BigDecimal("5.5") : defaultedNumber;
        defaultedString = defaultedString == null ? "hello world" : defaultedString;
        defaultedDate = defaultedDate == null ? java.time.LocalDate.parse("2020-06-30") : defaultedDate;
        defaultedDatetime = defaultedDatetime == null ? java.time.OffsetDateTime.parse("2020-06-30T15:30:50.123+01:00") : defaultedDatetime;
        defaultedUuid = defaultedUuid == null ? java.util.UUID.fromString("cb855a07-11d3-432f-936f-cd79590482df") : defaultedUuid;
        defaultedUri = defaultedUri == null ? java.net.URI.create("https://github.com/sp00m/jopenapi") : defaultedUri;
        defaultedInternalEnum = defaultedInternalEnum == null ? DefaultedInternalEnum.BAR : defaultedInternalEnum;
    }

    public static class DefaultedValuesBuilder {

        private int defaultedInt = 1;

        private long defaultedLong = 2L;

        private float defaultedFloat = 3.3F;

        private double defaultedDouble = 4.4D;

        private boolean defaultedBoolean = true;
    }
}
