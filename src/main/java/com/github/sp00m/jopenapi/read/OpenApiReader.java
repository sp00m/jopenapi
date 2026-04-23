package com.github.sp00m.jopenapi.read;

import com.github.sp00m.jopenapi.Names;
import com.github.sp00m.jopenapi.read.vo.*;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.parser.OpenAPIV3Parser;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import javax.annotation.Nullable;
import java.io.File;
import java.nio.file.Files;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Stream;

import static java.util.stream.Collectors.toMap;

/**
 * Phase 1 entry point. Scans the input directory for OpenAPI schema files, parses each one,
 * then links cross-references in a post-processing pass.
 *
 * <p>The directory structure maps to Java packages: each subdirectory becomes a sub-package,
 * and each schema file name becomes a sub-package too. When a single file is provided instead
 * of a directory, DTOs go directly into the base package (no extra sub-package).
 */
@RequiredArgsConstructor
@Slf4j
public final class OpenApiReader {

    private final String basePackageName;
    private final File inputDir;

    public List<JavaTypeDefinition> read() {
        var typeDefinitions = inputDir.isFile()
                ? parse(basePackageName, inputDir)
                : scan();
        return link(typeDefinitions);
    }

    private List<JavaTypeDefinition> scan() {
        return Stream
                .of(Objects.requireNonNull(inputDir.listFiles()))
                .flatMap(file -> scan(basePackageName, file).stream())
                .toList();
    }

    private List<JavaTypeDefinition> scan(String basePackageName, File input) {
        var updatedBasePackageName = "%s.%s".formatted(basePackageName, Names.toPackageName(input.getName()));
        if (input.isDirectory()) {
            return Stream
                    .of(Objects.requireNonNull(input.listFiles()))
                    .flatMap(file -> scan(updatedBasePackageName, file).stream())
                    .toList();
        } else if (isSchemaFile(input.getName())) {
            return parse(updatedBasePackageName, input);
        } else {
            return Collections.emptyList();
        }
    }

    private static boolean isSchemaFile(String name) {
        return name.endsWith(".yml") || name.endsWith(".yaml") || name.endsWith(".json");
    }

    private List<JavaTypeDefinition> parse(String packageName, File input) {
        log.info("Parsing {}...", input);
        try {
            return parse(packageName, Files.readString(input.toPath()));
        } catch (Throwable t) {
            log.error("Unable to parse {}", input, t);
            return Collections.emptyList();
        }
    }

    private List<JavaTypeDefinition> parse(String packageName, String openApiContents) {
        var result = new OpenAPIV3Parser().readContents(openApiContents);
        var openApi = result.getOpenAPI();
        if (openApi == null || openApi.getComponents() == null) {
            // Some schema files omit the top-level "openapi: 3.x" field. If the parser
            // complains about it, we retry by prepending a version header. This allows
            // users to write component-only YAML files without boilerplate.
            if (result.getMessages().contains("attribute openapi is missing")) {
                return parse(packageName, "openapi: 3.1.1\n" + openApiContents);
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
                .map(e -> new OpenApiComponent(e.getKey(), new OpenApiSchema(e.getValue())))
                .map(component -> readComponent(packageName, component))
                .filter(Objects::nonNull)
                .toList();
    }

    @Nullable
    private JavaTypeDefinition readComponent(String packageName, OpenApiComponent component) {
        try {
            log.info("Reading {}.{}...", packageName, component.name());
            return new OpenApiComponentReader(packageName, component).read();
        } catch (Throwable t) {
            log.error("Unable to read {}.{}", packageName, component.name(), t);
            return null;
        }
    }

    /**
     * Post-processing pass that wires relationships the parser couldn't resolve in isolation:
     * <ol>
     *   <li><b>Interfaces</b> — for each {@code oneOf}, tells the implementing records which
     *       sealed interface they must implement.</li>
     *   <li><b>Enum defaults</b> — if an optional property references an enum that has a default
     *       but the property itself doesn't define one, the enum's default is inherited. Also
     *       wires the enum's {@code decorateDefaultValue} so the generated code uses
     *       {@code EnumName.VALUE} syntax.</li>
     * </ol>
     */
    private List<JavaTypeDefinition> link(List<JavaTypeDefinition> typeDefinitions) {

        var typeDefinitionsByName = typeDefinitions
                .stream()
                .collect(toMap(JavaTypeDefinition::fullName, Function.identity()));

        // Pass 1: wire oneOf interfaces → tell each implementing record which sealed interface it belongs to
        typeDefinitions
                .stream()
                .filter(JavaInterfaceDefinition.class::isInstance)
                .forEach(typeDefinition -> {
                    var interfaceDefinition = (JavaInterfaceDefinition) typeDefinition;
                    interfaceDefinition
                            .mapping()
                            .values()
                            .forEach(implementingType -> {
                                var updatedRecordDefinition = linkInterface(interfaceDefinition, implementingType, typeDefinitionsByName);
                                if (updatedRecordDefinition != null) {
                                    typeDefinitionsByName.put(implementingType, updatedRecordDefinition);
                                }
                            });
                });

        // Pass 2: wire enum defaults → inherit enum's default when the field doesn't define its own
        typeDefinitionsByName
                .entrySet()
                .stream()
                .filter(entry -> entry.getValue() instanceof JavaRecordDefinition)
                .forEach(entry -> {
                    var recordDefinition = (JavaRecordDefinition) entry.getValue();
                    var updatedFields = recordDefinition
                            .fields()
                            .stream()
                            .map(field -> linkEnum(field, typeDefinitionsByName))
                            .toList();
                    var updatedRecordDefinition = recordDefinition.withFields(updatedFields);
                    entry.setValue(updatedRecordDefinition);
                });

        return typeDefinitionsByName
                .values()
                .stream()
                .sorted()
                .toList();
    }

    private JavaRecordDefinition linkInterface(JavaInterfaceDefinition interfaceDefinition, String implementingType, Map<String, JavaTypeDefinition> typeDefinitionsByName) {
        var existing = typeDefinitionsByName.get(implementingType);
        if (existing == null) {
            log.warn("{} not found, cannot implement {}", implementingType, interfaceDefinition.fullName());
            return null;
        } else if (existing instanceof JavaRecordDefinition recordDefinition) {
            return recordDefinition.withImplementedType(interfaceDefinition.fullName());
        } else {
            throw new IllegalStateException("Only 'object' schemas can be referenced by 'oneOf'");
        }
    }

    private JavaFieldDefinition linkEnum(JavaFieldDefinition field, Map<String, JavaTypeDefinition> typeDefinitionsByName) {
        var fieldTypeDefinition = typeDefinitionsByName.get(field.type().fullName());
        if (!(fieldTypeDefinition instanceof JavaEnumDefinition enumDefinition)) {
            return field;
        }
        var type = field.type().defaultValueDecorator(enumDefinition::decorateDefaultValue);
        if (field.property().optional() && field.type().decoratedDefaultValue() == null && enumDefinition.defaultValue() != null) {
            type = type.defaultValue(enumDefinition.defaultValue());
        }
        return field.withType(type);
    }

}
