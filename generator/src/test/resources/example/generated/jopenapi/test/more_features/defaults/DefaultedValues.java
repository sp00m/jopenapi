package jopenapi.test.more_features.defaults;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonValue;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Builder.Default;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Value;
import lombok.extern.jackson.Jacksonized;

@Value()
@Jacksonized()
@Getter(AccessLevel.NONE)
@Builder(toBuilder = true)
public class DefaultedValues {

    @Default()
    @JsonProperty(value = "defaulted_int", access = JsonProperty.Access.AUTO)
    Integer defaultedInt = 1;

    public int getDefaultedInt() {
        return defaultedInt == null ? 1 : defaultedInt;
    }

    @Default()
    @JsonProperty(value = "defaulted_long", access = JsonProperty.Access.AUTO)
    Long defaultedLong = 2L;

    public long getDefaultedLong() {
        return defaultedLong == null ? 2L : defaultedLong;
    }

    @Default()
    @JsonProperty(value = "defaulted_float", access = JsonProperty.Access.AUTO)
    Float defaultedFloat = 3.3F;

    public float getDefaultedFloat() {
        return defaultedFloat == null ? 3.3F : defaultedFloat;
    }

    @Default()
    @JsonProperty(value = "defaulted_double", access = JsonProperty.Access.AUTO)
    Double defaultedDouble = 4.4D;

    public double getDefaultedDouble() {
        return defaultedDouble == null ? 4.4D : defaultedDouble;
    }

    @Default()
    @JsonProperty(value = "defaulted_number", access = JsonProperty.Access.AUTO)
    Number defaultedNumber = new java.math.BigDecimal("5.5");

    public Number getDefaultedNumber() {
        return defaultedNumber == null ? new java.math.BigDecimal("5.5") : defaultedNumber;
    }

    @Default()
    @JsonProperty(value = "defaulted_boolean", access = JsonProperty.Access.AUTO)
    Boolean defaultedBoolean = true;

    public boolean isDefaultedBoolean() {
        return defaultedBoolean == null ? true : defaultedBoolean;
    }

    @Default()
    @JsonProperty(value = "defaulted_string", access = JsonProperty.Access.AUTO)
    String defaultedString = "hello world";

    public String getDefaultedString() {
        return defaultedString == null ? "hello world" : defaultedString;
    }

    @Default()
    @JsonProperty(value = "defaulted_date", access = JsonProperty.Access.AUTO)
    java.time.LocalDate defaultedDate = java.time.LocalDate.parse("2020-06-30");

    public java.time.LocalDate getDefaultedDate() {
        return defaultedDate == null ? java.time.LocalDate.parse("2020-06-30") : defaultedDate;
    }

    @Default()
    @JsonProperty(value = "defaulted_datetime", access = JsonProperty.Access.AUTO)
    java.time.OffsetDateTime defaultedDatetime = java.time.OffsetDateTime.parse("2020-06-30T15:30:50.123+01:00");

    public java.time.OffsetDateTime getDefaultedDatetime() {
        return defaultedDatetime == null ? java.time.OffsetDateTime.parse("2020-06-30T15:30:50.123+01:00") : defaultedDatetime;
    }

    @Default()
    @JsonProperty(value = "defaulted_uuid", access = JsonProperty.Access.AUTO)
    java.util.UUID defaultedUuid = java.util.UUID.fromString("cb855a07-11d3-432f-936f-cd79590482df");

    public java.util.UUID getDefaultedUuid() {
        return defaultedUuid == null ? java.util.UUID.fromString("cb855a07-11d3-432f-936f-cd79590482df") : defaultedUuid;
    }

    @Default()
    @JsonProperty(value = "defaulted_uri", access = JsonProperty.Access.AUTO)
    java.net.URI defaultedUri = java.net.URI.create("https://github.com/sp00m/jopenapi");

    public java.net.URI getDefaultedUri() {
        return defaultedUri == null ? java.net.URI.create("https://github.com/sp00m/jopenapi") : defaultedUri;
    }

    @RequiredArgsConstructor()
    public enum DefaultedInternalEnum {

        FOO("foo"), BAR("bar");

        private static final Map<String, DefaultedInternalEnum> BY_VALUE = Stream.of(values()).collect(Collectors.toUnmodifiableMap(DefaultedInternalEnum::get, Function.identity()));

        private final String value;

        @JsonValue()
        public String get() {
            return value;
        }

        @JsonCreator()
        public static DefaultedInternalEnum get(String value) {
            return Optional.ofNullable(BY_VALUE.get(value)).orElseThrow(() -> new IllegalArgumentException("No DefaultedInternalEnum with value " + value));
        }
    }

    @Default()
    @JsonProperty(value = "defaulted_internal_enum", access = JsonProperty.Access.AUTO)
    DefaultedInternalEnum defaultedInternalEnum = DefaultedInternalEnum.BAR;

    public DefaultedInternalEnum getDefaultedInternalEnum() {
        return defaultedInternalEnum == null ? DefaultedInternalEnum.BAR : defaultedInternalEnum;
    }
}
