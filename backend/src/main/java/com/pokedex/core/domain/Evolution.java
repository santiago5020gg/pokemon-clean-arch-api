package com.pokedex.core.domain;

/**
 * A single step in a Pokémon's evolutionary lineage. {@code stage} orders the chain (1,2,3...).
 */
public record Evolution(String speciesName, int stage) {
}
