package com.pokedex.core.mapper;

import com.pokedex.core.PokemonFixtures;
import com.pokedex.core.domain.Pokemon;
import com.pokedex.core.dto.PokemonDetailDto;
import com.pokedex.core.dto.PokemonSummaryDto;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class PokemonMapperTest {

    @Test
    void toSummary_projectsListFields() {
        Pokemon bulbasaur = PokemonFixtures.bulbasaur();

        PokemonSummaryDto dto = PokemonMapper.toSummary(bulbasaur);

        assertThat(dto.id()).isEqualTo(1L);
        assertThat(dto.name()).isEqualTo("bulbasaur");
        assertThat(dto.spriteUrl()).isEqualTo("https://img/sprites/1.png");
        assertThat(dto.category()).isEqualTo("Seed Pokémon");
        assertThat(dto.weight()).isEqualTo(69);
        assertThat(dto.abilities()).containsExactly("overgrow", "chlorophyll");
    }

    @Test
    void toDetail_projectsImageStatsDescriptionEvolutionsAndProprietaryFields() {
        Pokemon bulbasaur = PokemonFixtures.bulbasaur();

        PokemonDetailDto dto = PokemonMapper.toDetail(bulbasaur);

        assertThat(dto.imageUrl()).isEqualTo("https://img/artwork/1.png");
        assertThat(dto.stats().hp()).isEqualTo(45);
        assertThat(dto.stats().specialAttack()).isEqualTo(65);
        assertThat(dto.description()).isEqualTo("A strange seed was planted on its back at birth.");
        assertThat(dto.evolutions()).containsExactly("bulbasaur", "ivysaur", "venusaur");
        assertThat(dto.localizedName()).isEqualTo("Bulbasaur ES");
        assertThat(dto.region()).isEqualTo("Kanto");
        assertThat(dto.internalTags()).containsExactly("starter");
    }

    @Test
    void toDetail_handlesNullStatsGracefully() {
        Pokemon noStats = new Pokemon(9L, "x", null, null, 1, 1, "c", "d",
                null, java.util.List.of(), java.util.List.of(), null, null, java.util.List.of(), null, null);

        PokemonDetailDto dto = PokemonMapper.toDetail(noStats);

        assertThat(dto.stats().hp()).isZero();
    }
}
