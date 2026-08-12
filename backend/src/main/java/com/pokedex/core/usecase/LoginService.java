package com.pokedex.core.usecase;

import com.pokedex.core.domain.User;
import com.pokedex.core.dto.AuthResponse;
import com.pokedex.core.dto.LoginRequest;
import com.pokedex.core.exception.InvalidCredentialsException;
import com.pokedex.core.ports.in.LoginUseCase;
import com.pokedex.core.ports.out.PasswordEncoderPort;
import com.pokedex.core.ports.out.TokenProviderPort;
import com.pokedex.core.ports.out.UserRepositoryPort;

/**
 * Auth use case: authenticate credentials and issue a JWT. Returns a uniform 401 for both an
 * unknown username and a wrong password (no user-enumeration leak).
 */
public class LoginService implements LoginUseCase {

    private final UserRepositoryPort userRepository;
    private final PasswordEncoderPort passwordEncoder;
    private final TokenProviderPort tokenProvider;

    public LoginService(UserRepositoryPort userRepository,
                        PasswordEncoderPort passwordEncoder,
                        TokenProviderPort tokenProvider) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.tokenProvider = tokenProvider;
    }

    @Override
    public AuthResponse login(LoginRequest request) {
        User user = userRepository.findByUsername(request.username())
                .orElseThrow(() -> new InvalidCredentialsException("Invalid username or password"));

        if (!passwordEncoder.matches(request.password(), user.passwordHash())) {
            throw new InvalidCredentialsException("Invalid username or password");
        }

        String token = tokenProvider.generateToken(user);
        return AuthResponse.bearer(token, tokenProvider.getExpiresInSeconds());
    }
}
