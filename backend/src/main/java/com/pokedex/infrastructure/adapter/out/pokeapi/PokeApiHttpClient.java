package com.pokedex.infrastructure.adapter.out.pokeapi;

import com.pokedex.infrastructure.adapter.out.pokeapi.PokeApiModels.EvolutionChainResponse;
import com.pokedex.infrastructure.adapter.out.pokeapi.PokeApiModels.ListResponse;
import com.pokedex.infrastructure.adapter.out.pokeapi.PokeApiModels.PokemonResponse;
import com.pokedex.infrastructure.adapter.out.pokeapi.PokeApiModels.SpeciesResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

/**
 * Low-level PokeAPI HTTP access via {@link RestClient}, with Caffeine caching to mitigate the
 * documented N+1 problem (a page of 20 triggers dozens of detail/species/evolution calls). Kept as
 * a separate bean from {@link PokeApiClient} so the {@code @Cacheable} methods are invoked through
 * the Spring proxy (self-invocation would bypass the cache).
 */
@Component
public class PokeApiHttpClient {

    private final RestClient restClient;

    public PokeApiHttpClient(@Value("${pokeapi.base-url:https://pokeapi.co/api/v2}") String baseUrl) {
        this.restClient = RestClient.builder().baseUrl(baseUrl).build();
    }

    @Cacheable("pokeapi-list")
    public ListResponse listPokemon(int limit, int offset) {
        return restClient.get()
                .uri(uri -> uri.path("/pokemon").queryParam("limit", limit).queryParam("offset", offset).build())
                .retrieve()
                .body(ListResponse.class);
    }

    @Cacheable("pokeapi-pokemon")
    public PokemonResponse getPokemon(long id) {
        return restClient.get().uri("/pokemon/{id}", id).retrieve().body(PokemonResponse.class);
    }

    @Cacheable("pokeapi-species")
    public SpeciesResponse getSpecies(long id) {
        return restClient.get().uri("/pokemon-species/{id}", id).retrieve().body(SpeciesResponse.class);
    }

    @Cacheable("pokeapi-evolution")
    public EvolutionChainResponse getEvolutionChain(long id) {
        return restClient.get().uri("/evolution-chain/{id}", id).retrieve().body(EvolutionChainResponse.class);
    }
}
