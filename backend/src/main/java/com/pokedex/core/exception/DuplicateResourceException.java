package com.pokedex.core.exception;

/**
 * Thrown when creating a resource that violates a uniqueness constraint (e.g. duplicate username
 * or email). Mapped to HTTP 409 by the infrastructure exception handler.
 */
public class DuplicateResourceException extends RuntimeException {

    public DuplicateResourceException(String message) {
        super(message);
    }
}
