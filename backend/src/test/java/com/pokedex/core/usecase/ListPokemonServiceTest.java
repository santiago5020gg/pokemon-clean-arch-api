package com.pokedex.core.usecase;

import com.pokedex.core.PokemonFixtures;
import com.pokedex.core.dto.PageResult;
import com.pokedex.core.dto.PokemonSummaryDto;
import com.pokedex.core.ports.out.PokemonRepositoryPort;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ListPokemonServiceTest {

    @Mock
    PokemonRepositoryPort repository;

    @InjectMocks
    ListPokemonService service;

    @Test
    void list_mapsDomainPageToSummaryDtoPage() {
        PageResult<com.pokedex.core.domain.Pokemon> page = new PageResult<>(
                List.of(PokemonFixtures.bulbasaur(), PokemonFixtures.ivysaur()),
                0, 20, 2L, 1);
        when(repository.findAll(0, 20)).thenReturn(page);

        PageResult<PokemonSummaryDto> result = service.list(0, 20);

        assertThat(result.content()).hasSize(2);
        assertThat(result.content().get(0).name()).isEqualTo("bulbasaur");
        assertThat(result.content().get(0).abilities()).containsExactly("overgrow", "chlorophyll");
        assertThat(result.totalElements()).isEqualTo(2L);
        assertThat(result.totalPages()).isEqualTo(1);
    }

    @Test
    void list_normalizesNegativePageAndNonPositiveSize() {
        when(repository.findAll(0, 20)).thenReturn(
                new PageResult<>(List.of(), 0, 20, 0L, 0));

        service.list(-5, 0);

        verify(repository).findAll(eq(0), eq(20));
    }
}
