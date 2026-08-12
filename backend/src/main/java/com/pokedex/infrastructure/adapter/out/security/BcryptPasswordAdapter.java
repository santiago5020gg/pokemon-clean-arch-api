package com.pokedex.infrastructure.adapter.out.security;

import com.pokedex.core.ports.out.PasswordEncoderPort;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

/**
 * Output adapter implementing {@link PasswordEncoderPort} with Spring Security's BCrypt encoder.
 */
@Component
public class BcryptPasswordAdapter implements PasswordEncoderPort {

    private final PasswordEncoder delegate = new BCryptPasswordEncoder();

    @Override
    public String encode(String rawPassword) {
        return delegate.encode(rawPassword);
    }

    @Override
    public boolean matches(String rawPassword, String encodedPassword) {
        return delegate.matches(rawPassword, encodedPassword);
    }
}
