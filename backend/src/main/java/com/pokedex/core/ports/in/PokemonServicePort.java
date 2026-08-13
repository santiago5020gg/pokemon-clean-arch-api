package com.pokedex.core.ports.in;

import com.pokedex.core.dto.PageResult;
import com.pokedex.core.dto.PokemonDetailDto;
import com.pokedex.core.dto.PokemonSummaryDto;
import com.pokedex.core.dto.PokemonUpdateRequest;
import com.pokedex.core.dto.SyncRequest;
import com.pokedex.core.dto.SyncResult;

/**
 * Input port for the {@code pokemon} aggregate. Groups every Pokémon operation behind one
 * cohesive interface (list, detail, sync, update, delete) so the core exposes a single service
 * per aggregate instead of one port per operation.
 */
public interface PokemonServicePort {

    PageResult<PokemonSummaryDto> list(int page, int size);

    PokemonDetailDto getById(Long id);

    SyncResult sync(SyncRequest request);

    PokemonDetailDto update(Long id, PokemonUpdateRequest request);

    void delete(Long id);
}
