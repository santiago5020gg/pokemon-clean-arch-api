package com.pokedex.core.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.util.List;

/**
 * Body of {@code PUT /api/pokemon/{id}} (US04). Only the proprietary fields are editable.
 * Bean Validation (jakarta, framework-neutral) drives the 400 responses.
 */
public record PokemonUpdateRequest(
        @NotBlank @Size(max = 120) String localizedName,
        @NotBlank @Size(max = 120) String region,
        List<@Size(max = 50) String> internalTags) {
}
