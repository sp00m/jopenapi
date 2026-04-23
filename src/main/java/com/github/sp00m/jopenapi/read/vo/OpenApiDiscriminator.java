package com.github.sp00m.jopenapi.read.vo;

import io.swagger.v3.oas.models.media.Discriminator;
import lombok.RequiredArgsConstructor;

import java.util.Collections;
import java.util.Map;
import java.util.Optional;

/**
 * Anti-corruption wrapper around Swagger's {@link Discriminator}. Returns safe defaults
 * (empty map for {@code mapping}) so callers never deal with {@code null}.
 */
@RequiredArgsConstructor
public final class OpenApiDiscriminator {

    private final Discriminator discriminator;

    public Map<String, String> getMapping() {
        return Optional
                .ofNullable(discriminator.getMapping())
                .map(Collections::unmodifiableMap)
                .orElseGet(Collections::emptyMap);
    }

    public String getPropertyName() {
        return discriminator.getPropertyName();
    }

}
