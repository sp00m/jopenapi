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

    private final File baseOutputDir;
    private final List<JavaFile> javaFiles;

    public void write() {
        baseOutputDir.mkdirs();
        emptyDirectory(baseOutputDir);
        javaFiles.forEach(this::write);
    }

    @SneakyThrows
    private void write(JavaFile javaFile) {
        var outputDir = new File(baseOutputDir, javaFile.getPackageName().replaceAll("\\.", "/"));
        outputDir.mkdirs();
        var outputFile = new File(outputDir, javaFile.getName() + ".java").toPath();
        log.info("Writing into {}...", outputFile);
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
