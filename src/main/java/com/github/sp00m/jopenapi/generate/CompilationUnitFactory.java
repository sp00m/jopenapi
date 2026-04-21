package com.github.sp00m.jopenapi.generate;

import com.github.javaparser.ast.CompilationUnit;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class CompilationUnitFactory {

    public static CompilationUnit create() {
        return new CompilationUnit()
                .addImport("java.util.*", false, true)
                .addImport("java.util.stream.*", false, true)
                .addImport("java.util.function.*", false, true)
                .addImport("com.github.jopenapi.support.*", false, true)
                .addImport("com.fasterxml.jackson.annotation.*", false, true)
                .addImport("jakarta.validation.constraints.*", false, true);
    }

}
