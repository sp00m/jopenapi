package com.github.sp00m.jopenapi;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.media.Schema;
import io.swagger.v3.parser.OpenAPIV3Parser;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;

public final class Generator {

    private final String openApiLocation;
    private final String basePackage;
    private final File outputDir;

    public Generator(String openApiLocation, String basePackage, File baseDir) {
        this.openApiLocation = openApiLocation;
        this.basePackage = basePackage;
        try {
            this.outputDir = prepareOutputDir(basePackage, baseDir);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static File prepareOutputDir(String basePackage, File baseDir) throws IOException {
        Files.walkFileTree(baseDir.toPath(), new SimpleFileVisitor<>() {

            @Override
            public FileVisitResult visitFile(Path file, BasicFileAttributes attributes) throws IOException {
                Files.delete(file);
                return FileVisitResult.CONTINUE;
            }

            @Override
            public FileVisitResult postVisitDirectory(Path dir, IOException e) throws IOException {
                Files.delete(dir);
                return FileVisitResult.CONTINUE;
            }

        });
        File outputDir = new File(baseDir, basePackage.replaceAll("\\.", "/"));
        outputDir.mkdirs();
        return outputDir;
    }

    public void run() {
        OpenAPI openAPI = new OpenAPIV3Parser().read(openApiLocation);
        openAPI
                .getComponents()
                .getSchemas()
                .forEach(this::generateDto);
    }

    private void generateDto(String name, Schema<?> schema) {
        var generator = new DtoGenerator(basePackage, name, schema);
        String dto = generator.run();
        if (dto != null) {
            try {
                Path outputFile = new File(outputDir, generator.getClassName() + ".java").toPath();
                Files.write(outputFile, dto.getBytes());
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

}
