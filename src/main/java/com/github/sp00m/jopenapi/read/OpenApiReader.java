package com.github.sp00m.jopenapi.read;

import com.github.sp00m.jopenapi.Names;
import com.github.sp00m.jopenapi.read.vo.JavaClassDefinition;
import com.github.sp00m.jopenapi.read.vo.JavaInterfaceDefinition;
import com.github.sp00m.jopenapi.read.vo.JavaTypeDefinition;
import com.github.sp00m.jopenapi.read.vo.OpenApiComponent;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.parser.OpenAPIV3Parser;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import javax.annotation.Nullable;
import java.io.File;
import java.nio.file.Files;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Stream;

import static java.util.stream.Collectors.toMap;

@RequiredArgsConstructor
@Slf4j
public final class OpenApiReader {

    private final String basePackageName;
    private final File inputDir;

    public List<JavaTypeDefinition> read() {
        var typeDefinitions = Stream
                .of(Objects.requireNonNull(inputDir.listFiles()))
                .flatMap(file -> scan(basePackageName, file).stream())
                .toList();
        link(typeDefinitions);
        return typeDefinitions;
    }

    private List<JavaTypeDefinition> scan(String basePackageName, File input) {
        var updatedBasePackageName = "%s.%s".formatted(basePackageName, Names.toPackageName(input.getName()));
        if (input.isDirectory()) {
            return Stream
                    .of(Objects.requireNonNull(input.listFiles()))
                    .flatMap(file -> scan(updatedBasePackageName, file).stream())
                    .toList();
        } else if (input.getName().endsWith(".yml")) {
            return parse(updatedBasePackageName, input);
        } else {
            return List.of();
        }
    }

    private List<JavaTypeDefinition> parse(String packageName, File input) {
        log.info("Parsing {}...", input);
        try {
            return parse(packageName, Files.readString(input.toPath()));
        } catch (Throwable t) {
            log.error("Unable to parse {}", input, t);
            return List.of();
        }
    }

    private List<JavaTypeDefinition> parse(String packageName, String openApiContents) {
        var result = new OpenAPIV3Parser().readContents(openApiContents);
        var openApi = result.getOpenAPI();
        if (openApi == null || openApi.getComponents() == null) {
            if (result.getMessages().contains("attribute openapi is missing")) {
                return parse(packageName, "openapi: 3.0.3\n" + openApiContents);
            } else {
                throw new IllegalArgumentException(String.join("; ", result.getMessages()));
            }
        }
        return read(packageName, openApi);
    }

    private List<JavaTypeDefinition> read(String packageName, OpenAPI openApi) {
        return openApi
                .getComponents()
                .getSchemas()
                .entrySet()
                .stream()
                .map(e -> new OpenApiComponent(e.getKey(), e.getValue()))
                .map(component -> readComponent(packageName, component))
                .filter(Objects::nonNull)
                .toList();
    }

    @Nullable
    private JavaTypeDefinition readComponent(String packageName, OpenApiComponent component) {
        try {
            log.info("Reading {}.{}...", packageName, component.getName());
            return new OpenApiComponentReader(packageName, component).read();
        } catch (Throwable t) {
            log.error("Unable to read {}.{}", packageName, component.getName(), t);
            return null;
        }
    }

    private void link(List<JavaTypeDefinition> typeDefinitions) {
        Map<String, JavaTypeDefinition> typeDefinitionsByType = typeDefinitions
                .stream()
                .filter(typeDefinition -> !(typeDefinition instanceof JavaInterfaceDefinition))
                .collect(toMap(JavaTypeDefinition::getFullName, Function.identity()));
        typeDefinitions
                .stream()
                .filter(typeDefinition -> typeDefinition instanceof JavaInterfaceDefinition)
                .forEach(typeDefinition -> {
                    var interfaceDefinition = (JavaInterfaceDefinition) typeDefinition;
                    interfaceDefinition
                            .getMapping()
                            .values()
                            .forEach(implementingType -> link(implementingType, typeDefinitionsByType.get(implementingType), interfaceDefinition));
                });
    }

    private void link(String implementingType, JavaTypeDefinition implementingTypeDefinition, JavaInterfaceDefinition interfaceDefinition) {
        var interfaceType = interfaceDefinition.getFullName();
        if (implementingTypeDefinition == null) {
            log.warn("{} not found, cannot implement {}", implementingType, interfaceType);
            return;
        }
        if (implementingTypeDefinition instanceof JavaClassDefinition classDefinition) {
            classDefinition.addImplementedType(interfaceType);
        } else {
            throw new IllegalStateException("Only 'object' schemas can be referenced by 'oneOf'");
        }
    }

}
