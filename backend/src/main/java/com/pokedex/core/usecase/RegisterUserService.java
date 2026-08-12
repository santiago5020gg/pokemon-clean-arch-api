package com.pokedex.core.usecase;

import com.pokedex.core.domain.Role;
import com.pokedex.core.domain.User;
import com.pokedex.core.dto.RegisterRequest;
import com.pokedex.core.dto.UserDto;
import com.pokedex.core.exception.DuplicateResourceException;
import com.pokedex.core.mapper.UserMapper;
import com.pokedex.core.ports.in.RegisterUserUseCase;
import com.pokedex.core.ports.out.PasswordEncoderPort;
import com.pokedex.core.ports.out.UserRepositoryPort;

/**
 * Auth use case: register a new account. Enforces uniqueness (409) and never stores a plain
 * password — hashing is delegated to the {@link PasswordEncoderPort} (dependency inversion).
 */
public class RegisterUserService implements RegisterUserUseCase {

    private final UserRepositoryPort userRepository;
    private final PasswordEncoderPort passwordEncoder;

    public RegisterUserService(UserRepositoryPort userRepository, PasswordEncoderPort passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
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
}
