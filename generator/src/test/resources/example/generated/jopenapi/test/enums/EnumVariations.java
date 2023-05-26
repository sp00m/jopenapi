package jopenapi.test.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonValue;
import jakarta.validation.constraints.NotNull;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Value;
import lombok.extern.jackson.Jacksonized;

@Value()
@Jacksonized()
@Getter(AccessLevel.NONE)
@Builder(toBuilder = true)
public class EnumVariations {

    @RequiredArgsConstructor()
    public enum EnumWithoutType {

        WITHOUT_TYPE("without type");

        private static final Map<String, EnumWithoutType> BY_VALUE = Stream.of(values()).collect(Collectors.toUnmodifiableMap(EnumWithoutType::get, Function.identity()));

        private final String value;

        @JsonValue()
        public String get() {
            return value;
        }

        @JsonCreator()
        public static EnumWithoutType get(String value) {
            return Optional.ofNullable(BY_VALUE.get(value)).orElseThrow(() -> new IllegalArgumentException("No EnumWithoutType with value " + value));
        }
    }

    @JsonProperty(value = "enum_without_type", access = JsonProperty.Access.AUTO)
    @NotNull()
    EnumWithoutType enumWithoutType;

    public EnumWithoutType getEnumWithoutType() {
        return enumWithoutType;
    }

    @RequiredArgsConstructor()
    public enum OptionalEnum {

        OPTIONAL("optional");

        private static final Map<String, OptionalEnum> BY_VALUE = Stream.of(values()).collect(Collectors.toUnmodifiableMap(OptionalEnum::get, Function.identity()));

        private final String value;

        @JsonValue()
        public String get() {
            return value;
        }

        @JsonCreator()
        public static OptionalEnum get(String value) {
            return Optional.ofNullable(BY_VALUE.get(value)).orElseThrow(() -> new IllegalArgumentException("No OptionalEnum with value " + value));
        }
    }

    @JsonProperty(value = "optional_enum", access = JsonProperty.Access.AUTO)
    OptionalEnum optionalEnum;

    public Optional<OptionalEnum> getOptionalEnum() {
        return Optional.ofNullable(optionalEnum);
    }

    @RequiredArgsConstructor()
    public enum NullableEnum {

        NULLABLE("nullable");

        private static final Map<String, NullableEnum> BY_VALUE = Stream.of(values()).collect(Collectors.toUnmodifiableMap(NullableEnum::get, Function.identity()));

        private final String value;

        @JsonValue()
        public String get() {
            return value;
        }

        @JsonCreator()
        public static NullableEnum get(String value) {
            return Optional.ofNullable(BY_VALUE.get(value)).orElseThrow(() -> new IllegalArgumentException("No NullableEnum with value " + value));
        }
    }

    @JsonProperty(value = "nullable_enum", access = JsonProperty.Access.AUTO)
    @NotNull()
    NullableEnum nullableEnum;

    public NullableEnum getNullableEnum() {
        return nullableEnum;
    }

    @RequiredArgsConstructor()
    public enum EnumWithNull {

        WITH_NULL("with null");

        private static final Map<String, EnumWithNull> BY_VALUE = Stream.of(values()).collect(Collectors.toUnmodifiableMap(EnumWithNull::get, Function.identity()));

        private final String value;

        @JsonValue()
        public String get() {
            return value;
        }

        @JsonCreator()
        public static EnumWithNull get(String value) {
            return Optional.ofNullable(BY_VALUE.get(value)).orElseThrow(() -> new IllegalArgumentException("No EnumWithNull with value " + value));
        }
    }

    @JsonProperty(value = "enum_with_null", access = JsonProperty.Access.AUTO)
    @NotNull()
    EnumWithNull enumWithNull;

    public EnumWithNull getEnumWithNull() {
        return enumWithNull;
    }

    @RequiredArgsConstructor()
    public enum NullableEnumWithNull {

        WITH_NULL("with null");

        private static final Map<String, NullableEnumWithNull> BY_VALUE = Stream.of(values()).collect(Collectors.toUnmodifiableMap(NullableEnumWithNull::get, Function.identity()));

        private final String value;

        @JsonValue()
        public String get() {
            return value;
        }

        @JsonCreator()
        public static NullableEnumWithNull get(String value) {
            return Optional.ofNullable(BY_VALUE.get(value)).orElseThrow(() -> new IllegalArgumentException("No NullableEnumWithNull with value " + value));
        }
    }

    @JsonProperty(value = "nullable_enum_with_null", access = JsonProperty.Access.AUTO)
    NullableEnumWithNull nullableEnumWithNull;

    public Optional<NullableEnumWithNull> getNullableEnumWithNull() {
        return Optional.ofNullable(nullableEnumWithNull);
    }
}
