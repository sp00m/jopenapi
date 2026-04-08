package com.github.sp00m.jopenapi;

import com.github.sp00m.jopenapi.generate.JavaGenerator;
import com.github.sp00m.jopenapi.read.OpenApiReader;
import com.github.sp00m.jopenapi.write.JavaFileWriter;
import lombok.extern.slf4j.Slf4j;
import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;

import java.io.File;
import java.util.concurrent.Callable;

@Slf4j
@Command(
        name = "jopenapi",
        mixinStandardHelpOptions = true,
        version = "jopenapi 0-SNAPSHOT",
        description = "Generate Java DTOs from OpenAPI schemas."
)
public class Main implements Callable<Integer> {

    @Option(
            names = {"-p", "--package"},
            required = true,
            description = "Base Java package name for generated classes (e.g. com.example.api)."
    )
    private String packageName;

    @Option(
            names = {"-i", "--input"},
            required = true,
            description = "Input directory containing OpenAPI schema files (.yml)."
    )
    private File inputDir;

    @Option(
            names = {"-o", "--output"},
            required = true,
            description = "Output directory for generated Java source files."
    )
    private File outputDir;

    @Override
    public Integer call() {
        if (!inputDir.isDirectory()) {
            log.error("Input path is not an existing directory: {}", inputDir);
            return 1;
        }
        try {
            run(packageName, inputDir, outputDir);
            log.info("Code generation completed successfully.");
            return 0;
        } catch (Exception e) {
            log.error("Code generation failed: {}", e.getMessage(), e);
            return 1;
        }
    }

    public static void main(String[] args) {
        int exitCode = new CommandLine(new Main()).execute(args);
        System.exit(exitCode);
    }

    static void run(String basePackageName, File inputDir, File outputDir) {
        var typeDefinitions = new OpenApiReader(basePackageName, inputDir).read();
        var javaFiles = new JavaGenerator(typeDefinitions).generate();
        new JavaFileWriter(outputDir, javaFiles).write();
    }

}
