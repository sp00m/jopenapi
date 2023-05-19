package com.github.sp00m.jopenapi.generate;

import com.github.javaparser.ast.CompilationUnit;
import com.github.sp00m.jopenapi.generate.vo.JavaFile;
import com.github.sp00m.jopenapi.read.vo.JavaClassDefinition;
import com.github.sp00m.jopenapi.read.vo.JavaEnumDefinition;
import com.github.sp00m.jopenapi.read.vo.JavaInterfaceDefinition;
import com.github.sp00m.jopenapi.read.vo.JavaTypeDefinition;
import com.github.sp00m.jopenapi.read.vo.JavaValueClassDefinition;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import javax.annotation.Nullable;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;

@RequiredArgsConstructor
@Slf4j
public final class JavaGenerator {

    private final List<JavaTypeDefinition> typeDefinitions;

    public List<JavaFile> generate() {
        return typeDefinitions
                .stream()
                .map(this::generate)
                .filter(Objects::nonNull)
                .toList();
    }

    @Nullable
    private JavaFile generate(JavaTypeDefinition typeDefinition) {
        try {
            log.info("Generating {}.{}...", typeDefinition.getPackageName(), typeDefinition.getName());
            return generateOrThrow(typeDefinition);
        } catch (Throwable t) {
            log.error("Unable to generate {}.{}", typeDefinition.getPackageName(), typeDefinition.getName(), t);
            return null;
        }
    }

    private JavaFile generateOrThrow(JavaTypeDefinition typeDefinition) {
        var compiler = generateCompiler(typeDefinition)
                .setPackageDeclaration(typeDefinition.getPackageName());
        compiler
                .getImports()
                .sort(Comparator.comparing(x -> x.getName().asString()));
        return new JavaFile(typeDefinition.getPackageName(), typeDefinition.getName(), compiler.toString());
    }

    static CompilationUnit generateCompiler(JavaTypeDefinition typeDefinition) {
        final JavaTypeGenerator typeGenerator;
        if (typeDefinition instanceof JavaClassDefinition classDefinition) {
            typeGenerator = new JavaClassGenerator(classDefinition);
        } else if (typeDefinition instanceof JavaEnumDefinition enumDefinition) {
            typeGenerator = new JavaEnumGenerator(enumDefinition);
        } else if (typeDefinition instanceof JavaValueClassDefinition valueClassDefinition) {
            typeGenerator = new JavaValueClassGenerator(valueClassDefinition);
        } else if (typeDefinition instanceof JavaInterfaceDefinition interfaceDefinition) {
            typeGenerator = new JavaInterfaceGenerator(interfaceDefinition);
        } else {
            throw new IllegalStateException();
        }
        return typeGenerator.generate();
    }

}
