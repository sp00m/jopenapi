package com.github.sp00m.jopenapi;

import lombok.extern.slf4j.Slf4j;

import java.io.File;

@Slf4j
public class Main {

    public static void main(String[] args) {
        String generatorDir = args.length == 0 ? "/Users/christophemaillard/Work/codebases/github.com/sp00m/jopenapi/generator" : args[0];
        File packagerSrcDir = new File(generatorDir, "../packager/src/main/java");
        var generator = new Generator("https://raw.githubusercontent.com/sp00m/jopenapi/main/generator/openapi-petstore.yaml", "com.petstore", packagerSrcDir);
        generator.run();
    }

}
