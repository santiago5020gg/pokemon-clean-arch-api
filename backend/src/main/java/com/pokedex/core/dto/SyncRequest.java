package com.pokedex.core.dto;

/**
 * Body of {@code POST /api/pokemon/sync} (US03). How much of the PokeAPI to replicate.
 * Both fields are optional; sensible defaults are applied by the use case.
 */
public record SyncRequest(Integer limit, Integer offset) {

    /** Default page size when the caller does not specify one. */
    private static final int DEFAULT_LIMIT = 20;
    /** Hard upper bound: replicating N Pokémon costs ~3 PokeAPI calls each, so cap at Gen 1 (151). */
    private static final int MAX_LIMIT = 151;

    public int limitOrDefault() {
        if (limit == null || limit <= 0) {
            return DEFAULT_LIMIT;
        }
        return Math.min(limit, MAX_LIMIT);
    }

    public int offsetOrDefault() {
        return offset == null || offset < 0 ? 0 : offset;
    }
}
