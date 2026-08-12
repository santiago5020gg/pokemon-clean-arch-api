package com.pokedex.infrastructure.adapter.in;

import com.pokedex.core.dto.AuthResponse;
import com.pokedex.core.dto.LoginRequest;
import com.pokedex.core.dto.RegisterRequest;
import com.pokedex.core.dto.UserDto;
import com.pokedex.core.ports.in.LoginUseCase;
import com.pokedex.core.ports.in.RegisterUserUseCase;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

/**
 * REST input adapter for the auxiliary user/auth API.
 */
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final RegisterUserUseCase registerUser;
    private final LoginUseCase login;

    public AuthController(RegisterUserUseCase registerUser, LoginUseCase login) {
        this.registerUser = registerUser;
        this.login = login;
    }

    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    public UserDto register(@Valid @RequestBody RegisterRequest request) {
        return registerUser.register(request);
    }

    @PostMapping("/login")
    public AuthResponse login(@Valid @RequestBody LoginRequest request) {
        return login.login(request);
    }
}
