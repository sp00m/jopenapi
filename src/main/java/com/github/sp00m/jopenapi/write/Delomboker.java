package com.github.sp00m.jopenapi.write;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.ZipFile;

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
        if (lombokJar.isFatJar()) {
            command.add("-cp");
            command.add(lombokJar.path());
            command.add("lombok.launch.Main");
        } else {
            command.add("-jar");
            command.add(lombokJar.path());
        }
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

    private static LombokJar findLombokJar() {

        var classpath = System.getProperty("java.class.path");
        for (var entry : classpath.split(File.pathSeparator)) {
            if (entry.contains("lombok") && entry.endsWith(".jar")) {
                return new LombokJar(entry, false);
            }
        }

        try {
            var location = Delomboker.class.getProtectionDomain().getCodeSource().getLocation();
            if (location != null) {
                var jar = new File(location.toURI());
                if (jar.isFile()) {
                    try (var zf = new ZipFile(jar)) {
                        if (zf.getEntry("lombok/Lombok.class") != null) {
                            return new LombokJar(jar.getAbsolutePath(), true);
                        }
                    }
                }
            }
        } catch (Exception ignored) {
        }

        throw new IllegalStateException("Cannot find lombok.jar on the classpath");
    }

    private record LombokJar(String path, boolean isFatJar) {
    }

}
