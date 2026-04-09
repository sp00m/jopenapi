package com.github.sp00m.jopenapi.read;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonProperty.Access;
import com.fasterxml.jackson.annotation.JsonUnwrapped;
import com.github.javaparser.ast.nodeTypes.NodeWithAnnotations;
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
        public void annotate(NodeWithAnnotations<?> node, OpenApiProperty property) {
            if (property.schema().getMinimum() == null) {
                return;
            }
            var min = property.schema().getMinimum().toString();
            var exclusive = Boolean.TRUE.equals(property.schema().getExclusiveMinimum());
            node
                    .addAndGetAnnotation(DecimalMin.class)
                    .addPair("value", "\"%s\"".formatted(min))
                    .addPair("inclusive", "%b".formatted(!exclusive));
        }
    },

    MAX {
        @Override
        public void annotate(NodeWithAnnotations<?> node, OpenApiProperty property) {
            if (property.schema().getMaximum() == null) {
                return;
            }
            var max = property.schema().getMaximum().toString();
            var exclusive = Boolean.TRUE.equals(property.schema().getExclusiveMaximum());
            node
                    .addAndGetAnnotation(DecimalMax.class)
                    .addPair("value", "\"%s\"".formatted(max))
                    .addPair("inclusive", "%b".formatted(!exclusive));
        }
    },

    MULTIPLE_OF {
        @Override
        public void annotate(NodeWithAnnotations<?> node, OpenApiProperty property) {
            if (property.schema().getMultipleOf() == null) {
                return;
            }
            log.warn("'multipleOf' not supported");
        }
    },

    SIZE {
        @Override
        public void annotate(NodeWithAnnotations<?> node, OpenApiProperty property) {
            if (property.schema().getMinLength() == null && property.schema().getMaxLength() == null
                    && property.schema().getMinItems() == null && property.schema().getMaxItems() == null
                    && property.schema().getMinProperties() == null && property.schema().getMaxProperties() == null) {
                return;
            }
            var min = Optional.ofNullable(property.schema().getMinLength())
                    .or(() -> Optional.ofNullable(property.schema().getMinItems()))
                    .or(() -> Optional.ofNullable(property.schema().getMinProperties()))
                    .orElse(0);
            var max = Optional.ofNullable(property.schema().getMaxLength())
                    .or(() -> Optional.ofNullable(property.schema().getMaxItems()))
                    .or(() -> Optional.ofNullable(property.schema().getMaxProperties()))
                    .orElse(Integer.MAX_VALUE);
            node
                    .addAndGetAnnotation(Size.class)
                    .addPair("min", "%d".formatted(min))
                    .addPair("max", "%d".formatted(max));
        }
    },

    PATTERN {
        @Override
        public void annotate(NodeWithAnnotations<?> node, OpenApiProperty property) {
            if (property.schema().getPattern() == null) {
                return;
            }
            var pattern = property.schema().getPattern().replace("\\", "\\\\");
            node
                    .addAndGetAnnotation(Pattern.class)
                    .addPair("regexp", "\"%s\"".formatted(pattern));
        }
    },

    JSON_UNWRAPPED {
        @Override
        public void annotate(NodeWithAnnotations<?> node, OpenApiProperty property) {
            node.addAnnotation(JsonUnwrapped.class);
        }
    },

    JSON_PROPERTY {
        @Override
        public void annotate(NodeWithAnnotations<?> node, OpenApiProperty property) {
            var value = "\"%s\"".formatted(property.name());
            var annotation = node
                    .addAndGetAnnotation(JsonProperty.class)
                    .addPair("value", value);
            var access = getAccess(property);
            if (access != Access.AUTO) {
                annotation.addPair("access", "%s.%s.%s".formatted(
                        JsonProperty.class.getSimpleName(),
                        JsonProperty.Access.class.getSimpleName(),
                        access.name()
                ));
            }
        }

        private JsonProperty.Access getAccess(OpenApiProperty property) {
            if (Boolean.TRUE.equals(property.schema().getReadOnly())) {
                return JsonProperty.Access.READ_ONLY;
            } else if (Boolean.TRUE.equals(property.schema().getWriteOnly())) {
                return JsonProperty.Access.WRITE_ONLY;
            } else {
                return JsonProperty.Access.AUTO;
            }
        }
    };

    public abstract void annotate(NodeWithAnnotations<?> node, OpenApiProperty property);

}
