package com.pokedex.core.ports.out;

import com.pokedex.core.domain.User;

import java.util.Optional;

/**
 * Output port for issuing and validating authentication tokens (JWT), keeping the core free of
 * any concrete token library.
 */
public interface TokenProviderPort {

    String generateToken(User user);

    long getExpiresInSeconds();

    /**
     * @return the subject (username) if the token is valid, otherwise empty.
     */
    Optional<String> validateAndGetUsername(String token);
}
