package com.pokedex.core.exception;

/**
 * Thrown by the core when a requested resource does not exist. Mapped to HTTP 404 by the
 * infrastructure exception handler.
 */
public class ResourceNotFoundException extends RuntimeException {

    public ResourceNotFoundException(String message) {
        super(message);
    }

    public static ResourceNotFoundException pokemon(Long id) {
        return new ResourceNotFoundException("Pokemon not found: " + id);
    }
}
