package com.pokedex.core.ports.in;

import com.pokedex.core.dto.AuthResponse;
import com.pokedex.core.dto.LoginRequest;

/**
 * Input port — auth: authenticate and issue a JWT.
 */
public interface LoginUseCase {

    AuthResponse login(LoginRequest request);
}
