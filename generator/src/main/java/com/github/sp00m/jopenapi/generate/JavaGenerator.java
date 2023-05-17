package com.github.sp00m.jopenapi.generate;

import com.github.javaparser.ast.CompilationUnit;
import com.github.sp00m.jopenapi.read.JavaClassDefinition;
import com.github.sp00m.jopenapi.read.JavaEnumDefinition;
import com.github.sp00m.jopenapi.read.JavaTypeDefinition;
import lombok.RequiredArgsConstructor;

import java.util.List;

@RequiredArgsConstructor
public final class JavaGenerator {

    private final String basePackage;
    private final List<JavaTypeDefinition> javaTypeDefinitions;

    public List<JavaFile> generate() {
        return javaTypeDefinitions
                .stream()
                .map(this::generate)
                .toList();
    }

    private JavaFile generate(JavaTypeDefinition typeDefinition) {
        final JavaTypeGenerator generator;
        if (typeDefinition instanceof JavaClassDefinition classDefinition) {
            generator = new JavaClassGenerator(classDefinition);
        } else if (typeDefinition instanceof JavaEnumDefinition enumDefinition) {
            generator = new JavaEnumGenerator(enumDefinition);
        } else {
            throw new IllegalStateException();
        }
        var generatedType = generator.generate();
        var compiler = new CompilationUnit(basePackage);
        compiler.addType(generatedType.getType(0));
        compiler.setImports(generatedType.getImports());
        return new JavaFile(typeDefinition.getName(), compiler.toString());
    }

}
