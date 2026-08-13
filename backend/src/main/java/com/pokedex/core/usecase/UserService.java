package com.pokedex.core.usecase;

import com.pokedex.core.domain.Role;
import com.pokedex.core.domain.User;
import com.pokedex.core.dto.AuthResponse;
import com.pokedex.core.dto.LoginRequest;
import com.pokedex.core.dto.RegisterRequest;
import com.pokedex.core.dto.UserDto;
import com.pokedex.core.exception.DuplicateResourceException;
import com.pokedex.core.exception.InvalidCredentialsException;
import com.pokedex.core.mapper.UserMapper;
import com.pokedex.core.ports.in.UserServicePort;
import com.pokedex.core.ports.out.PasswordEncoderPort;
import com.pokedex.core.ports.out.TokenProviderPort;
import com.pokedex.core.ports.out.UserRepositoryPort;

/**
 * Cohesive application service for the {@code user} aggregate. Implements registration and login
 * against the same output ports as before. Registration enforces uniqueness (409) and never stores
 * a plain password; login returns a uniform 401 for both an unknown username and a wrong password
 * (no user-enumeration leak).
 */
public class UserService implements UserServicePort {

    private final UserRepositoryPort userRepository;
    private final PasswordEncoderPort passwordEncoder;
    private final TokenProviderPort tokenProvider;

    public UserService(UserRepositoryPort userRepository,
                       PasswordEncoderPort passwordEncoder,
                       TokenProviderPort tokenProvider) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.tokenProvider = tokenProvider;
    }

    @Override
    public UserDto register(RegisterRequest request) {
        if (userRepository.existsByUsername(request.username())) {
            throw new DuplicateResourceException("Username already exists: " + request.username());
        }
        if (userRepository.existsByEmail(request.email())) {
            throw new DuplicateResourceException("Email already exists: " + request.email());
        }

        User toSave = new User(
                null,
                request.username(),
                request.email(),
                passwordEncoder.encode(request.password()),
                Role.USER,
                null);

        return UserMapper.toDto(userRepository.save(toSave));
    }

    @Override
    public AuthResponse login(LoginRequest request) {
        User user = userRepository.findByUsername(request.username())
                .orElseThrow(() -> new InvalidCredentialsException("Invalid username or password"));

        if (!passwordEncoder.matches(request.password(), user.passwordHash())) {
            throw new InvalidCredentialsException("Invalid username or password");
        }

        String token = tokenProvider.generateToken(user);
        return AuthResponse.bearer(token, tokenProvider.getExpiresInSeconds());
    }
}
