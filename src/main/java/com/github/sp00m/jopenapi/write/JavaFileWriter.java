package com.github.sp00m.jopenapi.write;

import com.github.sp00m.jopenapi.generate.vo.JavaFile;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.List;

@RequiredArgsConstructor
@Slf4j
public final class JavaFileWriter {

    private final File finalBaseOutputDir;
    private final List<JavaFile> javaFiles;
    private final boolean delombok;

    @SneakyThrows
    public void write() {
        finalBaseOutputDir.mkdirs();
        emptyDirectory(finalBaseOutputDir);
        if (delombok) {
            var tempOutputDir = Files.createTempDirectory("jopenapi-").toFile();
            javaFiles.forEach(javaFile -> write(tempOutputDir, javaFile));
            writeSupportClasses(tempOutputDir);
            new Delomboker(tempOutputDir, finalBaseOutputDir).delombok();
        } else {
            javaFiles.forEach(javaFile -> write(finalBaseOutputDir, javaFile));
            writeSupportClasses(finalBaseOutputDir);
        }
    }

    @SneakyThrows
    private void write(File baseOutputDir, JavaFile javaFile) {
        var outputDir = new File(baseOutputDir, javaFile.packageName().replaceAll("\\.", "/"));
        outputDir.mkdirs();
        var outputFile = new File(outputDir, javaFile.name() + ".java").toPath();
        log.info("Writing into {}...", outputFile);
        Files.writeString(outputFile, javaFile.content());
    }

    @SneakyThrows
    private static void emptyDirectory(File dir) {
        Files.walkFileTree(dir.toPath(), new SimpleFileVisitor<>() {

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
    }

    @SneakyThrows
    private static void writeSupportClasses(File outputDir) {
        var supportPackage = "com.github.jopenapi.support";
        var supportDir = new File(outputDir, supportPackage.replace('.', '/'));
        supportDir.mkdirs();
        writeSupportClass(supportDir, "MissingPropertyException.java");
        writeSupportClass(supportDir, "InvalidPropertyException.java");
    }

    @SneakyThrows
    private static void writeSupportClass(File supportDir, String fileName) {
        var resourcePath = "/support/" + fileName;
        var dest = new File(supportDir, fileName);
        try (var in = JavaFileWriter.class.getResourceAsStream(resourcePath)) {
            if (in == null) {
                throw new IllegalStateException("Support class not found: " + resourcePath);
            }
            Files.write(dest.toPath(), in.readAllBytes());
        }
    }

}
