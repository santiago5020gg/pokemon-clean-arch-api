package com.pokedex.infrastructure.adapter.in;

import com.pokedex.core.dto.PageResult;
import com.pokedex.core.dto.PokemonDetailDto;
import com.pokedex.core.dto.PokemonSummaryDto;
import com.pokedex.core.dto.PokemonUpdateRequest;
import com.pokedex.core.dto.SyncRequest;
import com.pokedex.core.dto.SyncResult;
import com.pokedex.core.ports.in.DeletePokemonUseCase;
import com.pokedex.core.ports.in.GetPokemonDetailUseCase;
import com.pokedex.core.ports.in.ListPokemonUseCase;
import com.pokedex.core.ports.in.SyncPokemonUseCase;
import com.pokedex.core.ports.in.UpdatePokemonUseCase;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

/**
 * REST input adapter for the local {@code pokemon} resource. Depends only on input ports
 * (interface segregation), delegating all logic to the core use cases.
 */
@RestController
@RequestMapping("/api/pokemon")
public class PokemonController {

    private final ListPokemonUseCase listPokemon;
    private final GetPokemonDetailUseCase getPokemonDetail;
    private final SyncPokemonUseCase syncPokemon;
    private final UpdatePokemonUseCase updatePokemon;
    private final DeletePokemonUseCase deletePokemon;

    public PokemonController(ListPokemonUseCase listPokemon,
                             GetPokemonDetailUseCase getPokemonDetail,
                             SyncPokemonUseCase syncPokemon,
                             UpdatePokemonUseCase updatePokemon,
                             DeletePokemonUseCase deletePokemon) {
        this.listPokemon = listPokemon;
        this.getPokemonDetail = getPokemonDetail;
        this.syncPokemon = syncPokemon;
        this.updatePokemon = updatePokemon;
        this.deletePokemon = deletePokemon;
    }

    @GetMapping
    public PageResult<PokemonSummaryDto> list(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        return listPokemon.list(page, size);
    }

    @GetMapping("/{id}")
    public PokemonDetailDto getById(@PathVariable Long id) {
        return getPokemonDetail.getById(id);
    }

    @PostMapping("/sync")
    @ResponseStatus(HttpStatus.CREATED)
    public SyncResult sync(@RequestBody(required = false) SyncRequest request) {
        return syncPokemon.sync(request == null ? new SyncRequest(null, null) : request);
    }

    @PutMapping("/{id}")
    public PokemonDetailDto update(@PathVariable Long id,
                                   @Valid @RequestBody PokemonUpdateRequest request) {
        return updatePokemon.update(id, request);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        deletePokemon.delete(id);
        return ResponseEntity.noContent().build();
    }
}
