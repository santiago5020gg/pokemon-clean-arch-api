package com.pokedex.infrastructure.adapter.in;

import java.time.Instant;
import java.util.List;

/**
 * Consistent error body returned by {@link GlobalExceptionHandler}: never leaks stack traces or
 * infrastructure details. {@code fieldErrors} is populated only for validation failures.
 */
public record ErrorResponse(
        Instant timestamp,
        int status,
        String error,
        String message,
        String path,
        List<FieldError> fieldErrors) {

    public record FieldError(String field, String message) {
    }

    public static ErrorResponse of(int status, String error, String message, String path) {
        return new ErrorResponse(Instant.now(), status, error, message, path, null);
    }

    public static ErrorResponse of(int status, String error, String message, String path,
                                   List<FieldError> fieldErrors) {
        return new ErrorResponse(Instant.now(), status, error, message, path, fieldErrors);
    }
}
