package com.pokedex.core.usecase;

import com.pokedex.core.domain.Role;
import com.pokedex.core.domain.User;
import com.pokedex.core.dto.AuthResponse;
import com.pokedex.core.dto.LoginRequest;
import com.pokedex.core.exception.InvalidCredentialsException;
import com.pokedex.core.ports.out.PasswordEncoderPort;
import com.pokedex.core.ports.out.TokenProviderPort;
import com.pokedex.core.ports.out.UserRepositoryPort;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class LoginServiceTest {

    @Mock
    UserRepositoryPort userRepository;

    @Mock
    PasswordEncoderPort passwordEncoder;

    @Mock
    TokenProviderPort tokenProvider;

    @InjectMocks
    LoginService service;

    private User ash() {
        return new User(1L, "ash", "ash@pokedex.io", "HASH", Role.USER, Instant.now());
    }

    @Test
    void login_returnsBearerTokenOnValidCredentials() {
        when(userRepository.findByUsername("ash")).thenReturn(Optional.of(ash()));
        when(passwordEncoder.matches("s3cret!", "HASH")).thenReturn(true);
        when(tokenProvider.generateToken(org.mockito.ArgumentMatchers.any(User.class))).thenReturn("jwt-token");
        when(tokenProvider.getExpiresInSeconds()).thenReturn(3600L);

        AuthResponse response = service.login(new LoginRequest("ash", "s3cret!"));

        assertThat(response.token()).isEqualTo("jwt-token");
        assertThat(response.type()).isEqualTo("Bearer");
        assertThat(response.expiresIn()).isEqualTo(3600L);
    }

    @Test
    void login_throwsOnUnknownUser() {
        when(userRepository.findByUsername("ghost")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.login(new LoginRequest("ghost", "x")))
                .isInstanceOf(InvalidCredentialsException.class);
    }

    @Test
    void login_throwsOnWrongPassword() {
        when(userRepository.findByUsername("ash")).thenReturn(Optional.of(ash()));
        when(passwordEncoder.matches("wrong", "HASH")).thenReturn(false);

        assertThatThrownBy(() -> service.login(new LoginRequest("ash", "wrong")))
                .isInstanceOf(InvalidCredentialsException.class);
    }
}
