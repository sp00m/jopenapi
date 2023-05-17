package com.github.sp00m.jopenapi;

import com.github.sp00m.jopenapi.generate.Generator;
import com.github.sp00m.jopenapi.read.OpenApiReader;
import io.swagger.v3.parser.OpenAPIV3Parser;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import java.nio.file.Files;
import java.nio.file.Paths;

@Slf4j
public class Main {

    @SneakyThrows
    public static void main(String[] args) {

        var openApiContents = Files.readString(Paths.get("/Users/christophemaillard/Work/codebases/github.com/sp00m/jopenapi/generator/openapi-petstore.yaml"));
        var openApi = new OpenAPIV3Parser().readContents(openApiContents).getOpenAPI();

        var basePackage = "com.petstore";
        var reader = new OpenApiReader(basePackage, openApi);
        var javaClassDefinitions = reader.read();
        var converter = new Generator(basePackage, javaClassDefinitions);
        converter.convert();

//        String generatorDir = args.length == 0 ? "/Users/christophemaillard/Work/codebases/github.com/sp00m/jopenapi/generator" : args[0];
//
//        File packagerSrcDir = new File(generatorDir, "../packager/src/main/java");
//        var generator = new Generator("https://raw.githubusercontent.com/sp00m/jopenapi/main/generator/openapi-petstore.yaml", "com.petstore");
//        var vos = generator.run();

    }

}
