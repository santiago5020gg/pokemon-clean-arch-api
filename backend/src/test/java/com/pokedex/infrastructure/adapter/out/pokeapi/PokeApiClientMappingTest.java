package com.pokedex.infrastructure.adapter.out.pokeapi;

import com.pokedex.core.domain.Evolution;
import com.pokedex.core.domain.Pokemon;
import com.pokedex.infrastructure.adapter.out.pokeapi.PokeApiModels.AbilityEntry;
import com.pokedex.infrastructure.adapter.out.pokeapi.PokeApiModels.ChainLink;
import com.pokedex.infrastructure.adapter.out.pokeapi.PokeApiModels.EvolutionChainRef;
import com.pokedex.infrastructure.adapter.out.pokeapi.PokeApiModels.EvolutionChainResponse;
import com.pokedex.infrastructure.adapter.out.pokeapi.PokeApiModels.FlavorTextEntry;
import com.pokedex.infrastructure.adapter.out.pokeapi.PokeApiModels.Genus;
import com.pokedex.infrastructure.adapter.out.pokeapi.PokeApiModels.NamedApiResource;
import com.pokedex.infrastructure.adapter.out.pokeapi.PokeApiModels.OfficialArtwork;
import com.pokedex.infrastructure.adapter.out.pokeapi.PokeApiModels.OtherSprites;
import com.pokedex.infrastructure.adapter.out.pokeapi.PokeApiModels.PokemonResponse;
import com.pokedex.infrastructure.adapter.out.pokeapi.PokeApiModels.SpeciesResponse;
import com.pokedex.infrastructure.adapter.out.pokeapi.PokeApiModels.Sprites;
import com.pokedex.infrastructure.adapter.out.pokeapi.PokeApiModels.StatEntry;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Pure unit tests for the PokeAPI-to-domain mapping (no HTTP, no Spring): id extraction,
 * English filtering, description cleaning, official-artwork image and recursive evolution walk.
 */
class PokeApiClientMappingTest {

    @Test
    void extractId_parsesTrailingId() {
        assertThat(PokeApiClient.extractId("https://pokeapi.co/api/v2/pokemon/25/")).isEqualTo(25L);
        assertThat(PokeApiClient.extractId("https://pokeapi.co/api/v2/evolution-chain/10")).isEqualTo(10L);
    }

    @Test
    void toDomain_mapsAllRelevantFieldsAndCleansDescription() {
        PokemonResponse detail = new PokemonResponse(
                1L, "bulbasaur", 69, 7,
                List.of(new AbilityEntry(new NamedApiResource("overgrow", null), false),
                        new AbilityEntry(new NamedApiResource("chlorophyll", null), true)),
                new Sprites("sprite.png", new OtherSprites(new OfficialArtwork("artwork.png"))),
                List.of(new StatEntry(45, new NamedApiResource("hp", null)),
                        new StatEntry(49, new NamedApiResource("attack", null)),
                        new StatEntry(49, new NamedApiResource("defense", null)),
                        new StatEntry(65, new NamedApiResource("special-attack", null)),
                        new StatEntry(65, new NamedApiResource("special-defense", null)),
                        new StatEntry(45, new NamedApiResource("speed", null))),
                new NamedApiResource("bulbasaur", "https://pokeapi.co/api/v2/pokemon-species/1/"));

        SpeciesResponse species = new SpeciesResponse(
                List.of(new Genus("Semilla", new NamedApiResource("es", null)),
                        new Genus("Seed Pokémon", new NamedApiResource("en", null))),
                List.of(new FlavorTextEntry("A strange seed was\nplanted on its\fback.",
                        new NamedApiResource("en", null))),
                new EvolutionChainRef("https://pokeapi.co/api/v2/evolution-chain/1/"));

        EvolutionChainResponse evo = new EvolutionChainResponse(
                new ChainLink(new NamedApiResource("bulbasaur", null),
                        List.of(new ChainLink(new NamedApiResource("ivysaur", null),
                                List.of(new ChainLink(new NamedApiResource("venusaur", null), List.of()))))));

        Pokemon p = PokeApiClient.toDomain(detail, species, evo);

        assertThat(p.id()).isEqualTo(1L);
        assertThat(p.spriteUrl()).isEqualTo("sprite.png");
        assertThat(p.imageUrl()).isEqualTo("artwork.png");
        assertThat(p.weight()).isEqualTo(69);
        assertThat(p.category()).isEqualTo("Seed Pokémon");
        assertThat(p.description()).isEqualTo("A strange seed was planted on its back.");
        assertThat(p.stats().hp()).isEqualTo(45);
        assertThat(p.stats().specialAttack()).isEqualTo(65);
        assertThat(p.abilities()).extracting(a -> a.name()).containsExactly("overgrow", "chlorophyll");
        assertThat(p.evolutions()).extracting(Evolution::speciesName)
                .containsExactly("bulbasaur", "ivysaur", "venusaur");
        assertThat(p.evolutions()).extracting(Evolution::stage).containsExactly(1, 2, 3);
        // proprietary fields are not provided by PokeAPI
        assertThat(p.localizedName()).isNull();
        assertThat(p.internalTags()).isEmpty();
    }

    @Test
    void flattenEvolution_handlesNullChain() {
        assertThat(PokeApiClient.flattenEvolution(null)).isEmpty();
        assertThat(PokeApiClient.flattenEvolution(new EvolutionChainResponse(null))).isEmpty();
    }
}
