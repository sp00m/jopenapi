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
        var typeGenerator = switch (typeDefinition) {
            case JavaRecordDefinition recordDefinition -> new JavaRecordGenerator(recordDefinition);
            case JavaEnumDefinition enumDefinition -> new JavaEnumGenerator(enumDefinition);
            case JavaValueRecordDefinition valueRecordDefinition -> new JavaValueRecordGenerator(valueRecordDefinition);
            case JavaInterfaceDefinition interfaceDefinition -> new JavaInterfaceGenerator(interfaceDefinition);
            case null, default -> throw new IllegalStateException();
        };
        var compiler = typeGenerator.generate();
        addJavaDoc(typeDefinition, compiler);
        return compiler;
    }

    private static void addJavaDoc(JavaTypeDefinition typeDefinition, CompilationUnit compiler) {
        var javadocBuilder = new StringBuilder();
        if (typeDefinition.description() != null) {
            javadocBuilder.append(typeDefinition.description());
        }
        if (typeDefinition instanceof JavaRecordDefinition recordDefinition) {
            recordDefinition.fields().forEach(field -> {
                var desc = field.type().getDescription();
                if (desc != null) {
                    if (!javadocBuilder.isEmpty()) {
                        javadocBuilder.append("\n");
                    }
                    javadocBuilder.append("@param ").append(field.name()).append(" ").append(desc);
                }
            });
        }
        if (!javadocBuilder.isEmpty()) {
            compiler.getType(0).setJavadocComment(javadocBuilder.toString());
        }
    }

}
