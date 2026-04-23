package com.github.sp00m.jopenapi.read;

import com.github.sp00m.jopenapi.Names;
import com.github.sp00m.jopenapi.read.vo.*;
import lombok.RequiredArgsConstructor;

import javax.annotation.Nullable;

/**
 * Reads a single OpenAPI component and decides what Java type it becomes.
 *
 * <p>If the schema maps directly to a record/enum/interface, that definition is returned as-is.
 * Otherwise (e.g. a top-level {@code type: array} or {@code type: integer}), the schema is
 * wrapped in a {@link JavaValueRecordDefinition} — a single-field record that acts as a
 * type-safe wrapper (e.g. {@code record SimpleArray(List<String> value)}).
 */
@RequiredArgsConstructor
final class OpenApiComponentReader {

    private final String packageName;
    private final OpenApiComponent component;

    @Nullable
    public JavaTypeDefinition read() {
        var type = new OpenApiSchemaReader(packageName, component.name(), component.schema()).read();
        if (type == null) {
            return null;
        }
        if (type.definition() != null && !type.collection()) {
            return type.definition();
        } else {
            var property = new OpenApiProperty("value", component.schema(), false);
            var fieldDefinition = new JavaFieldDefinition(property, "value", type);
            return new JavaValueRecordDefinition(
                    packageName,
                    Names.toClassName(component.name()),
                    component.schema().getDescription(),
                    fieldDefinition
            );
        }
    }

}
