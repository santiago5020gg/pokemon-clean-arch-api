package com.pokedex.core.dto;

/**
 * Core statistics projection (US02).
 */
public record StatsDto(
        int hp,
        int attack,
        int defense,
        int specialAttack,
        int specialDefense,
        int speed) {
}
