package com.github.sp00m.jopenapi;

import com.github.sp00m.jopenapi.generate.JavaGenerator;
import com.github.sp00m.jopenapi.read.OpenApiReader;
import com.github.sp00m.jopenapi.write.JavaFileWriter;
import lombok.extern.slf4j.Slf4j;

import java.io.File;

@Slf4j
public class Main {

    public static void main(String[] args) {

        if (args.length != 3) {
            throw new IllegalArgumentException();
        }
        var basePackageName = args[0];
        var inputDirPath = args[1];
        var generatorDirPath = args[2];

        var inputDir = new File(inputDirPath);
        var packagerSrcDir = new File(generatorDirPath, "../packager/src/main/java");
        run(basePackageName, inputDir, packagerSrcDir);
    }

    static void run(String basePackageName, File inputDir, File outputDir) {
        var typeDefinitions = new OpenApiReader(basePackageName, inputDir).read();
        var javaFiles = new JavaGenerator(typeDefinitions).generate();
        new JavaFileWriter(outputDir, javaFiles).write();
    }

}
