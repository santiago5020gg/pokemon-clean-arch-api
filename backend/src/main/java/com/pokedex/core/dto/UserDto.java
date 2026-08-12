package com.pokedex.core.dto;

/**
 * Safe user projection returned by registration (the password hash is never exposed).
 */
public record UserDto(Long id, String username, String email, String role) {
}
