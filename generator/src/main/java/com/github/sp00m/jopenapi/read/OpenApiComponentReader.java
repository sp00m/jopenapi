package com.github.sp00m.jopenapi.read;

import io.swagger.v3.oas.models.media.Schema;
import lombok.RequiredArgsConstructor;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
public final class OpenApiComponentReader {

    private final String basePackage;
    private final OpenApiComponent component;

    public JavaClassDefinition read() {
        if (!"object".equals(component.getSchema().getType())) {
            throw new IllegalArgumentException(component.getSchema().getType());
        }
        var requiredProperties = Optional
                .ofNullable(component.getSchema().getRequired())
                .orElseGet(Collections::emptyList);
        var name = Names.toClassName(component.getName());
        var fields = component
                .getSchema()
                .getProperties()
                .entrySet()
                .stream()
                .map(e -> toOpenApiProperty(e.getKey(), e.getValue(), requiredProperties))
                .map(this::readProperty)
                .toList();
        return new JavaClassDefinition(component, name, fields);
    }

    private OpenApiProperty toOpenApiProperty(String name, Schema<?> schema, List<String> requiredProperties) {
        // https://swagger.io/docs/specification/data-models/enums/
        var enumValues = Optional
                .ofNullable(schema.getEnum())
                .orElseGet(Collections::emptyList);
        var optional = (!requiredProperties.contains(name) || Boolean.TRUE.equals(schema.getNullable()))
                && (enumValues.isEmpty() || enumValues.contains(null));
        return new OpenApiProperty(name, schema, optional);
    }

    private JavaFieldDefinition readProperty(OpenApiProperty property) {
        var generator = new OpenApiPropertyReader(basePackage, property);
        return generator.read();
    }

}
