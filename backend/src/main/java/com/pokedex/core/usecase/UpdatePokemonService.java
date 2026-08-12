package com.pokedex.core.usecase;

import com.pokedex.core.domain.Pokemon;
import com.pokedex.core.dto.PokemonDetailDto;
import com.pokedex.core.dto.PokemonUpdateRequest;
import com.pokedex.core.exception.ResourceNotFoundException;
import com.pokedex.core.mapper.PokemonMapper;
import com.pokedex.core.ports.in.UpdatePokemonUseCase;
import com.pokedex.core.ports.out.PokemonRepositoryPort;

import java.util.List;

/**
 * US04 use case. Updates only the proprietary fields; 404 when the id is unknown. Structural
 * (400) validation is enforced by Bean Validation on the request DTO at the adapter boundary.
 */
public class UpdatePokemonService implements UpdatePokemonUseCase {

    private final PokemonRepositoryPort repository;

    public UpdatePokemonService(PokemonRepositoryPort repository) {
        this.repository = repository;
    }

    @Override
    public PokemonDetailDto update(Long id, PokemonUpdateRequest request) {
        Pokemon existing = repository.findById(id)
                .orElseThrow(() -> ResourceNotFoundException.pokemon(id));

        List<String> tags = request.internalTags() == null ? List.of() : request.internalTags();
        Pokemon updated = existing.withProprietary(
                request.localizedName(),
                request.region(),
                tags);

        return PokemonMapper.toDetail(repository.save(updated));
    }
}
