package com.github.sp00m.jopenapi.read;

import com.github.sp00m.jopenapi.Names;
import com.github.sp00m.jopenapi.read.vo.JavaFieldDefinition;
import com.github.sp00m.jopenapi.read.vo.JavaTypeDefinition;
import com.github.sp00m.jopenapi.read.vo.JavaValueClassDefinition;
import com.github.sp00m.jopenapi.read.vo.OpenApiComponent;
import com.github.sp00m.jopenapi.read.vo.OpenApiProperty;
import lombok.RequiredArgsConstructor;

import javax.annotation.Nullable;

@RequiredArgsConstructor
final class OpenApiComponentReader {

    private final String packageName;
    private final OpenApiComponent component;

    @Nullable
    public JavaTypeDefinition read() {
        var type = new OpenApiSchemaReader(packageName, component.getName(), component.getSchema()).read();
        if (type == null) {
            return null;
        }
        if (type.getDefinition() != null && !type.isWrapped()) {
            return type.getDefinition();
        } else {
            var property = new OpenApiProperty("value", component.getSchema(), false);
            var fieldDefinition = new JavaFieldDefinition(property, "value", type);
            return new JavaValueClassDefinition(packageName, Names.toClassName(component.getName()), component.getSchema().getDescription(), fieldDefinition);
        }
    }

}
