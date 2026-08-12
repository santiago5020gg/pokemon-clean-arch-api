package com.pokedex.core.domain;

/**
 * The six fixed core statistics of a Pokémon (US02).
 */
public record Stats(
        int hp,
        int attack,
        int defense,
        int specialAttack,
        int specialDefense,
        int speed) {
}
