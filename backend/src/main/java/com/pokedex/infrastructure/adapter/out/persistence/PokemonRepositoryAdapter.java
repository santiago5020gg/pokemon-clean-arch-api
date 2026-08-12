package com.pokedex.infrastructure.adapter.out.persistence;

import com.pokedex.core.domain.Pokemon;
import com.pokedex.core.dto.PageResult;
import com.pokedex.core.ports.out.PokemonRepositoryPort;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.Optional;

/**
 * Output adapter implementing {@link PokemonRepositoryPort} over Spring Data JPA. Translates
 * between the domain model and {@link PokemonEntity}, and maps Spring's {@code Page} into the
 * framework-neutral {@link PageResult}.
 */
@Component
public class PokemonRepositoryAdapter implements PokemonRepositoryPort {

    private final PokemonJpaRepository jpa;

    public PokemonRepositoryAdapter(PokemonJpaRepository jpa) {
        this.jpa = jpa;
    }

    @Override
    @Transactional(readOnly = true)
    public PageResult<Pokemon> findAll(int page, int size) {
        var result = jpa.findAll(PageRequest.of(page, size, Sort.by("id").ascending()));
        return new PageResult<>(
                result.getContent().stream().map(PokemonPersistenceMapper::toDomain).toList(),
                result.getNumber(),
                result.getSize(),
                result.getTotalElements(),
                result.getTotalPages());
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Pokemon> findById(Long id) {
        return jpa.findById(id).map(PokemonPersistenceMapper::toDomain);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean existsById(Long id) {
        return jpa.existsById(id);
    }

    @Override
    @Transactional
    public Pokemon save(Pokemon pokemon) {
        PokemonEntity entity = jpa.findById(pokemon.id()).orElseGet(PokemonEntity::new);
        boolean isNew = entity.getCreatedAt() == null;
        PokemonPersistenceMapper.applyToEntity(pokemon, entity);
        Instant now = Instant.now();
        if (isNew) {
            entity.setCreatedAt(pokemon.createdAt() != null ? pokemon.createdAt() : now);
        }
        entity.setUpdatedAt(now);
        return PokemonPersistenceMapper.toDomain(jpa.save(entity));
    }

    @Override
    @Transactional
    public void deleteById(Long id) {
        jpa.deleteById(id);
    }
}
