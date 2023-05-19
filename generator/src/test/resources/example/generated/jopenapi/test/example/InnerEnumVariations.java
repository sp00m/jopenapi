package jopenapi.test.example;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonValue;
import jakarta.validation.constraints.NotNull;
import java.util.Optional;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Value;
import lombok.extern.jackson.Jacksonized;

@Value()
@Builder()
@Jacksonized()
@Getter(AccessLevel.NONE)
public class InnerEnumVariations {

    @RequiredArgsConstructor()
    public enum EnumWithoutType {

        WITHOUT_TYPE("without type");

        private static final java.util.Map<String, EnumWithoutType> BY_VALUE = java.util.stream.Stream.of(values()).collect(java.util.stream.Collectors.toUnmodifiableMap(EnumWithoutType::getValue, java.util.function.Function.identity()));

        private final String value;

        @JsonValue()
        public String get() {
            return value;
        }

        @JsonCreator()
        public static EnumWithoutType get(String value) {
            return java.util.Optional.ofNullable(BY_VALUE.get(value)).orElseThrow(() -> new IllegalArgumentException("No EnumWithoutType with value " + value));
        }
    }

    @JsonProperty(value = "enum_without_type", access = JsonProperty.Access.AUTO)
    @NotNull()
    EnumWithoutType enumWithoutType;

    public EnumWithoutType getEnumWithoutType() {
        return enumWithoutType;
    }

    @RequiredArgsConstructor()
    public enum MandatoryEnum {

        MANDATORY("mandatory");

        private static final java.util.Map<String, MandatoryEnum> BY_VALUE = java.util.stream.Stream.of(values()).collect(java.util.stream.Collectors.toUnmodifiableMap(MandatoryEnum::getValue, java.util.function.Function.identity()));

        private final String value;

        @JsonValue()
        public String get() {
            return value;
        }

        @JsonCreator()
        public static MandatoryEnum get(String value) {
            return java.util.Optional.ofNullable(BY_VALUE.get(value)).orElseThrow(() -> new IllegalArgumentException("No MandatoryEnum with value " + value));
        }
    }

    @JsonProperty(value = "mandatory_enum", access = JsonProperty.Access.AUTO)
    @NotNull()
    MandatoryEnum mandatoryEnum;

    public MandatoryEnum getMandatoryEnum() {
        return mandatoryEnum;
    }

    @RequiredArgsConstructor()
    public enum OptionalEnum {

        OPTIONAL("optional");

        private static final java.util.Map<String, OptionalEnum> BY_VALUE = java.util.stream.Stream.of(values()).collect(java.util.stream.Collectors.toUnmodifiableMap(OptionalEnum::getValue, java.util.function.Function.identity()));

        private final String value;

        @JsonValue()
        public String get() {
            return value;
        }

        @JsonCreator()
        public static OptionalEnum get(String value) {
            return java.util.Optional.ofNullable(BY_VALUE.get(value)).orElseThrow(() -> new IllegalArgumentException("No OptionalEnum with value " + value));
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

        private static final java.util.Map<String, NullableEnum> BY_VALUE = java.util.stream.Stream.of(values()).collect(java.util.stream.Collectors.toUnmodifiableMap(NullableEnum::getValue, java.util.function.Function.identity()));

        private final String value;

        @JsonValue()
        public String get() {
            return value;
        }

        @JsonCreator()
        public static NullableEnum get(String value) {
            return java.util.Optional.ofNullable(BY_VALUE.get(value)).orElseThrow(() -> new IllegalArgumentException("No NullableEnum with value " + value));
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

        private static final java.util.Map<String, EnumWithNull> BY_VALUE = java.util.stream.Stream.of(values()).collect(java.util.stream.Collectors.toUnmodifiableMap(EnumWithNull::getValue, java.util.function.Function.identity()));

        private final String value;

        @JsonValue()
        public String get() {
            return value;
        }

        @JsonCreator()
        public static EnumWithNull get(String value) {
            return java.util.Optional.ofNullable(BY_VALUE.get(value)).orElseThrow(() -> new IllegalArgumentException("No EnumWithNull with value " + value));
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

        private static final java.util.Map<String, NullableEnumWithNull> BY_VALUE = java.util.stream.Stream.of(values()).collect(java.util.stream.Collectors.toUnmodifiableMap(NullableEnumWithNull::getValue, java.util.function.Function.identity()));

        private final String value;

        @JsonValue()
        public String get() {
            return value;
        }

        @JsonCreator()
        public static NullableEnumWithNull get(String value) {
            return java.util.Optional.ofNullable(BY_VALUE.get(value)).orElseThrow(() -> new IllegalArgumentException("No NullableEnumWithNull with value " + value));
        }
    }

    @JsonProperty(value = "nullable_enum_with_null", access = JsonProperty.Access.AUTO)
    NullableEnumWithNull nullableEnumWithNull;

    public Optional<NullableEnumWithNull> getNullableEnumWithNull() {
        return Optional.ofNullable(nullableEnumWithNull);
    }
}
