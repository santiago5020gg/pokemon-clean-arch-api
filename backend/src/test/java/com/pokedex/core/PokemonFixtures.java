package com.pokedex.core;

import com.pokedex.core.domain.Ability;
import com.pokedex.core.domain.Evolution;
import com.pokedex.core.domain.Pokemon;
import com.pokedex.core.domain.Stats;

import java.time.Instant;
import java.util.List;

/**
 * Shared domain fixtures for core unit tests.
 */
public final class PokemonFixtures {

    private PokemonFixtures() {
    }

    public static Pokemon bulbasaur() {
        return new Pokemon(
                1L,
                "bulbasaur",
                "https://img/sprites/1.png",
                "https://img/artwork/1.png",
                69,
                7,
                "Seed Pokémon",
                "A strange seed was planted on its back at birth.",
                new Stats(45, 49, 49, 65, 65, 45),
                List.of(new Ability("overgrow", false), new Ability("chlorophyll", true)),
                List.of(new Evolution("bulbasaur", 1),
                        new Evolution("ivysaur", 2),
                        new Evolution("venusaur", 3)),
                "Bulbasaur ES",
                "Kanto",
                List.of("starter"),
                Instant.parse("2026-01-01T00:00:00Z"),
                Instant.parse("2026-01-01T00:00:00Z"));
    }

    public static Pokemon ivysaur() {
        return new Pokemon(
                2L, "ivysaur",
                "https://img/sprites/2.png",
                "https://img/artwork/2.png",
                130, 10, "Seed Pokémon",
                "There is a bud on this Pokémon's back.",
                new Stats(60, 62, 63, 80, 80, 60),
                List.of(new Ability("overgrow", false)),
                List.of(new Evolution("ivysaur", 1)),
                null, null, List.of(),
                null, null);
    }
}
