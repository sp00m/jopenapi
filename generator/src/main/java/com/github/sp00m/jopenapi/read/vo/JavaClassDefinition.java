package com.github.sp00m.jopenapi.read.vo;

import lombok.AllArgsConstructor;
import lombok.Value;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

@Value
@AllArgsConstructor
public class JavaClassDefinition implements JavaTypeDefinition {

    String packageName;
    String name;
    String description;
    List<JavaFieldDefinition> fields;
    Set<String> implementedTypes;

    public JavaClassDefinition(String packageName, String name, String description, List<JavaFieldDefinition> fields) {
        this(packageName, name, description, fields, new TreeSet<>());
    }

    public JavaClassDefinition addFields(List<JavaFieldDefinition> newFields) {
        List<JavaFieldDefinition> updatedFields = new ArrayList<>(fields);
        updatedFields.addAll(newFields);
        return new JavaClassDefinition(packageName, name, description, updatedFields);
    }

    public void addImplementedType(String newImplementedType) {
        implementedTypes.add(newImplementedType);
    }

}
