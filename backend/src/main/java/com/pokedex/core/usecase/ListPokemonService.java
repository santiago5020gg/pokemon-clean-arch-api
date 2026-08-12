package com.pokedex.core.usecase;

import com.pokedex.core.domain.Pokemon;
import com.pokedex.core.dto.PageResult;
import com.pokedex.core.dto.PokemonSummaryDto;
import com.pokedex.core.mapper.PokemonMapper;
import com.pokedex.core.ports.in.ListPokemonUseCase;
import com.pokedex.core.ports.out.PokemonRepositoryPort;

/**
 * US01 use case. Depends only on the repository output port (dependency inversion) and delegates
 * the projection to the pure mapper (single responsibility).
 */
public class ListPokemonService implements ListPokemonUseCase {

    private final PokemonRepositoryPort repository;

    public ListPokemonService(PokemonRepositoryPort repository) {
        this.repository = repository;
    }

    @Override
    public PageResult<PokemonSummaryDto> list(int page, int size) {
        int safePage = Math.max(page, 0);
        int safeSize = size <= 0 ? 20 : size;
        PageResult<Pokemon> stored = repository.findAll(safePage, safeSize);
        return stored.map(PokemonMapper::toSummary);
    }
}
