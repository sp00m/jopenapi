package jopenapi.test.enums;

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

@Jacksonized()
@With()
@Builder(toBuilder = true)
public record EnumVariations(@JsonProperty(value = "enum_without_type") EnumWithoutType enumWithoutType, @JsonProperty(value = "optional_enum") Optional<OptionalEnum> optionalEnum, @JsonProperty(value = "nullable_enum") NullableEnum nullableEnum, @JsonProperty(value = "enum_with_null") EnumWithNull enumWithNull, @JsonProperty(value = "nullable_enum_with_null") Optional<NullableEnumWithNull> nullableEnumWithNull) {

    @RequiredArgsConstructor()
    public enum EnumWithoutType {

        WITHOUT_TYPE("without type");

        private static final Map<String, EnumWithoutType> BY_VALUE = Stream.of(values()).collect(Collectors.toUnmodifiableMap(EnumWithoutType::get, Function.identity()));

        private final String value;

        @JsonValue()
        public String value() {
            return value;
        }

        @JsonCreator()
        public static EnumWithoutType findByValue(String value) {
            return Optional.ofNullable(BY_VALUE.get(value)).orElseThrow(() -> new IllegalArgumentException("No EnumWithoutType with value " + value));
        }
    }

    @RequiredArgsConstructor()
    public enum OptionalEnum {

        OPTIONAL("optional");

        private static final Map<String, OptionalEnum> BY_VALUE = Stream.of(values()).collect(Collectors.toUnmodifiableMap(OptionalEnum::get, Function.identity()));

        private final String value;

        @JsonValue()
        public String value() {
            return value;
        }

        @JsonCreator()
        public static OptionalEnum findByValue(String value) {
            return Optional.ofNullable(BY_VALUE.get(value)).orElseThrow(() -> new IllegalArgumentException("No OptionalEnum with value " + value));
        }
    }

    @RequiredArgsConstructor()
    public enum NullableEnum {

        NULLABLE("nullable");

        private static final Map<String, NullableEnum> BY_VALUE = Stream.of(values()).collect(Collectors.toUnmodifiableMap(NullableEnum::get, Function.identity()));

        private final String value;

        @JsonValue()
        public String value() {
            return value;
        }

        @JsonCreator()
        public static NullableEnum findByValue(String value) {
            return Optional.ofNullable(BY_VALUE.get(value)).orElseThrow(() -> new IllegalArgumentException("No NullableEnum with value " + value));
        }
    }

    @RequiredArgsConstructor()
    public enum EnumWithNull {

        WITH_NULL("with null");

        private static final Map<String, EnumWithNull> BY_VALUE = Stream.of(values()).collect(Collectors.toUnmodifiableMap(EnumWithNull::get, Function.identity()));

        private final String value;

        @JsonValue()
        public String value() {
            return value;
        }

        @JsonCreator()
        public static EnumWithNull findByValue(String value) {
            return Optional.ofNullable(BY_VALUE.get(value)).orElseThrow(() -> new IllegalArgumentException("No EnumWithNull with value " + value));
        }
    }

    @RequiredArgsConstructor()
    public enum NullableEnumWithNull {

        WITH_NULL("with null");

        private static final Map<String, NullableEnumWithNull> BY_VALUE = Stream.of(values()).collect(Collectors.toUnmodifiableMap(NullableEnumWithNull::get, Function.identity()));

        private final String value;

        @JsonValue()
        public String value() {
            return value;
        }

        @JsonCreator()
        public static NullableEnumWithNull findByValue(String value) {
            return Optional.ofNullable(BY_VALUE.get(value)).orElseThrow(() -> new IllegalArgumentException("No NullableEnumWithNull with value " + value));
        }
    }

    public EnumVariations {
        optionalEnum = optionalEnum == null ? Optional.empty() : optionalEnum;
        nullableEnumWithNull = nullableEnumWithNull == null ? Optional.empty() : nullableEnumWithNull;
    }
}
