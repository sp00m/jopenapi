package com.github.sp00m.jopenapi.read;

import io.swagger.v3.oas.models.OpenAPI;
import lombok.RequiredArgsConstructor;

import java.util.List;

@RequiredArgsConstructor
public final class OpenApiReader {

    private final String basePackage;
    private final OpenAPI openApi;

    public List<JavaClassDefinition> read() {
        return openApi
                .getComponents()
                .getSchemas()
                .entrySet()
                .stream()
                .map(e -> new OpenApiComponent(e.getKey(), e.getValue()))
                .map(this::readComponent)
                .toList();
    }

    private JavaClassDefinition readComponent(OpenApiComponent component) {
        var generator = new OpenApiComponentReader(basePackage, component);
        return generator.read();
    }

}
