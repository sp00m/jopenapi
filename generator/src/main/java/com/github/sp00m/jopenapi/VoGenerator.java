package com.github.sp00m.jopenapi;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import io.swagger.v3.oas.models.media.Schema;
import lombok.Builder;
import lombok.Value;
import lombok.extern.jackson.Jacksonized;
import lombok.extern.slf4j.Slf4j;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

/**
 * https://swagger.io/docs/specification/data-models/data-types
 */
@Slf4j
public final class VoGenerator {

    private final String basePackage;
    private final String voName;
    private final String className;
    private final Schema<?> schema;
    private final CompilationUnit cu;
    private final ClassOrInterfaceDeclaration vo;
    private final List<String> requiredFields;

    public VoGenerator(String basePackage, String voName, Schema<?> schema) {
        this.basePackage = basePackage;
        this.voName = voName;
        this.className = Generator.normalizeClassName(voName);
        this.schema = schema;
        this.cu = new CompilationUnit(basePackage);
        this.vo = cu
                .addClass(Generator.normalizeClassName(voName))
                .addAnnotation(Value.class)
                .addAnnotation(Builder.class)
                .addAnnotation(Jacksonized.class);
        this.requiredFields = Optional
                .ofNullable(schema.getRequired())
                .orElseGet(Collections::emptyList);
    }

    public String run() {
        if (!"object".equals(schema.getType())) {
            log.error("Schema {} has type {}", schema.getName(), schema.getType());
            return null;
        }
        schema
                .getProperties()
                .forEach(this::generateField);
        return cu.toString();
    }

    private void generateField(String name, Schema<?> schema) {
        var generator = new FieldGenerator(basePackage, vo, name, schema, requiredFields.contains(name));
        generator.run();
    }

    public String getClassName() {
        return className;
    }

}