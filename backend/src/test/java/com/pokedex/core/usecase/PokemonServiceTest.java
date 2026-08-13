package com.pokedex.core.usecase;

import com.pokedex.core.PokemonFixtures;
import com.pokedex.core.domain.Pokemon;
import com.pokedex.core.dto.PageResult;
import com.pokedex.core.dto.PokemonDetailDto;
import com.pokedex.core.dto.PokemonSummaryDto;
import com.pokedex.core.dto.PokemonUpdateRequest;
import com.pokedex.core.dto.SyncRequest;
import com.pokedex.core.dto.SyncResult;
import com.pokedex.core.exception.ResourceNotFoundException;
import com.pokedex.core.ports.out.PokemonProviderPort;
import com.pokedex.core.ports.out.PokemonRepositoryPort;
import org.junit.jupiter.api.Nested;
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
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Unit tests for the consolidated {@link PokemonService}. Groups the coverage of the former
 * per-operation services (list, detail, sync, update, delete) with no loss of assertions.
 */
@ExtendWith(MockitoExtension.class)
class PokemonServiceTest {

    @Mock
    PokemonProviderPort provider;

    @Mock
    PokemonRepositoryPort repository;

    @InjectMocks
    PokemonService service;

    @Nested
    class ListPage {

        @Test
        void list_mapsDomainPageToSummaryDtoPage() {
            PageResult<Pokemon> page = new PageResult<>(
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

    @Nested
    class GetDetail {

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

    @Nested
    class Sync {

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

    @Nested
    class Update {

        @Test
        void update_replacesProprietaryFieldsAndKeepsReplicatedData() {
            when(repository.findById(1L)).thenReturn(Optional.of(PokemonFixtures.bulbasaur()));
            when(repository.save(any(Pokemon.class))).thenAnswer(inv -> inv.getArgument(0));

            PokemonUpdateRequest req = new PokemonUpdateRequest(
                    "Bulba KR", "Johto", List.of("legendary", "favorite"));

            PokemonDetailDto dto = service.update(1L, req);

            ArgumentCaptor<Pokemon> captor = ArgumentCaptor.forClass(Pokemon.class);
            verify(repository).save(captor.capture());
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

    @Nested
    class Delete {

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
}
