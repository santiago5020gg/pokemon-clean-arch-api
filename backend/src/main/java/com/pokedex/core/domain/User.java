package com.pokedex.core.domain;

import java.time.Instant;

/**
 * Core user domain model for the auxiliary authentication API. Holds the BCrypt password hash
 * (never a plain password) and an authorization {@link Role}.
 */
public record User(
        Long id,
        String username,
        String email,
        String passwordHash,
        Role role,
        Instant createdAt) {
}
