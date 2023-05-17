package com.github.sp00m.jopenapi.generate;

import com.github.javaparser.ast.CompilationUnit;
import com.github.sp00m.jopenapi.read.JavaClassDefinition;
import lombok.RequiredArgsConstructor;

import java.util.List;

@RequiredArgsConstructor
public final class Generator {

    private final String basePackage;
    private final List<JavaClassDefinition> classDefinitions;

    public void convert() {
        classDefinitions.forEach(this::convert);
    }

    private void convert(JavaClassDefinition classDefinition) {
        var generator = new JavaClassGenerator(classDefinition);
        var generatedClass = generator.generate();
        var cu = new CompilationUnit(basePackage);
        cu.addType(generatedClass.getType(0));
        cu.setImports(generatedClass.getImports());
        System.out.println(cu);
    }

}
