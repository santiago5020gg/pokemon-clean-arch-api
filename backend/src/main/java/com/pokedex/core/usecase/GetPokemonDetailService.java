package com.pokedex.core.usecase;

import com.pokedex.core.dto.PokemonDetailDto;
import com.pokedex.core.exception.ResourceNotFoundException;
import com.pokedex.core.mapper.PokemonMapper;
import com.pokedex.core.ports.in.GetPokemonDetailUseCase;
import com.pokedex.core.ports.out.PokemonRepositoryPort;

/**
 * US02 use case. Returns the full detail DTO or raises a 404-mapped domain exception.
 */
public class GetPokemonDetailService implements GetPokemonDetailUseCase {

    private final PokemonRepositoryPort repository;

    public GetPokemonDetailService(PokemonRepositoryPort repository) {
        this.repository = repository;
    }

    @Override
    public PokemonDetailDto getById(Long id) {
        return repository.findById(id)
                .map(PokemonMapper::toDetail)
                .orElseThrow(() -> ResourceNotFoundException.pokemon(id));
    }
}
