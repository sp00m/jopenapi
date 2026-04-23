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

/**
 * Enum of annotation strategies applied to record fields and factory parameters.
 *
 * <p>Each strategy has two hooks: {@link #annotateRecordField} (adds the annotation to the
 * record component declaration) and {@link #annotateFactoryArgument} (adds it to the
 * {@code @JsonCreator} factory parameter). Most validation annotations (MIN, MAX, SIZE, PATTERN)
 * only go on the record field — Jackson doesn't need them on the factory. Only Jackson-specific
 * annotations (JSON_PROPERTY, JSON_UNWRAPPED) are duplicated on the factory parameter so
 * Jackson can bind the right JSON field during deserialization.
 */
@Slf4j
public enum JavaPropertyAnnotator {

    MIN {
        @Override
        public void annotateRecordField(NodeWithAnnotations<?> node, OpenApiProperty property) {
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

        @Override
        public void annotateFactoryArgument(NodeWithAnnotations<?> node, OpenApiProperty property) {
            // noop
        }
    },

    MAX {
        @Override
        public void annotateRecordField(NodeWithAnnotations<?> node, OpenApiProperty property) {
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

        @Override
        public void annotateFactoryArgument(NodeWithAnnotations<?> node, OpenApiProperty property) {
            // noop
        }
    },

    MULTIPLE_OF {
        @Override
        public void annotateRecordField(NodeWithAnnotations<?> node, OpenApiProperty property) {
            if (property.schema().getMultipleOf() == null) {
                return;
            }
            log.warn("'multipleOf' not supported");
        }

        @Override
        public void annotateFactoryArgument(NodeWithAnnotations<?> node, OpenApiProperty property) {
            // noop
        }
    },

    SIZE {
        @Override
        public void annotateRecordField(NodeWithAnnotations<?> node, OpenApiProperty property) {
            if (property.schema().getMinLength() == null && property.schema().getMaxLength() == null
                    && property.schema().getMinItems() == null && property.schema().getMaxItems() == null
                    && property.schema().getMinProperties() == null && property.schema().getMaxProperties() == null) {
                return;
            }
            var min = Optional.ofNullable(property.schema().getMinLength())
                    .or(() -> Optional.ofNullable(property.schema().getMinItems()))
                    .or(() -> Optional.ofNullable(property.schema().getMinProperties()));
            var max = Optional.ofNullable(property.schema().getMaxLength())
                    .or(() -> Optional.ofNullable(property.schema().getMaxItems()))
                    .or(() -> Optional.ofNullable(property.schema().getMaxProperties()));
            var size = node.addAndGetAnnotation(Size.class);
            min.ifPresent(i -> size.addPair("min", "%d".formatted(i)));
            max.ifPresent(i -> size.addPair("max", "%d".formatted(i)));
        }

        @Override
        public void annotateFactoryArgument(NodeWithAnnotations<?> node, OpenApiProperty property) {
            // noop
        }
    },

    PATTERN {
        @Override
        public void annotateRecordField(NodeWithAnnotations<?> node, OpenApiProperty property) {
            if (property.schema().getPattern() == null) {
                return;
            }
            var pattern = property.schema().getPattern().replace("\\", "\\\\");
            node
                    .addAndGetAnnotation(Pattern.class)
                    .addPair("regexp", "\"%s\"".formatted(pattern));
        }

        @Override
        public void annotateFactoryArgument(NodeWithAnnotations<?> node, OpenApiProperty property) {
            // noop
        }
    },

    JSON_UNWRAPPED {
        @Override
        public void annotateRecordField(NodeWithAnnotations<?> node, OpenApiProperty property) {
            node.addAnnotation(JsonUnwrapped.class);
        }

        @Override
        public void annotateFactoryArgument(NodeWithAnnotations<?> node, OpenApiProperty property) {
            annotateRecordField(node, property);
        }
    },

    JSON_PROPERTY {
        @Override
        public void annotateRecordField(NodeWithAnnotations<?> node, OpenApiProperty property) {
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

        @Override
        public void annotateFactoryArgument(NodeWithAnnotations<?> node, OpenApiProperty property) {
            annotateRecordField(node, property);
        }

        private JsonProperty.Access getAccess(OpenApiProperty property) {
            if (property.readOnly()) {
                return JsonProperty.Access.READ_ONLY;
            } else if (property.writeOnly()) {
                return JsonProperty.Access.WRITE_ONLY;
            } else {
                return JsonProperty.Access.AUTO;
            }
        }
    };

    public abstract void annotateRecordField(NodeWithAnnotations<?> node, OpenApiProperty property);

    public abstract void annotateFactoryArgument(NodeWithAnnotations<?> node, OpenApiProperty property);

}
