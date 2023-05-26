package com.github.sp00m.jopenapi.generate;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.sp00m.jopenapi.Names;
import com.github.sp00m.jopenapi.read.vo.JavaClassDefinition;
import com.github.sp00m.jopenapi.read.vo.JavaFieldDefinition;
import com.github.sp00m.jopenapi.read.vo.JavaTypeDefinition;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Builder.Default;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Value;
import lombok.extern.jackson.Jacksonized;
import org.apache.commons.lang3.ClassUtils;

import java.util.Optional;

import static com.github.javaparser.StaticJavaParser.parseBlock;
import static com.github.javaparser.StaticJavaParser.parseExpression;
import static com.github.javaparser.ast.Modifier.Keyword.PUBLIC;
import static com.github.javaparser.ast.Modifier.Keyword.STATIC;

@RequiredArgsConstructor
final class JavaClassGenerator implements JavaTypeGenerator {

    private final JavaClassDefinition classDefinition;
    private final CompilationUnit compiler = new CompilationUnit();

    @Override
    public CompilationUnit generate() {
        compiler.addImport(AccessLevel.class);
        var classDeclaration = compiler
                .addClass(classDefinition.getName())
                .addAnnotation(Value.class)
                .addAnnotation(Jacksonized.class)
                .addSingleMemberAnnotation(Getter.class, "AccessLevel.NONE");
        classDeclaration
                .addAndGetAnnotation(Builder.class)
                .addPair("toBuilder", "true");
        classDefinition
                .getImplementedTypes()
                .forEach(classDeclaration::addImplementedType);
        classDefinition
                .getFields()
                .forEach(field -> addField(compiler, classDeclaration, field));
        return compiler;
    }

    static MethodDeclaration addField(CompilationUnit compiler, ClassOrInterfaceDeclaration classDeclaration, JavaFieldDefinition fieldDefinition) {
        var fieldType = fieldDefinition.getType();
        Optional
                .ofNullable(fieldType.getDefinition())
                .ifPresent(typeDefinition -> addMember(compiler, classDeclaration, typeDefinition));
        var fieldDeclaration = Optional
                .ofNullable(fieldType.getDefaultValue())
                .map(defaultValue -> classDeclaration.addFieldWithInitializer(fieldType.getFullName(), fieldDefinition.getName(), parseExpression(defaultValue)).addAnnotation(Default.class))
                .orElseGet(() -> classDeclaration.addField(fieldType.getFullName(), fieldDefinition.getName()));
        Optional
                .ofNullable(fieldType.getDescription())
                .ifPresent(fieldDeclaration::setJavadocComment);
        fieldType
                .getFieldAnnotators()
                .forEach(annotator -> annotator.annotate(fieldDeclaration, fieldDefinition.getProperty()));
        if (!fieldDefinition.getProperty().isOptional()) {
            fieldDeclaration.addAnnotation(NotNull.class);
            return toPrimitiveType(fieldType.getFullName())
                    .map(primitiveType -> addPrimitiveGetter(classDeclaration, fieldDefinition, primitiveType))
                    .orElseGet(() -> addGetter(classDeclaration, fieldDefinition));
        } else if (fieldType.getDefaultValue() != null) {
            return toPrimitiveType(fieldType.getFullName())
                    .map(primitiveType -> addPrimitiveGetterWithDefaultValue(classDeclaration, fieldDefinition, primitiveType))
                    .orElseGet(() -> addGetterWithDefaultValue(classDeclaration, fieldDefinition));
        } else {
            return addOptionalGetter(classDeclaration, fieldDefinition);
        }
    }

    private static MethodDeclaration addGetterWithDefaultValue(ClassOrInterfaceDeclaration classDeclaration, JavaFieldDefinition fieldDefinition) {
        var fieldType = fieldDefinition.getType();
        return addGetter(
                classDeclaration,
                fieldDefinition,
                fieldType.getFullName(),
                "{return %s == null ? %s : %s;}".formatted(fieldDefinition.getName(), fieldType.getDefaultValue(), fieldDefinition.getName())
        );
    }

    private static MethodDeclaration addGetter(ClassOrInterfaceDeclaration classDeclaration, JavaFieldDefinition fieldDefinition) {
        return addGetter(
                classDeclaration,
                fieldDefinition,
                fieldDefinition.getType().getFullName(),
                "{return %s;}".formatted(fieldDefinition.getName())
        );
    }

    private static MethodDeclaration addOptionalGetter(ClassOrInterfaceDeclaration classDeclaration, JavaFieldDefinition fieldDefinition) {
        classDeclaration.tryAddImportToParentCompilationUnit(Optional.class);
        return addGetter(
                classDeclaration,
                fieldDefinition,
                "Optional<%s>".formatted(fieldDefinition.getType().getFullName()),
                "{return Optional.ofNullable(%s);}".formatted(fieldDefinition.getName())
        );
    }

    private static MethodDeclaration addGetter(ClassOrInterfaceDeclaration classDeclaration, JavaFieldDefinition fieldDefinition, String returnType, String body) {
        var fieldType = fieldDefinition.getType();
        return classDeclaration
                .addMethod("get%s".formatted(Names.toClassName(fieldDefinition.getName())), PUBLIC)
                .setType(returnType)
                .setBody(parseBlock(body));
    }

    private static MethodDeclaration addPrimitiveGetter(ClassOrInterfaceDeclaration classDeclaration, JavaFieldDefinition fieldDefinition, Class<?> primitiveType) {
        return addPrimitiveGetter(
                classDeclaration,
                fieldDefinition,
                primitiveType,
                "{return %s;}".formatted(fieldDefinition.getName())
        );
    }

    private static MethodDeclaration addPrimitiveGetterWithDefaultValue(ClassOrInterfaceDeclaration classDeclaration, JavaFieldDefinition fieldDefinition, Class<?> primitiveType) {
        return addPrimitiveGetter(
                classDeclaration,
                fieldDefinition,
                primitiveType,
                "{return %s == null ? %s : %s;}".formatted(fieldDefinition.getName(), fieldDefinition.getType().getDefaultValue(), fieldDefinition.getName())
        );
    }

    private static MethodDeclaration addPrimitiveGetter(ClassOrInterfaceDeclaration classDeclaration, JavaFieldDefinition fieldDefinition, Class<?> primitiveType, String body) {
        var getterNameFormat = boolean.class.equals(primitiveType) ? "is%s" : "get%s";
        var getterName = getterNameFormat
                .formatted(Names.toClassName(fieldDefinition.getName()))
                .replaceFirst("^isIs(?=[A-Z])", "is");
        return classDeclaration
                .addMethod(getterName, PUBLIC)
                .setType(primitiveType)
                .setBody(parseBlock(body));
    }

    private static void addMember(CompilationUnit compiler, ClassOrInterfaceDeclaration classDeclaration, JavaTypeDefinition innerTypeDefinition) {
        var innerTypeCompiler = JavaGenerator.generateCompiler(innerTypeDefinition);
        var innerType = innerTypeCompiler.getType(0);
        if (innerType instanceof ClassOrInterfaceDeclaration declaration) {
            if (declaration.isInterface()) {
                throw new IllegalStateException("'oneOf' not supported at property level");
            }
            declaration.addModifier(STATIC);
        }
        classDeclaration.addMember(innerType);
        innerTypeCompiler.getImports().forEach(compiler::addImport);
    }

    private static Optional<? extends Class<?>> toPrimitiveType(String wrapperType) {
        try {
            return Optional
                    .ofNullable(ClassUtils.wrapperToPrimitive(Class.forName("java.lang.%s".formatted(wrapperType))))
                    .filter(primitive -> !wrapperType.equals(primitive.getName()));
        } catch (Throwable t) {
            return Optional.empty();
        }
    }

}
