package com.pokedex.core.usecase;

import com.pokedex.core.domain.Role;
import com.pokedex.core.domain.User;
import com.pokedex.core.dto.AuthResponse;
import com.pokedex.core.dto.LoginRequest;
import com.pokedex.core.dto.RegisterRequest;
import com.pokedex.core.dto.UserDto;
import com.pokedex.core.exception.DuplicateResourceException;
import com.pokedex.core.exception.InvalidCredentialsException;
import com.pokedex.core.ports.out.PasswordEncoderPort;
import com.pokedex.core.ports.out.TokenProviderPort;
import com.pokedex.core.ports.out.UserRepositoryPort;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Unit tests for the consolidated {@link UserService}. Groups the coverage of the former
 * RegisterUserService and LoginService with no loss of assertions.
 */
@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    UserRepositoryPort userRepository;

    @Mock
    PasswordEncoderPort passwordEncoder;

    @Mock
    TokenProviderPort tokenProvider;

    @InjectMocks
    UserService service;

    private User ash() {
        return new User(1L, "ash", "ash@pokedex.io", "HASH", Role.USER, Instant.now());
    }

    @Nested
    class Register {

        @Test
        void register_hashesPasswordAndPersistsUser() {
            when(userRepository.existsByUsername("ash")).thenReturn(false);
            when(userRepository.existsByEmail("ash@pokedex.io")).thenReturn(false);
            when(passwordEncoder.encode("s3cret!")).thenReturn("HASHED");
            when(userRepository.save(org.mockito.ArgumentMatchers.any(User.class)))
                    .thenAnswer(inv -> {
                        User u = inv.getArgument(0);
                        return new User(10L, u.username(), u.email(), u.passwordHash(), u.role(), Instant.now());
                    });

            UserDto dto = service.register(new RegisterRequest("ash", "ash@pokedex.io", "s3cret!"));

            ArgumentCaptor<User> captor = ArgumentCaptor.forClass(User.class);
            verify(userRepository).save(captor.capture());
            assertThat(captor.getValue().passwordHash()).isEqualTo("HASHED");
            assertThat(captor.getValue().role()).isEqualTo(Role.USER);

            assertThat(dto.id()).isEqualTo(10L);
            assertThat(dto.username()).isEqualTo("ash");
            assertThat(dto.role()).isEqualTo("USER");
        }

        @Test
        void register_rejectsDuplicateUsername() {
            when(userRepository.existsByUsername("ash")).thenReturn(true);

            assertThatThrownBy(() -> service.register(
                    new RegisterRequest("ash", "ash@pokedex.io", "s3cret!")))
                    .isInstanceOf(DuplicateResourceException.class);

            verify(userRepository, never()).save(org.mockito.ArgumentMatchers.any());
        }

        @Test
        void register_rejectsDuplicateEmail() {
            when(userRepository.existsByUsername("ash")).thenReturn(false);
            when(userRepository.existsByEmail("ash@pokedex.io")).thenReturn(true);

            assertThatThrownBy(() -> service.register(
                    new RegisterRequest("ash", "ash@pokedex.io", "s3cret!")))
                    .isInstanceOf(DuplicateResourceException.class);
        }
    }

    @Nested
    class Login {

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
}
