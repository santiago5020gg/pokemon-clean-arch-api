package com.pokedex.core.ports.in;

import com.pokedex.core.dto.PageResult;
import com.pokedex.core.dto.PokemonSummaryDto;

/**
 * Input port — US01: paginated list of stored Pokémon.
 */
public interface ListPokemonUseCase {

    PageResult<PokemonSummaryDto> list(int page, int size);
}
