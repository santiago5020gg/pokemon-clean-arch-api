package com.pokedex.core.ports.out;

import com.pokedex.core.domain.User;

import java.util.Optional;

/**
 * Output port to the local user store. Speaks only in domain models.
 */
public interface UserRepositoryPort {

    Optional<User> findByUsername(String username);

    boolean existsByUsername(String username);

    boolean existsByEmail(String email);

    User save(User user);
}
