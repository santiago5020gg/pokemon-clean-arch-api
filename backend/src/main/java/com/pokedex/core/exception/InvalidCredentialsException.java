package com.pokedex.core.exception;

/**
 * Thrown on failed authentication. Mapped to HTTP 401 by the infrastructure exception handler.
 */
public class InvalidCredentialsException extends RuntimeException {

    public InvalidCredentialsException(String message) {
        super(message);
    }
}
