package com.github.sp00m.jopenapi.read;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonUnwrapped;
import com.github.javaparser.ast.nodeTypes.NodeWithAnnotations;
import com.github.sp00m.jopenapi.read.vo.OpenApiProperty;

public enum JavaFieldAnnotator {

    JSON_UNWRAPPED {
        @Override
        public void annotate(NodeWithAnnotations<?> node, OpenApiProperty property) {
            node.addAnnotation(JsonUnwrapped.class);
        }
    },

    JSON_PROPERTY {
        @Override
        public void annotate(NodeWithAnnotations<?> node, OpenApiProperty property) {
            node.addAndGetAnnotation(JsonProperty.class)
                    .addPair("value", "\"%s\"".formatted(property.getName()));
        }
    };

    public abstract void annotate(NodeWithAnnotations<?> node, OpenApiProperty property);

}
