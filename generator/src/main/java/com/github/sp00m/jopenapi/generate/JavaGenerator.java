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
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.ToolFactory;
import org.eclipse.jdt.core.formatter.CodeFormatter;
import org.eclipse.jdt.core.formatter.DefaultCodeFormatterConstants;
import org.eclipse.jface.text.Document;

import javax.annotation.Nullable;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@RequiredArgsConstructor
@Slf4j
public final class JavaGenerator {

    private static final CodeFormatter FORMATTER;

    static {
        Map<String, String> options = DefaultCodeFormatterConstants.getEclipseDefaultSettings();
        options.put(JavaCore.COMPILER_SOURCE, "21");
        options.put(JavaCore.COMPILER_COMPLIANCE, "21");
        options.put(JavaCore.COMPILER_CODEGEN_TARGET_PLATFORM, "21");
        FORMATTER = ToolFactory.createCodeFormatter(options);
    }

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
        var content = formatSource(compiler.toString());
        return new JavaFile(typeDefinition.getPackageName(), typeDefinition.getName(), content);
    }

    private static String formatSource(String source) {
        try {
            var edit = FORMATTER.format(
                    CodeFormatter.K_COMPILATION_UNIT,
                    source, 0, source.length(), 0, "\n");
            if (edit == null) {
                log.warn("Formatter returned null, returning as-is");
                return source;
            }
            var document = new Document(source);
            edit.apply(document);
            return document.get();
        } catch (Throwable e) {
            log.warn("Unable to format source, returning as-is", e);
            return source;
        }
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
        var compiler = typeGenerator.generate();

        // Build javadoc: class description + @param tags for record fields
        var javadocBuilder = new StringBuilder();
        if (typeDefinition.getDescription() != null) {
            javadocBuilder.append(typeDefinition.getDescription());
        }
        if (typeDefinition instanceof JavaClassDefinition classDefinition) {
            for (var field : classDefinition.getFields()) {
                var desc = field.getType().getDescription();
                if (desc != null) {
                    if (!javadocBuilder.isEmpty()) {
                        javadocBuilder.append("\n");
                    }
                    javadocBuilder.append("@param ").append(field.getName()).append(" ").append(desc);
                }
            }
        }
        if (!javadocBuilder.isEmpty()) {
            compiler.getType(0).setJavadocComment(javadocBuilder.toString());
        }

        return compiler;
    }

}
