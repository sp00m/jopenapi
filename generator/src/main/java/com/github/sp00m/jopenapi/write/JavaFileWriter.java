package com.github.sp00m.jopenapi.write;

import com.github.sp00m.jopenapi.generate.JavaFile;
import lombok.SneakyThrows;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.List;

public final class JavaFileWriter {

    private final File baseDir;
    private final File outputDir;
    private final List<JavaFile> javaFiles;

    public JavaFileWriter(String basePackage, File baseDir, List<JavaFile> javaFiles) {
        this.baseDir = baseDir;
        this.outputDir = new File(baseDir, basePackage.replaceAll("\\.", "/"));
        this.javaFiles = javaFiles;
    }

    public void write() {
        outputDir.mkdirs();
        emptyDirectory(baseDir);
        outputDir.mkdirs();
        javaFiles.forEach(this::write);
    }

    @SneakyThrows
    private void write(JavaFile javaFile) {
        var outputFile = new File(outputDir, javaFile.getName() + ".java").toPath();
        Files.writeString(outputFile, javaFile.getContent());
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

}
