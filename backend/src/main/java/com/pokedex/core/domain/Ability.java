package com.pokedex.core.domain;

/**
 * A Pokémon skill/ability. Plain domain value object (no framework annotations).
 */
public record Ability(String name, boolean hidden) {
}
