package com.github.sp00m.jopenapi.generate;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.Modifier;
import com.github.javaparser.ast.NodeList;
import com.github.javaparser.ast.expr.ArrayInitializerExpr;
import com.github.javaparser.ast.expr.Expression;
import com.github.sp00m.jopenapi.read.vo.JavaInterfaceDefinition;
import lombok.RequiredArgsConstructor;

import java.util.Map;

import static java.util.Map.Entry.comparingByKey;

@RequiredArgsConstructor
final class JavaInterfaceGenerator implements JavaTypeGenerator {

    private final JavaInterfaceDefinition interfaceDefinition;
    private final CompilationUnit compiler = new CompilationUnit();

    @Override
    public CompilationUnit generate() {
        var jsonSubTypeExpressions = interfaceDefinition
                .mapping()
                .entrySet()
                .stream()
                .sorted(comparingByKey())
                .map(mapping -> "@JsonSubTypes.Type(name = \"%s\", value = %s.class)".formatted(mapping.getKey(), mapping.getValue()))
                .map(StaticJavaParser::parseAnnotation)
                .map(Expression.class::cast)
                .toList();
        var permittedTypes = interfaceDefinition
                .mapping()
                .entrySet()
                .stream()
                .sorted(comparingByKey())
                .map(Map.Entry::getValue)
                .map(StaticJavaParser::parseClassOrInterfaceType)
                .toList();
        var interfaceDeclaration = compiler
                .addInterface(interfaceDefinition.name())
                .addModifier(Modifier.Keyword.SEALED)
                .setPermittedTypes(new NodeList<>(permittedTypes))
                .addSingleMemberAnnotation(JsonSubTypes.class, new ArrayInitializerExpr(new NodeList<>(jsonSubTypeExpressions)));
        interfaceDeclaration
                .addAndGetAnnotation(JsonTypeInfo.class)
                .addPair("use", "JsonTypeInfo.Id.NAME")
                .addPair("include", "JsonTypeInfo.As.EXISTING_PROPERTY")
                .addPair("property", "\"%s\"".formatted(interfaceDefinition.propertyName()))
                .addPair("visible", "true");
        return compiler;
    }

}
