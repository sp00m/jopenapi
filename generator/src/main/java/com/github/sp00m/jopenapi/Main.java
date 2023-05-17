package com.github.sp00m.jopenapi;

import com.github.sp00m.jopenapi.generate.JavaGenerator;
import com.github.sp00m.jopenapi.read.OpenApiReader;
import com.github.sp00m.jopenapi.write.JavaFileWriter;
import io.swagger.v3.parser.OpenAPIV3Parser;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;

@Slf4j
public class Main {

    @SneakyThrows
    public static void main(String[] args) {

        var basePackage = "com.petstore";

        var openApiContents = Files.readString(Paths.get("/Users/christophemaillard/Work/codebases/github.com/sp00m/jopenapi/generator/openapi-petstore.yaml"));
        var openApi = new OpenAPIV3Parser().readContents(openApiContents).getOpenAPI();
        var reader = new OpenApiReader(basePackage, openApi);
        var javaTypeDefinitions = reader.read();

        var converter = new JavaGenerator(basePackage, javaTypeDefinitions);
        var javaFiles = converter.generate();

        String generatorDir = args.length == 0 ? "/Users/christophemaillard/Work/codebases/github.com/sp00m/jopenapi/generator" : args[0];
        File packagerSrcDir = new File(generatorDir, "../packager/src/main/java");
        var writer = new JavaFileWriter(basePackage, packagerSrcDir, javaFiles);
        writer.write();

    }

}
