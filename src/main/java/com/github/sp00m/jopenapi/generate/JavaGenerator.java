package com.github.sp00m.jopenapi.generate;

import com.github.javaparser.ast.CompilationUnit;
import com.github.sp00m.jopenapi.generate.vo.JavaFile;
import com.github.sp00m.jopenapi.read.vo.*;
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
            log.info("Generating {}.{}...", typeDefinition.packageName(), typeDefinition.name());
            return generateOrThrow(typeDefinition);
        } catch (Throwable t) {
            log.error("Unable to generate {}.{}", typeDefinition.packageName(), typeDefinition.name(), t);
            return null;
        }
    }

    private JavaFile generateOrThrow(JavaTypeDefinition typeDefinition) {
        var compiler = generateCompiler(typeDefinition)
                .setPackageDeclaration(typeDefinition.packageName());
        compiler
                .getImports()
                .sort(Comparator.comparing(x -> x.getName().asString()));
        return new JavaFile(typeDefinition.packageName(), typeDefinition.name(), compiler.toString());
    }

    static CompilationUnit generateCompiler(JavaTypeDefinition typeDefinition) {
        final JavaTypeGenerator typeGenerator;
        if (typeDefinition == null) {
            throw new IllegalStateException();
        } else if (typeDefinition instanceof JavaRecordDefinition recordDefinition) {
            typeGenerator = new JavaRecordGenerator(recordDefinition);
        } else if (typeDefinition instanceof JavaEnumDefinition enumDefinition) {
            typeGenerator = new JavaEnumGenerator(enumDefinition);
        } else if (typeDefinition instanceof JavaValueRecordDefinition valueRecordDefinition) {
            typeGenerator = new JavaValueRecordGenerator(valueRecordDefinition);
        } else if (typeDefinition instanceof JavaInterfaceDefinition interfaceDefinition) {
            typeGenerator = new JavaInterfaceGenerator(interfaceDefinition);
        } else {
            throw new IllegalStateException();
        }
        var compiler = typeGenerator.generate();
        addJavadoc(typeDefinition, compiler);
        return compiler;
    }

    private static void addJavadoc(JavaTypeDefinition typeDefinition, CompilationUnit compiler) {
        var javadoc = new StringBuilder();
        if (typeDefinition.description() != null) {
            javadoc.append(typeDefinition.description());
        }
        if (typeDefinition instanceof JavaRecordDefinition recordDefinition) {
            recordDefinition.fields().forEach(field -> {
                var description = field.type().description();
                if (description != null) {
                    if (!javadoc.isEmpty()) {
                        javadoc.append("\n");
                    }
                    javadoc.append("@param ").append(field.name()).append(" ").append(description);
                }
            });
        }
        if (!javadoc.isEmpty()) {
            compiler.getType(0).setJavadocComment(javadoc.toString());
        }
    }

}
