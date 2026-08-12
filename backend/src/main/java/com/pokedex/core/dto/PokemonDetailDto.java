package com.pokedex.core.dto;

import java.util.List;

/**
 * Detail projection for US02: image, core statistics, description, evolutionary lineage and the
 * proprietary fields (localizedName, region, internalTags).
 */
public record PokemonDetailDto(
        Long id,
        String name,
        String imageUrl,
        StatsDto stats,
        String description,
        List<String> evolutions,
        String localizedName,
        String region,
        List<String> internalTags) {
}
