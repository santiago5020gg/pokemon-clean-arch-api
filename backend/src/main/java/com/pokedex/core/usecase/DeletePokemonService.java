package com.pokedex.core.usecase;

import com.pokedex.core.exception.ResourceNotFoundException;
import com.pokedex.core.ports.in.DeletePokemonUseCase;
import com.pokedex.core.ports.out.PokemonRepositoryPort;

/**
 * CRUD delete use case. 404 when the id is unknown, otherwise removes the record.
 */
public class DeletePokemonService implements DeletePokemonUseCase {

    private final PokemonRepositoryPort repository;

    public DeletePokemonService(PokemonRepositoryPort repository) {
        this.repository = repository;
    }

    @Override
    public void delete(Long id) {
        if (!repository.existsById(id)) {
            throw ResourceNotFoundException.pokemon(id);
        }
        repository.deleteById(id);
    }
}
