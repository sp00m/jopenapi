package com.github.sp00m.jopenapi.read.vo;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

public record JavaRecordDefinition(
        String packageName,
        String name,
        String description,
        List<JavaFieldDefinition> fields,
        Set<String> implementedTypes
) implements JavaTypeDefinition {

    public JavaRecordDefinition(String packageName, String name, String description, List<JavaFieldDefinition> fields) {
        this(packageName, name, description, fields, new TreeSet<>());
    }

    public JavaRecordDefinition addFields(List<JavaFieldDefinition> newFields) {
        List<JavaFieldDefinition> updatedFields = new ArrayList<>(fields);
        updatedFields.addAll(newFields);
        return new JavaRecordDefinition(packageName, name, description, updatedFields);
    }

    public void addImplementedType(String newImplementedType) {
        implementedTypes.add(newImplementedType);
    }

}
