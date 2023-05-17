package com.github.sp00m.jopenapi.write;

import lombok.SneakyThrows;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;

public final class VoWriter {

    private final File baseDir;
    private final File outputDir;

    public VoWriter(File baseDir, String basePackage) {
        this.baseDir = baseDir;
        this.outputDir = new File(baseDir, basePackage.replaceAll("\\.", "/"));
        outputDir.mkdirs();
    }

    @SneakyThrows
    public void empty() {
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
    }

//    @SneakyThrows
//    public void write(Vo vo) {
//        var outputFile = new File(outputDir, vo.getName() + ".java").toPath();
//        Files.write(outputFile, vo.getContent().getBytes());
//    }

}
