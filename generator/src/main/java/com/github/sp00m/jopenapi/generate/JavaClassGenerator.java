package com.github.sp00m.jopenapi.generate;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.sp00m.jopenapi.read.JavaClassDefinition;
import com.github.sp00m.jopenapi.read.JavaEnumDefinition;
import com.github.sp00m.jopenapi.read.JavaFieldDefinition;
import com.github.sp00m.jopenapi.read.JavaTypeDefinition;
import com.github.sp00m.jopenapi.read.Names;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.RequiredArgsConstructor;
import lombok.Value;
import lombok.extern.jackson.Jacksonized;
import org.apache.commons.lang3.ClassUtils;

import java.util.Optional;

import static com.github.javaparser.StaticJavaParser.parseBlock;
import static com.github.javaparser.ast.Modifier.Keyword.PUBLIC;
import static com.github.javaparser.ast.Modifier.Keyword.STATIC;

@RequiredArgsConstructor
public final class JavaClassGenerator implements JavaTypeGenerator {

    private final JavaClassDefinition classDefinition;
    private final CompilationUnit compiler = new CompilationUnit();

    @Override
    public CompilationUnit generate() {
        var classDeclaration = compiler
                .addClass(classDefinition.getName())
                .addAnnotation(Value.class)
                .addAnnotation(Builder.class)
                .addAnnotation(Jacksonized.class);
        classDefinition.getFields().forEach(field -> addField(classDeclaration, field));
        return compiler;
    }

    private void addField(ClassOrInterfaceDeclaration classDeclaration, JavaFieldDefinition fieldDefinition) {
        var fieldType = fieldDefinition.getType();
        var finalType = fieldType
                .getAnnotators()
                .stream()
                .reduce(
                        fieldType.getRawType(),
                        (rawType, annotator) -> annotator.annotate(rawType, fieldDefinition.getProperty()),
                        (x, y) -> {
                            throw new IllegalStateException();
                        }
                );
        var field = classDeclaration.addField(finalType, fieldDefinition.getName());
        if (fieldDefinition.getType().getDefaultIfNull() != null) {
            addGetterWithDefaultIfNull(classDeclaration, fieldDefinition);
        } else if (fieldDefinition.getProperty().isOptional()) {
            addOptionalGetter(classDeclaration, fieldDefinition);
        } else {
            field.addAnnotation(NotNull.class);
            toPrimitiveType(fieldType.getRawType()).ifPresent(primitiveType -> addPrimitiveGetter(classDeclaration, fieldDefinition, primitiveType));
        }
        Optional
                .ofNullable(fieldType.getInnerType())
                .ifPresent(innerType -> addMember(classDeclaration, innerType));
    }

    private void addGetterWithDefaultIfNull(ClassOrInterfaceDeclaration classDeclaration, JavaFieldDefinition fieldDefinition) {
        var fieldType = fieldDefinition.getType();
        classDeclaration
                .addMethod("get%s".formatted(Names.toClassName(fieldDefinition.getName())), PUBLIC)
                .setType(fieldType.getRawType())
                .setBody(parseBlock("{return %s == null ? %s : %s;}".formatted(fieldDefinition.getName(), fieldType.getDefaultIfNull(), fieldDefinition.getName())));
    }

    private void addOptionalGetter(ClassOrInterfaceDeclaration classDeclaration, JavaFieldDefinition fieldDefinition) {
        var fieldType = fieldDefinition.getType();
        classDeclaration
                .addMethod("get%s".formatted(Names.toClassName(fieldDefinition.getName())), PUBLIC)
                .setType("java.util.Optional<%s>".formatted(fieldType.getRawType()))
                .setBody(parseBlock("{return java.util.Optional.ofNullable(%s);}".formatted(fieldDefinition.getName())));
    }

    private void addPrimitiveGetter(ClassOrInterfaceDeclaration classDeclaration, JavaFieldDefinition fieldDefinition, Class<?> primitiveType) {
        var methodNameFormat = boolean.class.equals(primitiveType) ? "is%s" : "get%s";
        var methodName = methodNameFormat
                .formatted(Names.toClassName(fieldDefinition.getName()))
                .replaceFirst("^isIs(?=[A-Z])", "is");
        classDeclaration
                .addMethod(methodName, PUBLIC)
                .setType(primitiveType)
                .setBody(parseBlock("{return %s;}".formatted(fieldDefinition.getName())));
    }

    private void addMember(ClassOrInterfaceDeclaration classDeclaration, JavaTypeDefinition innerTypeDefinition) {
        final JavaTypeGenerator innerTypeGenerator;
        if (innerTypeDefinition instanceof JavaEnumDefinition enumDefinition) {
            innerTypeGenerator = new JavaEnumGenerator(enumDefinition);
        } else if (innerTypeDefinition instanceof JavaClassDefinition innerClassDefinition) {
            innerTypeGenerator = new JavaClassGenerator(innerClassDefinition);
        } else {
            throw new IllegalStateException();
        }
        var innerTypeCompiler = innerTypeGenerator.generate();
        classDeclaration.addMember(innerTypeCompiler.getType(0).addModifier(STATIC));
        innerTypeCompiler.getImports().forEach(compiler::addImport);
    }

    private static Optional<? extends Class<?>> toPrimitiveType(String type) {
        try {
            return Optional
                    .ofNullable(ClassUtils.wrapperToPrimitive(Class.forName(type)))
                    .filter(primitive -> !type.equals(primitive.getName()));
        } catch (Exception e) {
            return Optional.empty();
        }
    }

}
