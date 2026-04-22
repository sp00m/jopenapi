package jopenapi.test.enums;

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

@With()
@Builder(toBuilder = true)
public record EnumVariations(@JsonProperty(value = "enum_without_type") EnumWithoutType enumWithoutType, @JsonProperty(value = "optional_enum") Optional<OptionalEnum> optionalEnum, @JsonProperty(value = "nullable_enum") NullableEnum nullableEnum, @JsonProperty(value = "enum_with_null") EnumWithNull enumWithNull, @JsonProperty(value = "nullable_enum_with_null") Optional<NullableEnumWithNull> nullableEnumWithNull) {

    @RequiredArgsConstructor()
    public enum EnumWithoutType {

        WITHOUT_TYPE("without type");

        private static final Map<String, EnumWithoutType> BY_VALUE = Stream.of(values()).collect(Collectors.toUnmodifiableMap(EnumWithoutType::value, Function.identity()));

        private final String value;

        @JsonValue()
        public String value() {
            return value;
        }

        @JsonCreator()
        public static EnumWithoutType findByValue(String value) {
            return Optional.ofNullable(value).map(BY_VALUE::get).orElseThrow(() -> new InvalidPropertyException("EnumWithoutType", value));
        }
    }

    @RequiredArgsConstructor()
    public enum OptionalEnum {

        OPTIONAL("optional");

        private static final Map<String, OptionalEnum> BY_VALUE = Stream.of(values()).collect(Collectors.toUnmodifiableMap(OptionalEnum::value, Function.identity()));

        private final String value;

        @JsonValue()
        public String value() {
            return value;
        }

        @JsonCreator()
        public static OptionalEnum findByValue(String value) {
            return Optional.ofNullable(value).map(BY_VALUE::get).orElseThrow(() -> new InvalidPropertyException("OptionalEnum", value));
        }
    }

    @RequiredArgsConstructor()
    public enum NullableEnum {

        NULLABLE("nullable");

        private static final Map<String, NullableEnum> BY_VALUE = Stream.of(values()).collect(Collectors.toUnmodifiableMap(NullableEnum::value, Function.identity()));

        private final String value;

        @JsonValue()
        public String value() {
            return value;
        }

        @JsonCreator()
        public static NullableEnum findByValue(String value) {
            return Optional.ofNullable(value).map(BY_VALUE::get).orElseThrow(() -> new InvalidPropertyException("NullableEnum", value));
        }
    }

    @RequiredArgsConstructor()
    public enum EnumWithNull {

        WITH_NULL("with null");

        private static final Map<String, EnumWithNull> BY_VALUE = Stream.of(values()).collect(Collectors.toUnmodifiableMap(EnumWithNull::value, Function.identity()));

        private final String value;

        @JsonValue()
        public String value() {
            return value;
        }

        @JsonCreator()
        public static EnumWithNull findByValue(String value) {
            return Optional.ofNullable(value).map(BY_VALUE::get).orElseThrow(() -> new InvalidPropertyException("EnumWithNull", value));
        }
    }

    @RequiredArgsConstructor()
    public enum NullableEnumWithNull {

        WITH_NULL("with null");

        private static final Map<String, NullableEnumWithNull> BY_VALUE = Stream.of(values()).collect(Collectors.toUnmodifiableMap(NullableEnumWithNull::value, Function.identity()));

        private final String value;

        @JsonValue()
        public String value() {
            return value;
        }

        @JsonCreator()
        public static NullableEnumWithNull findByValue(String value) {
            return Optional.ofNullable(value).map(BY_VALUE::get).orElseThrow(() -> new InvalidPropertyException("NullableEnumWithNull", value));
        }
    }

    public EnumVariations {
        optionalEnum = Objects.requireNonNullElse(optionalEnum, Optional.empty());
        nullableEnumWithNull = Objects.requireNonNullElse(nullableEnumWithNull, Optional.empty());
    }

    @JsonCreator()
    static EnumVariations create(@JsonProperty(value = "enum_without_type") EnumWithoutType enumWithoutType, @JsonProperty(value = "optional_enum") OptionalEnum optionalEnum, @JsonProperty(value = "nullable_enum") NullableEnum nullableEnum, @JsonProperty(value = "enum_with_null") EnumWithNull enumWithNull, @JsonProperty(value = "nullable_enum_with_null") NullableEnumWithNull nullableEnumWithNull) {
        if (enumWithoutType == null) {
            throw new MissingPropertyException("enum_without_type");
        }
        if (nullableEnum == null) {
            throw new MissingPropertyException("nullable_enum");
        }
        if (enumWithNull == null) {
            throw new MissingPropertyException("enum_with_null");
        }
        return new EnumVariations(enumWithoutType, Optional.ofNullable(optionalEnum), nullableEnum, enumWithNull, Optional.ofNullable(nullableEnumWithNull));
    }
}
