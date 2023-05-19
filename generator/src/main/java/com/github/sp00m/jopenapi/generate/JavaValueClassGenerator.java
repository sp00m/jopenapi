package com.github.sp00m.jopenapi.generate;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import com.github.javaparser.ast.CompilationUnit;
import com.github.sp00m.jopenapi.read.vo.JavaValueClassDefinition;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Value;

import static com.github.javaparser.StaticJavaParser.parseBlock;
import static com.github.javaparser.ast.Modifier.Keyword.PUBLIC;
import static com.github.javaparser.ast.Modifier.Keyword.STATIC;

@RequiredArgsConstructor
final class JavaValueClassGenerator implements JavaTypeGenerator {

    private final JavaValueClassDefinition valueClassDefinition;
    private final CompilationUnit compiler = new CompilationUnit();

    @Override
    public CompilationUnit generate() {
        compiler.addImport(AccessLevel.class);
        var classDeclaration = compiler
                .addClass(valueClassDefinition.getName())
                .addAnnotation(Value.class)
                .addSingleMemberAnnotation(Getter.class, "AccessLevel.NONE");
        classDeclaration
                .addAndGetAnnotation(AllArgsConstructor.class)
                .addPair("access", "AccessLevel.PRIVATE");
        var fieldDefinition = valueClassDefinition.getField();
        JavaClassGenerator
                .addField(compiler, classDeclaration, fieldDefinition)
                .setName("get")
                .addAnnotation(JsonValue.class);
        classDeclaration
                .addMethod("of", PUBLIC, STATIC)
                .addParameter(fieldDefinition.getType().getFullName(), fieldDefinition.getName())
                .setType(valueClassDefinition.getName())
                .setBody(parseBlock("{return new %s(%s);}".formatted(valueClassDefinition.getName(), fieldDefinition.getName())))
                .addAnnotation(JsonCreator.class);
        return compiler;
    }

}
