package com.pokedex.core.usecase;

import com.pokedex.core.PokemonFixtures;
import com.pokedex.core.domain.Pokemon;
import com.pokedex.core.dto.SyncRequest;
import com.pokedex.core.dto.SyncResult;
import com.pokedex.core.ports.out.PokemonProviderPort;
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
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SyncPokemonServiceTest {

    @Mock
    PokemonProviderPort provider;

    @Mock
    PokemonRepositoryPort repository;

    @InjectMocks
    SyncPokemonService service;

    @Test
    void sync_countsCreatedAndUpdatedAndPreservesProprietaryOnExisting() {
        when(provider.fetchPage(20, 0)).thenReturn(
                List.of(PokemonFixtures.bulbasaur(), PokemonFixtures.ivysaur()));
        // bulbasaur already exists locally with proprietary fields set
        Pokemon existingBulba = PokemonFixtures.bulbasaur()
                .withProprietary("Existing Name", "Existing Region", List.of("keep-me"));
        when(repository.findById(1L)).thenReturn(Optional.of(existingBulba));
        when(repository.findById(2L)).thenReturn(Optional.empty());
        when(repository.save(any(Pokemon.class))).thenAnswer(inv -> inv.getArgument(0));

        SyncResult result = service.sync(new SyncRequest(20, 0));

        assertThat(result.synced()).isEqualTo(2);
        assertThat(result.created()).isEqualTo(1);
        assertThat(result.updated()).isEqualTo(1);
        assertThat(result.items()).hasSize(2);

        ArgumentCaptor<Pokemon> captor = ArgumentCaptor.forClass(Pokemon.class);
        verify(repository, org.mockito.Mockito.times(2)).save(captor.capture());
        Pokemon savedBulba = captor.getAllValues().stream()
                .filter(p -> p.id().equals(1L)).findFirst().orElseThrow();
        assertThat(savedBulba.localizedName()).isEqualTo("Existing Name");
        assertThat(savedBulba.internalTags()).containsExactly("keep-me");
    }

    @Test
    void sync_appliesDefaultsWhenRequestNull() {
        when(provider.fetchPage(20, 0)).thenReturn(List.of());

        SyncResult result = service.sync(null);

        assertThat(result.synced()).isZero();
        verify(provider).fetchPage(20, 0);
    }
}
