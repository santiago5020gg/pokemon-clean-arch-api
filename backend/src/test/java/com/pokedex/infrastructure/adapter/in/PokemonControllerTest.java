package com.pokedex.infrastructure.adapter.in;

import com.pokedex.application.config.SecurityConfig;
import com.pokedex.core.domain.Role;
import com.pokedex.core.domain.User;
import com.pokedex.core.dto.PageResult;
import com.pokedex.core.dto.PokemonDetailDto;
import com.pokedex.core.dto.PokemonSummaryDto;
import com.pokedex.core.dto.StatsDto;
import com.pokedex.core.exception.ResourceNotFoundException;
import com.pokedex.core.ports.in.PokemonServicePort;
import com.pokedex.infrastructure.adapter.out.security.JwtAuthenticationFilter;
import com.pokedex.infrastructure.adapter.out.security.JwtTokenProvider;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(PokemonController.class)
@Import({SecurityConfig.class, JwtAuthenticationFilter.class, JwtTokenProvider.class})
class PokemonControllerTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    JwtTokenProvider tokenProvider;

    @MockitoBean
    PokemonServicePort pokemonService;

    private String bearer() {
        return "Bearer " + tokenProvider.generateToken(
                new User(1L, "ash", "ash@pokedex.io", "hash", Role.USER, null));
    }

    @Test
    void list_isPublic_returnsOkWithPage() throws Exception {
        when(pokemonService.list(null, 0, 20)).thenReturn(new PageResult<>(
                List.of(new PokemonSummaryDto(1L, "bulbasaur", "s.png", "Seed Pokémon", 69, List.of("overgrow"))),
                0, 20, 1L, 1));

        mockMvc.perform(get("/api/pokemon?page=0&size=20"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].name").value("bulbasaur"))
                .andExpect(jsonPath("$.totalElements").value(1));
    }

    @Test
    void list_forwardsSearchQuery() throws Exception {
        when(pokemonService.list("char", 0, 20)).thenReturn(new PageResult<>(
                List.of(new PokemonSummaryDto(4L, "charmander", "s.png", "Lizard Pokémon", 85, List.of("blaze"))),
                0, 20, 1L, 1));

        mockMvc.perform(get("/api/pokemon?q=char&page=0&size=20"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].name").value("charmander"));

        verify(pokemonService).list("char", 0, 20);
    }

    @Test
    void getById_returnsDetail() throws Exception {
        when(pokemonService.getById(1L)).thenReturn(new PokemonDetailDto(
                1L, "bulbasaur", "art.png", new StatsDto(45, 49, 49, 65, 65, 45),
                "desc", List.of("bulbasaur", "ivysaur"), "Bulba ES", "Kanto", List.of("starter")));

        mockMvc.perform(get("/api/pokemon/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.imageUrl").value("art.png"))
                .andExpect(jsonPath("$.stats.hp").value(45))
                .andExpect(jsonPath("$.evolutions[1]").value("ivysaur"));
    }

    @Test
    void getById_returns404WhenMissing() throws Exception {
        when(pokemonService.getById(999L)).thenThrow(ResourceNotFoundException.pokemon(999L));

        mockMvc.perform(get("/api/pokemon/999"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.path").value("/api/pokemon/999"));
    }

    @Test
    void sync_withoutToken_returns401() throws Exception {
        mockMvc.perform(post("/api/pokemon/sync").contentType(MediaType.APPLICATION_JSON).content("{}"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void sync_withToken_returns201() throws Exception {
        when(pokemonService.sync(any())).thenReturn(
                new com.pokedex.core.dto.SyncResult(0, 0, 0, List.of()));

        mockMvc.perform(post("/api/pokemon/sync")
                        .header("Authorization", bearer())
                        .contentType(MediaType.APPLICATION_JSON).content("{\"limit\":5,\"offset\":0}"))
                .andExpect(status().isCreated());
    }

    @Test
    void update_withoutToken_returns401() throws Exception {
        mockMvc.perform(put("/api/pokemon/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"localizedName\":\"X\",\"region\":\"Y\",\"internalTags\":[]}"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void update_withToken_invalidBody_returns400() throws Exception {
        mockMvc.perform(put("/api/pokemon/1")
                        .header("Authorization", bearer())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"localizedName\":\"\",\"region\":\"\"}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400));
    }

    @Test
    void update_withToken_returns200() throws Exception {
        when(pokemonService.update(eq(1L), any())).thenReturn(new PokemonDetailDto(
                1L, "bulbasaur", "art.png", new StatsDto(45, 49, 49, 65, 65, 45),
                "desc", List.of(), "New", "Johto", List.of("x")));

        mockMvc.perform(put("/api/pokemon/1")
                        .header("Authorization", bearer())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"localizedName\":\"New\",\"region\":\"Johto\",\"internalTags\":[\"x\"]}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.region").value("Johto"));
    }

    @Test
    void delete_withToken_returns204() throws Exception {
        mockMvc.perform(delete("/api/pokemon/1").header("Authorization", bearer()))
                .andExpect(status().isNoContent());
        verify(pokemonService).delete(1L);
    }

    @Test
    void delete_missing_returns404() throws Exception {
        doThrow(ResourceNotFoundException.pokemon(2L)).when(pokemonService).delete(2L);

        mockMvc.perform(delete("/api/pokemon/2").header("Authorization", bearer()))
                .andExpect(status().isNotFound());
    }
}
