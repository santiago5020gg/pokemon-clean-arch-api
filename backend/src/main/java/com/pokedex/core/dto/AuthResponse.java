package com.pokedex.core.dto;

/**
 * Response of a successful login: the JWT plus its type and lifetime in seconds.
 */
public record AuthResponse(String token, String type, long expiresIn) {

    public static AuthResponse bearer(String token, long expiresIn) {
        return new AuthResponse(token, "Bearer", expiresIn);
    }
}
