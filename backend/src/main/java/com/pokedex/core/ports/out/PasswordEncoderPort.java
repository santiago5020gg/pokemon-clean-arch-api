package com.pokedex.core.ports.out;

/**
 * Output port for password hashing, keeping the core free of any concrete crypto dependency.
 */
public interface PasswordEncoderPort {

    String encode(String rawPassword);

    boolean matches(String rawPassword, String encodedPassword);
}
