package com.pokedex.core.ports.in;

import com.pokedex.core.dto.PokemonDetailDto;
import com.pokedex.core.dto.PokemonUpdateRequest;

/**
 * Input port — US04: update the proprietary fields of a stored Pokémon.
 */
public interface UpdatePokemonUseCase {

    PokemonDetailDto update(Long id, PokemonUpdateRequest request);
}
