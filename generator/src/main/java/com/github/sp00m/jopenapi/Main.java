package com.github.sp00m.jopenapi;

import lombok.extern.slf4j.Slf4j;

import java.io.File;

@Slf4j
public class Main {

    public static void main(String[] args) {
        String generatorDir = args[0];
        File beautifierSrcDir = new File(generatorDir, "../beautifier/src/main/java");
        var generator = new Generator("https://petstore3.swagger.io/api/v3/openapi.json", "com.petstore", beautifierSrcDir);
        generator.run();
    }

}
