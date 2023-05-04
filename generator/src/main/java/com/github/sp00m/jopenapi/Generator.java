package com.github.sp00m.jopenapi;

import io.swagger.v3.oas.models.media.Schema;
import io.swagger.v3.parser.OpenAPIV3Parser;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.stream.Collectors;
import java.util.stream.Stream;

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
        var outputDir = new File(baseDir, basePackage.replaceAll("\\.", "/"));
        outputDir.mkdirs();
        return outputDir;
    }

    public void run() {
        var openAPI = new OpenAPIV3Parser().read(openApiLocation);
        openAPI
                .getComponents()
                .getSchemas()
                .forEach(this::generateVo);
    }

    private void generateVo(String name, Schema<?> schema) {
        var generator = new VoGenerator(basePackage, name, schema);
        var vo = generator.run();
        if (vo != null) {
            try {
                var outputFile = new File(outputDir, generator.getClassName() + ".java").toPath();
                Files.write(outputFile, vo.getBytes());
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    static String normalizeClassName(String name) {
        return Stream
                .of(toWords(name))
                .map(string -> string.substring(0, 1).toUpperCase() + string.substring(1))
                .collect(Collectors.joining());
    }

    static String normalizeFieldName(String name) {
        var className = normalizeClassName(name);
        return className.substring(0, 1).toLowerCase() + className.substring(1);
    }

    static String normalizeEnumValueName(String name) {
        return Stream
                .of(toWords(name))
                .map(String::toUpperCase)
                .collect(Collectors.joining("_"));
    }

    private static String[] toWords(String name) {
        return name.split("([_\\W]|(?!=[a-z])(?=[A-Z]))", -1);
    }

}
