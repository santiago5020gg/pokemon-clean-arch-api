package com.pokedex.infrastructure.adapter.out.security;

import com.pokedex.core.domain.User;
import com.pokedex.core.ports.out.TokenProviderPort;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Date;
import java.util.Optional;

/**
 * Output adapter implementing {@link TokenProviderPort} with JJWT (HS256). The signing secret and
 * lifetime are externalized via configuration.
 */
@Component
public class JwtTokenProvider implements TokenProviderPort {

    private final SecretKey key;
    private final long expiresInSeconds;

    public JwtTokenProvider(
            @Value("${security.jwt.secret:change-me-change-me-change-me-change-me-1234}") String secret,
            @Value("${security.jwt.expires-in-seconds:3600}") long expiresInSeconds) {
        this.key = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
        this.expiresInSeconds = expiresInSeconds;
    }

    @Override
    public String generateToken(User user) {
        Instant now = Instant.now();
        return Jwts.builder()
                .subject(user.username())
                .claim("role", user.role().name())
                .issuedAt(Date.from(now))
                .expiration(Date.from(now.plusSeconds(expiresInSeconds)))
                .signWith(key)
                .compact();
    }

    @Override
    public long getExpiresInSeconds() {
        return expiresInSeconds;
    }

    @Override
    public Optional<String> validateAndGetUsername(String token) {
        try {
            Claims claims = Jwts.parser()
                    .verifyWith(key)
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();
            return Optional.ofNullable(claims.getSubject());
        } catch (Exception ex) {
            return Optional.empty();
        }
    }
}
