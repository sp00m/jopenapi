package com.github.sp00m.jopenapi.write;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
@Slf4j
public final class Delomboker {

    private final File inputDir;
    private final File outputDir;

    @SneakyThrows
    public void delombok() {

        var lombokJar = findLombokJar();
        var classpath = System.getProperty("java.class.path");

        List<String> command = new ArrayList<>();
        command.add(ProcessHandle.current().info().command().orElse("java"));
        command.add("-jar");
        command.add(lombokJar);
        command.add("delombok");
        command.add(inputDir.getAbsolutePath());
        command.add("-d");
        command.add(outputDir.getAbsolutePath());
        command.add("--classpath");
        command.add(classpath);
        command.add("-f");
        command.add("pretty");
        command.add("-n");
        command.add("-v");

        var process = new ProcessBuilder(command)
                .redirectErrorStream(true)
                .start();

        var output = new String(process.getInputStream().readAllBytes());
        int exitCode = process.waitFor();

        if (!output.isBlank()) {
            log.info(output.trim());
        }

        if (exitCode != 0) {
            throw new RuntimeException("Delombok failed with exit code " + exitCode + ":\n" + output);
        }
    }

    private static String findLombokJar() {
        var classpath = System.getProperty("java.class.path");
        for (var entry : classpath.split(File.pathSeparator)) {
            if (entry.contains("lombok") && entry.endsWith(".jar")) {
                return entry;
            }
        }
        throw new IllegalStateException("Cannot find lombok.jar on the classpath");
    }

}

