package com.pokedex.core.dto;

/**
 * Body of {@code POST /api/pokemon/sync} (US03). How much of the PokeAPI to replicate.
 * Both fields are optional; sensible defaults are applied by the use case.
 */
public record SyncRequest(Integer limit, Integer offset) {

    public int limitOrDefault() {
        return limit == null || limit <= 0 ? 20 : limit;
    }

    public int offsetOrDefault() {
        return offset == null || offset < 0 ? 0 : offset;
    }
}
