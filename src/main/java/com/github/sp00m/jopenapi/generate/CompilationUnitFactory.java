package com.github.sp00m.jopenapi.generate;

import com.github.javaparser.ast.CompilationUnit;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

/**
 * Creates a pre-configured {@link CompilationUnit} with wildcard imports for all packages
 * commonly used in generated code. This avoids the need to add individual imports for
 * {@code Objects}, {@code Optional}, {@code Collections}, etc. — the generated code can
 * use short names directly.
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class CompilationUnitFactory {

    public static CompilationUnit create() {
        return new CompilationUnit()
                .addImport("java.util.*", false, true)
                .addImport("java.time.*", false, true)
                .addImport("java.net.*", false, true)
                .addImport("java.math.*", false, true)
                .addImport("java.util.stream.*", false, true)
                .addImport("java.util.function.*", false, true)
                .addImport("com.github.jopenapi.support.*", false, true)
                .addImport("com.fasterxml.jackson.annotation.*", false, true)
                .addImport("jakarta.validation.constraints.*", false, true);
    }

}
