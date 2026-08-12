package com.pokedex.core.ports.in;

import com.pokedex.core.dto.PokemonDetailDto;

/**
 * Input port — US02: full detail for one Pokémon.
 */
public interface GetPokemonDetailUseCase {

    PokemonDetailDto getById(Long id);
}
