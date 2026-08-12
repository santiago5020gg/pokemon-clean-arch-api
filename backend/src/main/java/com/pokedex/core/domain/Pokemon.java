package com.pokedex.core.domain;

import java.time.Instant;
import java.util.List;

/**
 * Core Pokémon domain model. A framework-free aggregate combining data replicated from the
 * PokeAPI with the proprietary fields owned by our system (localizedName, region, internalTags).
 */
public record Pokemon(
        Long id,
        String name,
        String spriteUrl,
        String imageUrl,
        Integer weight,
        Integer height,
        String category,
        String description,
        Stats stats,
        List<Ability> abilities,
        List<Evolution> evolutions,
        String localizedName,
        String region,
        List<String> internalTags,
        Instant createdAt,
        Instant updatedAt) {

    public Pokemon {
        abilities = abilities == null ? List.of() : List.copyOf(abilities);
        evolutions = evolutions == null ? List.of() : List.copyOf(evolutions);
        internalTags = internalTags == null ? List.of() : List.copyOf(internalTags);
    }

    /**
     * Returns a copy of this Pokémon with only the proprietary (US04-editable) fields replaced.
     * Keeps the replicated PokeAPI data intact.
     */
    public Pokemon withProprietary(String localizedName, String region, List<String> internalTags) {
        return new Pokemon(
                id, name, spriteUrl, imageUrl, weight, height, category, description,
                stats, abilities, evolutions,
                localizedName, region, internalTags,
                createdAt, updatedAt);
    }
}
