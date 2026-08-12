package com.pokedex.infrastructure.adapter.in;

import com.pokedex.application.config.SecurityConfig;
import com.pokedex.core.dto.AuthResponse;
import com.pokedex.core.dto.UserDto;
import com.pokedex.core.exception.DuplicateResourceException;
import com.pokedex.core.exception.InvalidCredentialsException;
import com.pokedex.core.ports.in.LoginUseCase;
import com.pokedex.core.ports.in.RegisterUserUseCase;
import com.pokedex.infrastructure.adapter.out.security.JwtAuthenticationFilter;
import com.pokedex.infrastructure.adapter.out.security.JwtTokenProvider;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AuthController.class)
@Import({SecurityConfig.class, JwtAuthenticationFilter.class, JwtTokenProvider.class})
class AuthControllerTest {

    @Autowired
    MockMvc mockMvc;

    @MockitoBean
    RegisterUserUseCase registerUser;
    @MockitoBean
    LoginUseCase login;

    @Test
    void register_returns201AndNeverThePassword() throws Exception {
        when(registerUser.register(any())).thenReturn(new UserDto(1L, "ash", "ash@pokedex.io", "USER"));

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"username\":\"ash\",\"email\":\"ash@pokedex.io\",\"password\":\"s3cret!\"}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.role").value("USER"))
                .andExpect(jsonPath("$.password").doesNotExist())
                .andExpect(jsonPath("$.passwordHash").doesNotExist());
    }

    @Test
    void register_invalidPayload_returns400() throws Exception {
        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"username\":\"a\",\"email\":\"not-an-email\",\"password\":\"x\"}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400));
    }

    @Test
    void register_duplicate_returns409() throws Exception {
        when(registerUser.register(any())).thenThrow(new DuplicateResourceException("dup"));

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"username\":\"ash\",\"email\":\"ash@pokedex.io\",\"password\":\"s3cret!\"}"))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.status").value(409));
    }

    @Test
    void login_returns200WithBearerToken() throws Exception {
        when(login.login(any())).thenReturn(AuthResponse.bearer("jwt", 3600));

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"username\":\"ash\",\"password\":\"s3cret!\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").value("jwt"))
                .andExpect(jsonPath("$.type").value("Bearer"))
                .andExpect(jsonPath("$.expiresIn").value(3600));
    }

    @Test
    void login_badCredentials_returns401() throws Exception {
        when(login.login(any())).thenThrow(new InvalidCredentialsException("bad"));

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"username\":\"ash\",\"password\":\"wrong\"}"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.status").value(401));
    }
}
