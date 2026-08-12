package com.pokedex.core.usecase;

import com.pokedex.core.PokemonFixtures;
import com.pokedex.core.dto.PokemonDetailDto;
import com.pokedex.core.exception.ResourceNotFoundException;
import com.pokedex.core.ports.out.PokemonRepositoryPort;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GetPokemonDetailServiceTest {

    @Mock
    PokemonRepositoryPort repository;

    @InjectMocks
    GetPokemonDetailService service;

    @Test
    void getById_returnsDetailWhenFound() {
        when(repository.findById(1L)).thenReturn(Optional.of(PokemonFixtures.bulbasaur()));

        PokemonDetailDto dto = service.getById(1L);

        assertThat(dto.id()).isEqualTo(1L);
        assertThat(dto.evolutions()).containsExactly("bulbasaur", "ivysaur", "venusaur");
    }

    @Test
    void getById_throwsNotFoundWhenMissing() {
        when(repository.findById(999L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.getById(999L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("999");
    }
}
