package com.pokedex.core.mapper;

import com.pokedex.core.domain.Ability;
import com.pokedex.core.domain.Evolution;
import com.pokedex.core.domain.Pokemon;
import com.pokedex.core.domain.Stats;
import com.pokedex.core.dto.PokemonDetailDto;
import com.pokedex.core.dto.PokemonSummaryDto;
import com.pokedex.core.dto.StatsDto;

import java.util.List;

/**
 * Pure converter between the {@link Pokemon} domain model and its DTO projections.
 * Framework-free and stateless (single responsibility, easy to unit-test).
 */
public final class PokemonMapper {

    private PokemonMapper() {
    }

    public static PokemonSummaryDto toSummary(Pokemon pokemon) {
        return new PokemonSummaryDto(
                pokemon.id(),
                pokemon.name(),
                pokemon.spriteUrl(),
                pokemon.category(),
                pokemon.weight(),
                pokemon.abilities().stream().map(Ability::name).toList());
    }

    public static PokemonDetailDto toDetail(Pokemon pokemon) {
        return new PokemonDetailDto(
                pokemon.id(),
                pokemon.name(),
                pokemon.imageUrl(),
                toStatsDto(pokemon.stats()),
                pokemon.description(),
                pokemon.evolutions().stream().map(Evolution::speciesName).toList(),
                pokemon.localizedName(),
                pokemon.region(),
                pokemon.internalTags());
    }

    private static StatsDto toStatsDto(Stats stats) {
        if (stats == null) {
            return new StatsDto(0, 0, 0, 0, 0, 0);
        }
        return new StatsDto(
                stats.hp(),
                stats.attack(),
                stats.defense(),
                stats.specialAttack(),
                stats.specialDefense(),
                stats.speed());
    }

    public static List<PokemonSummaryDto> toSummaries(List<Pokemon> pokemon) {
        return pokemon.stream().map(PokemonMapper::toSummary).toList();
    }
}
