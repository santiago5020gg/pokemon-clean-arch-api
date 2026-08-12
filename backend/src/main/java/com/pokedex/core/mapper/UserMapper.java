package com.pokedex.core.mapper;

import com.pokedex.core.domain.User;
import com.pokedex.core.dto.UserDto;

/**
 * Pure converter between the {@link User} domain model and its safe DTO (no password hash).
 */
public final class UserMapper {

    private UserMapper() {
    }

    public static UserDto toDto(User user) {
        return new UserDto(
                user.id(),
                user.username(),
                user.email(),
                user.role().name());
    }
}
