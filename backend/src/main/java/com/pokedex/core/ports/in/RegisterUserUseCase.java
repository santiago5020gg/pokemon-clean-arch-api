package com.pokedex.core.ports.in;

import com.pokedex.core.dto.RegisterRequest;
import com.pokedex.core.dto.UserDto;

/**
 * Input port — auth: register a new user account.
 */
public interface RegisterUserUseCase {

    UserDto register(RegisterRequest request);
}
