package com.github.sp00m.jopenapi;

import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.opentest4j.AssertionFailedError;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.Arrays;

class Example {

    @SneakyThrows
    @Test
    void run() {

        var basePackageName = "jopenapi.test";
        var inputDir = new File("src/test/resources/example/schemas");
        var actualOutputDir = Files.createTempDirectory("jopenapi-").toFile();

        Main.run(basePackageName, inputDir, actualOutputDir);
        var expectedOutputDir = new File("src/test/resources/example/generated");

        compareDirs(actualOutputDir.toPath(), expectedOutputDir.toPath());
    }

    @SneakyThrows
    private void compareDirs(Path expectedDir, Path actualDir) {
        Files.walkFileTree(expectedDir, new SimpleFileVisitor<>() {

            @Override
            public FileVisitResult visitFile(Path expectedPath, BasicFileAttributes attributes) throws IOException {
                var relativePath = expectedDir.relativize(expectedPath);
                var actualPath = actualDir.resolve(relativePath);
                compareFiles(expectedPath, actualPath, relativePath);
                return FileVisitResult.CONTINUE;
            }

        });
    }

    @SneakyThrows
    private static void compareFiles(Path expectedFile, Path actualFile, Path relativePath) {
        int mismatchingByte = (int) Files.mismatch(expectedFile, actualFile);
        if (mismatchingByte != -1) {
            byte[] expectedBytes = Files.readAllBytes(expectedFile);
            byte[] expectedBytesMismatch = Arrays.copyOfRange(
                    expectedBytes,
                    Math.max(mismatchingByte - 20, 0),
                    Math.min(mismatchingByte + 20, expectedBytes.length)
            );
            byte[] actualBytes = Files.readAllBytes(actualFile);
            byte[] actualBytesMismatch = Arrays.copyOfRange(
                    actualBytes,
                    Math.max(mismatchingByte - 20, 0),
                    Math.min(mismatchingByte + 20, actualBytes.length)
            );
            throw new AssertionFailedError(
                    relativePath + " mismatches",
                    "[...]" + new String(expectedBytesMismatch) + "[...]",
                    "[...]" + new String(actualBytesMismatch) + "[...]"
            );
        }
    }

}
