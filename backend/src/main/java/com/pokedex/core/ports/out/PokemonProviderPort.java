package com.pokedex.core.ports.out;

import com.pokedex.core.domain.Pokemon;

import java.util.List;

/**
 * Output port to the external PokeAPI. The adapter fully assembles domain {@link Pokemon}
 * (detail + species + evolution) so the core never sees PokeAPI JSON shapes.
 */
public interface PokemonProviderPort {

    /**
     * Fetch a page of fully-assembled Pokémon from the PokeAPI.
     *
     * @param limit  how many to fetch
     * @param offset starting index
     */
    List<Pokemon> fetchPage(int limit, int offset);
}
