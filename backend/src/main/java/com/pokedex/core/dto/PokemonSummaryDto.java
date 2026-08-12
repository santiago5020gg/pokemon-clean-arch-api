package com.pokedex.core.dto;

import java.util.List;

/**
 * List projection for US01: sprite, category, mass and skills.
 */
public record PokemonSummaryDto(
        Long id,
        String name,
        String spriteUrl,
        String category,
        Integer weight,
        List<String> abilities) {
}
