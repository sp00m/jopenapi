package com.github.sp00m.jopenapi.read;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonUnwrapped;
import com.github.javaparser.ast.body.FieldDeclaration;
import com.github.sp00m.jopenapi.read.vo.OpenApiProperty;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.extern.slf4j.Slf4j;

import java.util.Optional;

@Slf4j
public enum JavaFieldAnnotator {

    MIN {
        @Override
        public void annotate(FieldDeclaration fieldDeclaration, OpenApiProperty property) {
            if (property.getSchema().getMinimum() == null) {
                return;
            }
            var min = property.getSchema().getMinimum().toString();
            var exclusive = Boolean.TRUE.equals(property.getSchema().getExclusiveMinimum());
            fieldDeclaration
                    .addAndGetAnnotation(DecimalMin.class)
                    .addPair("value", "\"%s\"".formatted(min))
                    .addPair("inclusive", "%b".formatted(!exclusive));
        }
    },

    MAX {
        @Override
        public void annotate(FieldDeclaration fieldDeclaration, OpenApiProperty property) {
            if (property.getSchema().getMaximum() == null) {
                return;
            }
            var max = property.getSchema().getMaximum().toString();
            var exclusive = Boolean.TRUE.equals(property.getSchema().getExclusiveMaximum());
            fieldDeclaration
                    .addAndGetAnnotation(DecimalMax.class)
                    .addPair("value", "\"%s\"".formatted(max))
                    .addPair("inclusive", "%b".formatted(!exclusive));
        }
    },

    MULTIPLE_OF {
        @Override
        public void annotate(FieldDeclaration fieldDeclaration, OpenApiProperty property) {
            if (property.getSchema().getMultipleOf() == null) {
                return;
            }
            log.warn("'multipleOf' not supported");
        }
    },

    SIZE {
        @Override
        public void annotate(FieldDeclaration fieldDeclaration, OpenApiProperty property) {
            if (property.getSchema().getMinLength() == null && property.getSchema().getMaxLength() == null
                    && property.getSchema().getMinItems() == null && property.getSchema().getMaxItems() == null
                    && property.getSchema().getMinProperties() == null && property.getSchema().getMaxProperties() == null) {
                return;
            }
            var min = Optional.ofNullable(property.getSchema().getMinLength())
                    .or(() -> Optional.ofNullable(property.getSchema().getMinItems()))
                    .or(() -> Optional.ofNullable(property.getSchema().getMinProperties()))
                    .orElse(0);
            var max = Optional.ofNullable(property.getSchema().getMaxLength())
                    .or(() -> Optional.ofNullable(property.getSchema().getMaxItems()))
                    .or(() -> Optional.ofNullable(property.getSchema().getMaxProperties()))
                    .orElse(Integer.MAX_VALUE);
            fieldDeclaration
                    .addAndGetAnnotation(Size.class)
                    .addPair("min", "%d".formatted(min))
                    .addPair("max", "%d".formatted(max));
        }
    },

    PATTERN {
        @Override
        public void annotate(FieldDeclaration fieldDeclaration, OpenApiProperty property) {
            if (property.getSchema().getPattern() == null) {
                return;
            }
            var pattern = property.getSchema().getPattern().replace("\\", "\\\\");
            fieldDeclaration
                    .addAndGetAnnotation(Pattern.class)
                    .addPair("regexp", "\"%s\"".formatted(pattern));
        }
    },

    JSON_UNWRAPPED {
        @Override
        public void annotate(FieldDeclaration fieldDeclaration, OpenApiProperty property) {
            fieldDeclaration.addAnnotation(JsonUnwrapped.class);
        }
    },

    JSON_PROPERTY {
        @Override
        public void annotate(FieldDeclaration fieldDeclaration, OpenApiProperty property) {
            var value = "\"%s\"".formatted(property.getName());
            var access = "%s.%s.%s".formatted(
                    JsonProperty.class.getSimpleName(),
                    JsonProperty.Access.class.getSimpleName(),
                    getAccess(property).name()
            );
            fieldDeclaration
                    .addAndGetAnnotation(JsonProperty.class)
                    .addPair("value", value)
                    .addPair("access", access);
        }

        private JsonProperty.Access getAccess(OpenApiProperty property) {
            if (Boolean.TRUE.equals(property.getSchema().getReadOnly())) {
                return JsonProperty.Access.READ_ONLY;
            } else if (Boolean.TRUE.equals(property.getSchema().getWriteOnly())) {
                return JsonProperty.Access.WRITE_ONLY;
            } else {
                return JsonProperty.Access.AUTO;
            }
        }
    };

    public abstract void annotate(FieldDeclaration fieldDeclaration, OpenApiProperty property);

}
