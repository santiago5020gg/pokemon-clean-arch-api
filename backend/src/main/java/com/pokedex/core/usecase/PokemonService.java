package com.pokedex.core.usecase;

import com.pokedex.core.domain.Pokemon;
import com.pokedex.core.dto.PageResult;
import com.pokedex.core.dto.PokemonDetailDto;
import com.pokedex.core.dto.PokemonSummaryDto;
import com.pokedex.core.dto.PokemonUpdateRequest;
import com.pokedex.core.dto.SyncRequest;
import com.pokedex.core.dto.SyncResult;
import com.pokedex.core.exception.ResourceNotFoundException;
import com.pokedex.core.mapper.PokemonMapper;
import com.pokedex.core.ports.in.PokemonServicePort;
import com.pokedex.core.ports.out.PokemonProviderPort;
import com.pokedex.core.ports.out.PokemonRepositoryPort;

import java.util.ArrayList;
import java.util.List;

/**
 * Cohesive application service for the {@code pokemon} aggregate. Implements every Pokémon use
 * case (US01 list, US02 detail, US03 sync, US04 update, CRUD delete) against the same output ports
 * as before (dependency inversion), delegating projection to the pure {@link PokemonMapper}.
 */
public class PokemonService implements PokemonServicePort {

    private final PokemonProviderPort provider;
    private final PokemonRepositoryPort repository;

    public PokemonService(PokemonProviderPort provider, PokemonRepositoryPort repository) {
        this.provider = provider;
        this.repository = repository;
    }

    @Override
    public PageResult<PokemonSummaryDto> list(int page, int size) {
        int safePage = Math.max(page, 0);
        int safeSize = size <= 0 ? 20 : size;
        PageResult<Pokemon> stored = repository.findAll(safePage, safeSize);
        return stored.map(PokemonMapper::toSummary);
    }

    @Override
    public PokemonDetailDto getById(Long id) {
        return repository.findById(id)
                .map(PokemonMapper::toDetail)
                .orElseThrow(() -> ResourceNotFoundException.pokemon(id));
    }

    @Override
    public SyncResult sync(SyncRequest request) {
        int limit = request == null ? 20 : request.limitOrDefault();
        int offset = request == null ? 0 : request.offsetOrDefault();

        List<Pokemon> fetched = provider.fetchPage(limit, offset);

        int created = 0;
        int updated = 0;
        List<SyncResult.SyncItem> items = new ArrayList<>();

        for (Pokemon incoming : fetched) {
            Pokemon existing = repository.findById(incoming.id()).orElse(null);
            Pokemon toPersist = incoming;
            if (existing != null) {
                // Keep proprietary fields owned by our system; refresh replicated PokeAPI data.
                toPersist = incoming.withProprietary(
                        existing.localizedName(),
                        existing.region(),
                        existing.internalTags());
                updated++;
            } else {
                created++;
            }
            Pokemon saved = repository.save(toPersist);
            items.add(new SyncResult.SyncItem(saved.id(), saved.name(), saved.category()));
        }

        return new SyncResult(fetched.size(), created, updated, items);
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

    @Override
    public void delete(Long id) {
        if (!repository.existsById(id)) {
            throw ResourceNotFoundException.pokemon(id);
        }
        repository.deleteById(id);
    }
}
