package com.pokedex.infrastructure.adapter.out.persistence;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Spring Data repository over {@link PokemonEntity}.
 */
public interface PokemonJpaRepository extends JpaRepository<PokemonEntity, Long> {

    /** Case-insensitive name search (US01 server-side filtering). */
    Page<PokemonEntity> findByNameContainingIgnoreCase(String name, Pageable pageable);
}
