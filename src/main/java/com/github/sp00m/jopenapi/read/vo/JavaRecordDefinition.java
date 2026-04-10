package com.github.sp00m.jopenapi.read.vo;

import java.util.*;

public record JavaRecordDefinition(
        String packageName,
        String name,
        String description,
        List<JavaFieldDefinition> fields,
        Set<String> implementedTypes
) implements JavaTypeDefinition {

    public JavaRecordDefinition {
        fields = Collections.unmodifiableList(fields);
        implementedTypes = Collections.unmodifiableSortedSet(new TreeSet<>(implementedTypes));
    }

    public JavaRecordDefinition(String packageName, String name, String description, List<JavaFieldDefinition> fields) {
        this(packageName, name, description, fields, Collections.emptySet());
    }

    public JavaRecordDefinition addFields(List<JavaFieldDefinition> newFields) {
        List<JavaFieldDefinition> updatedFields = new ArrayList<>(fields);
        updatedFields.addAll(newFields);
        return new JavaRecordDefinition(packageName, name, description, updatedFields, implementedTypes);
    }

    public JavaRecordDefinition withFields(List<JavaFieldDefinition> newFields) {
        return new JavaRecordDefinition(packageName, name, description, newFields, implementedTypes);
    }

    public JavaRecordDefinition withImplementedType(String newImplementedType) {
        var updatedTypes = new TreeSet<>(implementedTypes);
        updatedTypes.add(newImplementedType);
        return new JavaRecordDefinition(packageName, name, description, fields, updatedTypes);
    }

}
