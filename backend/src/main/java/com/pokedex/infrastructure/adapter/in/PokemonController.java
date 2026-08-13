package com.pokedex.infrastructure.adapter.in;

import com.pokedex.core.dto.PageResult;
import com.pokedex.core.dto.PokemonDetailDto;
import com.pokedex.core.dto.PokemonSummaryDto;
import com.pokedex.core.dto.PokemonUpdateRequest;
import com.pokedex.core.dto.SyncRequest;
import com.pokedex.core.dto.SyncResult;
import com.pokedex.core.ports.in.PokemonServicePort;
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
 * REST input adapter for the local {@code pokemon} resource. Depends only on the cohesive
 * {@link PokemonServicePort} input port, delegating all logic to the core.
 */
@RestController
@RequestMapping("/api/pokemon")
public class PokemonController {

    private final PokemonServicePort pokemonService;

    public PokemonController(PokemonServicePort pokemonService) {
        this.pokemonService = pokemonService;
    }

    @GetMapping
    public PageResult<PokemonSummaryDto> list(
            @RequestParam(required = false) String q,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        return pokemonService.list(q, page, size);
    }

    @GetMapping("/{id}")
    public PokemonDetailDto getById(@PathVariable Long id) {
        return pokemonService.getById(id);
    }

    @PostMapping("/sync")
    @ResponseStatus(HttpStatus.CREATED)
    public SyncResult sync(@RequestBody(required = false) SyncRequest request) {
        return pokemonService.sync(request == null ? new SyncRequest(null, null) : request);
    }

    @PutMapping("/{id}")
    public PokemonDetailDto update(@PathVariable Long id,
                                   @Valid @RequestBody PokemonUpdateRequest request) {
        return pokemonService.update(id, request);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        pokemonService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
