package com.pokedex.core.ports.in;

import com.pokedex.core.dto.SyncRequest;
import com.pokedex.core.dto.SyncResult;

/**
 * Input port — US03: replicate Pokémon from the PokeAPI into the local store.
 */
public interface SyncPokemonUseCase {

    SyncResult sync(SyncRequest request);
}
