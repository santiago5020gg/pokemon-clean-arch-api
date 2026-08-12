package com.pokedex.core.usecase;

import com.pokedex.core.domain.Pokemon;
import com.pokedex.core.dto.SyncRequest;
import com.pokedex.core.dto.SyncResult;
import com.pokedex.core.ports.in.SyncPokemonUseCase;
import com.pokedex.core.ports.out.PokemonProviderPort;
import com.pokedex.core.ports.out.PokemonRepositoryPort;

import java.util.ArrayList;
import java.util.List;

/**
 * US03 use case. Pulls a page from the PokeAPI provider port and persists it locally, preserving
 * any previously-set proprietary fields on records that already exist (created vs updated).
 */
public class SyncPokemonService implements SyncPokemonUseCase {

    private final PokemonProviderPort provider;
    private final PokemonRepositoryPort repository;

    public SyncPokemonService(PokemonProviderPort provider, PokemonRepositoryPort repository) {
        this.provider = provider;
        this.repository = repository;
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
}
