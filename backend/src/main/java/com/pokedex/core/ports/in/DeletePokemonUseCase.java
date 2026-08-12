package com.pokedex.core.ports.in;

/**
 * Input port — CRUD: remove a stored Pokémon.
 */
public interface DeletePokemonUseCase {

    void delete(Long id);
}
