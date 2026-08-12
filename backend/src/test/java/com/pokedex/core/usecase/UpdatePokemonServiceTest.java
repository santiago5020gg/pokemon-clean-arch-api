package com.pokedex.core.usecase;

import com.pokedex.core.PokemonFixtures;
import com.pokedex.core.domain.Pokemon;
import com.pokedex.core.dto.PokemonDetailDto;
import com.pokedex.core.dto.PokemonUpdateRequest;
import com.pokedex.core.exception.ResourceNotFoundException;
import com.pokedex.core.ports.out.PokemonRepositoryPort;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UpdatePokemonServiceTest {

    @Mock
    PokemonRepositoryPort repository;

    @InjectMocks
    UpdatePokemonService service;

    @Test
    void update_replacesProprietaryFieldsAndKeepsReplicatedData() {
        when(repository.findById(1L)).thenReturn(Optional.of(PokemonFixtures.bulbasaur()));
        when(repository.save(any(Pokemon.class))).thenAnswer(inv -> inv.getArgument(0));

        PokemonUpdateRequest req = new PokemonUpdateRequest(
                "Bulba KR", "Johto", List.of("legendary", "favorite"));

        PokemonDetailDto dto = service.update(1L, req);

        ArgumentCaptor<Pokemon> captor = ArgumentCaptor.forClass(Pokemon.class);
        org.mockito.Mockito.verify(repository).save(captor.capture());
        Pokemon saved = captor.getValue();

        assertThat(saved.localizedName()).isEqualTo("Bulba KR");
        assertThat(saved.region()).isEqualTo("Johto");
        assertThat(saved.internalTags()).containsExactly("legendary", "favorite");
        // replicated data preserved
        assertThat(saved.name()).isEqualTo("bulbasaur");
        assertThat(saved.category()).isEqualTo("Seed Pokémon");

        assertThat(dto.localizedName()).isEqualTo("Bulba KR");
        assertThat(dto.region()).isEqualTo("Johto");
    }

    @Test
    void update_throwsNotFoundWhenMissing() {
        when(repository.findById(7L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.update(7L,
                new PokemonUpdateRequest("a", "b", List.of())))
                .isInstanceOf(ResourceNotFoundException.class);
    }
}
