package com.pokedex.infrastructure.adapter.out.pokeapi;

import com.pokedex.core.domain.Pokemon;
import com.pokedex.infrastructure.adapter.out.pokeapi.PokeApiModels.AbilityEntry;
import com.pokedex.infrastructure.adapter.out.pokeapi.PokeApiModels.ChainLink;
import com.pokedex.infrastructure.adapter.out.pokeapi.PokeApiModels.EvolutionChainRef;
import com.pokedex.infrastructure.adapter.out.pokeapi.PokeApiModels.EvolutionChainResponse;
import com.pokedex.infrastructure.adapter.out.pokeapi.PokeApiModels.FlavorTextEntry;
import com.pokedex.infrastructure.adapter.out.pokeapi.PokeApiModels.Genus;
import com.pokedex.infrastructure.adapter.out.pokeapi.PokeApiModels.ListResponse;
import com.pokedex.infrastructure.adapter.out.pokeapi.PokeApiModels.NamedApiResource;
import com.pokedex.infrastructure.adapter.out.pokeapi.PokeApiModels.OfficialArtwork;
import com.pokedex.infrastructure.adapter.out.pokeapi.PokeApiModels.OtherSprites;
import com.pokedex.infrastructure.adapter.out.pokeapi.PokeApiModels.PokemonResponse;
import com.pokedex.infrastructure.adapter.out.pokeapi.PokeApiModels.SpeciesResponse;
import com.pokedex.infrastructure.adapter.out.pokeapi.PokeApiModels.Sprites;
import com.pokedex.infrastructure.adapter.out.pokeapi.PokeApiModels.StatEntry;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Orchestration unit test for {@link PokeApiClient#fetchPage}: verifies the list -> detail ->
 * species -> evolution flow and correct id extraction, with the HTTP layer mocked.
 */
@ExtendWith(MockitoExtension.class)
class PokeApiClientTest {

    @Mock
    PokeApiHttpClient http;

    @InjectMocks
    PokeApiClient client;

    @Test
    void fetchPage_assemblesDomainFromTheThreeCalls() {
        when(http.listPokemon(1, 0)).thenReturn(new ListResponse(1,
                List.of(new NamedApiResource("bulbasaur", "https://pokeapi.co/api/v2/pokemon/1/"))));
        when(http.getPokemon(1L)).thenReturn(new PokemonResponse(
                1L, "bulbasaur", 69, 7,
                List.of(new AbilityEntry(new NamedApiResource("overgrow", null), false)),
                new Sprites("sprite.png", new OtherSprites(new OfficialArtwork("art.png"))),
                List.of(new StatEntry(45, new NamedApiResource("hp", null))),
                new NamedApiResource("bulbasaur", "https://pokeapi.co/api/v2/pokemon-species/1/")));
        when(http.getSpecies(1L)).thenReturn(new SpeciesResponse(
                List.of(new Genus("Seed Pokémon", new NamedApiResource("en", null))),
                List.of(new FlavorTextEntry("clean text", new NamedApiResource("en", null))),
                new EvolutionChainRef("https://pokeapi.co/api/v2/evolution-chain/1/")));
        when(http.getEvolutionChain(1L)).thenReturn(new EvolutionChainResponse(
                new ChainLink(new NamedApiResource("bulbasaur", null),
                        List.of(new ChainLink(new NamedApiResource("ivysaur", null), List.of())))));

        List<Pokemon> page = client.fetchPage(1, 0);

        assertThat(page).hasSize(1);
        Pokemon p = page.get(0);
        assertThat(p.id()).isEqualTo(1L);
        assertThat(p.category()).isEqualTo("Seed Pokémon");
        assertThat(p.imageUrl()).isEqualTo("art.png");
        assertThat(p.evolutions()).extracting(e -> e.speciesName()).containsExactly("bulbasaur", "ivysaur");
        verify(http).getEvolutionChain(1L);
    }

    @Test
    void fetchPage_returnsEmptyWhenListNull() {
        when(http.listPokemon(20, 0)).thenReturn(null);
        assertThat(client.fetchPage(20, 0)).isEmpty();
    }
}
