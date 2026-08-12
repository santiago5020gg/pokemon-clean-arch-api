package com.pokedex.infrastructure.adapter.out.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Spring Data repository over {@link PokemonEntity}.
 */
public interface PokemonJpaRepository extends JpaRepository<PokemonEntity, Long> {
}
