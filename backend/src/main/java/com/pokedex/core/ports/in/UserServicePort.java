package com.pokedex.core.ports.in;

import com.pokedex.core.dto.AuthResponse;
import com.pokedex.core.dto.LoginRequest;
import com.pokedex.core.dto.RegisterRequest;
import com.pokedex.core.dto.UserDto;

/**
 * Input port for the {@code user} aggregate. Groups the auth operations (register, login) behind
 * one cohesive interface so the core exposes a single service per aggregate.
 */
public interface UserServicePort {

    UserDto register(RegisterRequest request);

    AuthResponse login(LoginRequest request);
}
