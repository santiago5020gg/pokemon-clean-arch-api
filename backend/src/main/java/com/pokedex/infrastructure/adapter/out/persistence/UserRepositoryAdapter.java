package com.pokedex.infrastructure.adapter.out.persistence;

import com.pokedex.core.domain.Role;
import com.pokedex.core.domain.User;
import com.pokedex.core.ports.out.UserRepositoryPort;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.Optional;

/**
 * Output adapter implementing {@link UserRepositoryPort} over Spring Data JPA.
 */
@Component
public class UserRepositoryAdapter implements UserRepositoryPort {

    private final UserJpaRepository jpa;

    public UserRepositoryAdapter(UserJpaRepository jpa) {
        this.jpa = jpa;
    }

    @Override
    public Optional<User> findByUsername(String username) {
        return jpa.findByUsername(username).map(this::toDomain);
    }

    @Override
    public boolean existsByUsername(String username) {
        return jpa.existsByUsername(username);
    }

    @Override
    public boolean existsByEmail(String email) {
        return jpa.existsByEmail(email);
    }

    @Override
    public User save(User user) {
        UserEntity entity = new UserEntity(
                user.id(),
                user.username(),
                user.email(),
                user.passwordHash(),
                user.role().name(),
                user.createdAt() != null ? user.createdAt() : Instant.now());
        return toDomain(jpa.save(entity));
    }

    private User toDomain(UserEntity e) {
        return new User(
                e.getId(),
                e.getUsername(),
                e.getEmail(),
                e.getPasswordHash(),
                Role.valueOf(e.getRole()),
                e.getCreatedAt());
    }
}
