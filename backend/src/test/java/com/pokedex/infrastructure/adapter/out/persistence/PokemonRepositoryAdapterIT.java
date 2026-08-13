package com.pokedex.infrastructure.adapter.out.persistence;

import com.pokedex.application.TestcontainersConfiguration;
import com.pokedex.core.domain.Ability;
import com.pokedex.core.domain.Evolution;
import com.pokedex.core.domain.Pokemon;
import com.pokedex.core.domain.Stats;
import com.pokedex.core.dto.PageResult;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Persistence adapter slice test against a real PostgreSQL (Flyway migration + JPA validation).
 * Skipped without Docker so the build stays green.
 */
@Testcontainers(disabledWithoutDocker = true)
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Import({TestcontainersConfiguration.class, PokemonRepositoryAdapter.class})
class PokemonRepositoryAdapterIT {

    @Autowired
    PokemonRepositoryAdapter adapter;

    @Autowired
    PokemonJpaRepository jpaRepository;

    // The V2 seed migration pre-populates demo Pokemon; clear it so each test controls its own data.
    @BeforeEach
    void clearSeed() {
        jpaRepository.deleteAllInBatch();
    }

    private Pokemon sample(long id, String name) {
        return new Pokemon(id, name, "sprite", "image", 69, 7, "Seed Pokémon", "desc",
                new Stats(45, 49, 49, 65, 65, 45),
                List.of(new Ability("overgrow", false), new Ability("chlorophyll", true)),
                List.of(new Evolution("bulbasaur", 1), new Evolution("ivysaur", 2)),
                "Localized", "Kanto", List.of("starter", "favorite"), null, null);
    }

    @Test
    void save_thenFindById_roundTripsWithChildrenAndCsvTags() {
        adapter.save(sample(1L, "bulbasaur"));

        Optional<Pokemon> found = adapter.findById(1L);

        assertThat(found).isPresent();
        Pokemon p = found.get();
        assertThat(p.name()).isEqualTo("bulbasaur");
        assertThat(p.stats().hp()).isEqualTo(45);
        assertThat(p.abilities()).extracting(Ability::name)
                .containsExactlyInAnyOrder("overgrow", "chlorophyll");
        assertThat(p.evolutions()).extracting(Evolution::speciesName)
                .containsExactly("bulbasaur", "ivysaur");
        assertThat(p.internalTags()).containsExactly("starter", "favorite");
        assertThat(p.createdAt()).isNotNull();
    }

    @Test
    void save_isUpsert_updatesExistingAndReplacesChildren() {
        adapter.save(sample(1L, "bulbasaur"));
        Pokemon updated = sample(1L, "bulbasaur").withProprietary("New", "Johto", List.of("x"));
        adapter.save(updated);

        Pokemon p = adapter.findById(1L).orElseThrow();
        assertThat(p.region()).isEqualTo("Johto");
        assertThat(p.internalTags()).containsExactly("x");
        assertThat(adapter.findAll(0, 10).totalElements()).isEqualTo(1L);
    }

    @Test
    void findAll_paginates() {
        adapter.save(sample(1L, "bulbasaur"));
        adapter.save(sample(2L, "ivysaur"));
        adapter.save(sample(3L, "venusaur"));

        PageResult<Pokemon> page = adapter.findAll(0, 2);

        assertThat(page.content()).hasSize(2);
        assertThat(page.totalElements()).isEqualTo(3L);
        assertThat(page.totalPages()).isEqualTo(2);
    }

    @Test
    void deleteById_removes() {
        adapter.save(sample(1L, "bulbasaur"));
        adapter.deleteById(1L);
        assertThat(adapter.existsById(1L)).isFalse();
    }
}
