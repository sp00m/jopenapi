package com.github.sp00m.jopenapi.read;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import java.util.Optional;

public enum JavaFieldAnnotator {

    MAX {
        @Override
        public String annotate(String type, OpenApiProperty property) {
            if (property.getSchema().getMaximum() == null) {
                return type;
            }
            var max = property.getSchema().getMaximum().toString();
            var exclusive = Boolean.TRUE.equals(property.getSchema().getExclusiveMaximum());
            return "@%s(value = \"%s\", inclusive = %b) %s".formatted(
                    DecimalMax.class.getName(),
                    max,
                    !exclusive,
                    type
            );
        }
    },

    MIN {
        @Override
        public String annotate(String type, OpenApiProperty property) {
            if (property.getSchema().getMinimum() == null) {
                return type;
            }
            var min = property.getSchema().getMinimum().toString();
            var exclusive = Boolean.TRUE.equals(property.getSchema().getExclusiveMinimum());
            return "@%s(value = \"%s\", inclusive = %b) %s".formatted(
                    DecimalMin.class.getName(),
                    min,
                    !exclusive,
                    type
            );
        }
    },

    PATTERN {
        @Override
        public String annotate(String type, OpenApiProperty property) {
            if (property.getSchema().getPattern() == null) {
                return type;
            }
            return "@%s(regexp = \"%s\") %s".formatted(
                    Pattern.class.getName(),
                    property.getSchema().getPattern().replace("\\", "\\\\"),
                    type
            );
        }
    },

    SIZE {
        @Override
        public String annotate(String type, OpenApiProperty property) {
            if (property.getSchema().getMinLength() == null && property.getSchema().getMaxLength() == null
                    && property.getSchema().getMinItems() == null && property.getSchema().getMaxItems() == null) {
                return type;
            }
            var min = Optional.ofNullable(property.getSchema().getMinLength())
                    .or(() -> Optional.ofNullable(property.getSchema().getMinItems()))
                    .orElse(0);
            var max = Optional.ofNullable(property.getSchema().getMaxLength())
                    .or(() -> Optional.ofNullable(property.getSchema().getMaxItems()))
                    .orElse(Integer.MAX_VALUE);
            return "@%s(min = %d, max = %d) %s".formatted(
                    Size.class.getName(),
                    min,
                    max,
                    type
            );
        }
    },

    JSON_PROPERTY {
        @Override
        public String annotate(String type, OpenApiProperty property) {
            return getAccess(property)
                    .map(access -> withAccess(type, property, access))
                    .orElseGet(() -> withoutAccess(type, property));
        }

        private String withAccess(String type, OpenApiProperty property, JsonProperty.Access access) {
            return "@%s(value = \"%s\", access = %s.%s.%s) %s".formatted(
                    JsonProperty.class.getName(),
                    property.getName(),
                    JsonProperty.class.getName(),
                    JsonProperty.Access.class.getSimpleName(),
                    access.name(),
                    type
            );
        }

        private String withoutAccess(String type, OpenApiProperty property) {
            return "@%s(value = \"%s\") %s".formatted(
                    JsonProperty.class.getName(),
                    property.getName(),
                    type
            );
        }

        private Optional<JsonProperty.Access> getAccess(OpenApiProperty property) {
            if (Boolean.TRUE.equals(property.getSchema().getReadOnly())) {
                return Optional.of(JsonProperty.Access.READ_ONLY);
            } else if (Boolean.TRUE.equals(property.getSchema().getWriteOnly())) {
                return Optional.of(JsonProperty.Access.WRITE_ONLY);
            } else {
                return Optional.empty();
            }
        }
    };

    public abstract String annotate(String type, OpenApiProperty property);

}
