package com.pokedex.core.ports.out;

import com.pokedex.core.domain.Pokemon;
import com.pokedex.core.dto.PageResult;

import java.util.Optional;

/**
 * Output port to the local Pokémon store. Speaks only in domain models, never JPA entities.
 */
public interface PokemonRepositoryPort {

    /**
     * Page through stored Pokémon, optionally filtered by a case-insensitive name fragment.
     *
     * @param query name fragment to match; {@code null}/blank returns all
     */
    PageResult<Pokemon> findAll(String query, int page, int size);

    Optional<Pokemon> findById(Long id);

    boolean existsById(Long id);

    Pokemon save(Pokemon pokemon);

    void deleteById(Long id);
}
