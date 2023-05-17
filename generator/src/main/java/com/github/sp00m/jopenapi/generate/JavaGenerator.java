package com.github.sp00m.jopenapi.generate;

import com.github.javaparser.ast.CompilationUnit;
import com.github.sp00m.jopenapi.read.JavaClassDefinition;
import lombok.RequiredArgsConstructor;

import java.util.List;

@RequiredArgsConstructor
public final class JavaGenerator {

    private final String basePackage;
    private final List<JavaClassDefinition> classDefinitions;

    public List<JavaFile> generate() {
        return classDefinitions
                .stream()
                .map(this::generate)
                .toList();
    }

    private JavaFile generate(JavaClassDefinition classDefinition) {
        var generator = new JavaClassGenerator(classDefinition);
        var generatedClass = generator.generate();
        var compiler = new CompilationUnit(basePackage);
        compiler.addType(generatedClass.getType(0));
        compiler.setImports(generatedClass.getImports());
        return new JavaFile(classDefinition.getName(), compiler.toString());
    }

}
