package com.pokedex.core.usecase;

import com.pokedex.core.exception.ResourceNotFoundException;
import com.pokedex.core.ports.out.PokemonRepositoryPort;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DeletePokemonServiceTest {

    @Mock
    PokemonRepositoryPort repository;

    @InjectMocks
    DeletePokemonService service;

    @Test
    void delete_removesWhenExists() {
        when(repository.existsById(1L)).thenReturn(true);

        service.delete(1L);

        verify(repository).deleteById(1L);
    }

    @Test
    void delete_throwsNotFoundWhenMissing() {
        when(repository.existsById(2L)).thenReturn(false);

        assertThatThrownBy(() -> service.delete(2L))
                .isInstanceOf(ResourceNotFoundException.class);

        verify(repository, never()).deleteById(2L);
    }
}
