package com.pokedex.application.config;

import com.pokedex.core.ports.in.PokemonServicePort;
import com.pokedex.core.ports.in.UserServicePort;
import com.pokedex.core.ports.out.PasswordEncoderPort;
import com.pokedex.core.ports.out.PokemonProviderPort;
import com.pokedex.core.ports.out.PokemonRepositoryPort;
import com.pokedex.core.ports.out.TokenProviderPort;
import com.pokedex.core.ports.out.UserRepositoryPort;
import com.pokedex.core.usecase.PokemonService;
import com.pokedex.core.usecase.UserService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Composition root: wires the framework-free core services to their output-port adapters.
 * Keeping this here lets the {@code core} package stay entirely free of Spring annotations
 * (dependency inversion driven from the application layer). One cohesive service per aggregate.
 */
@Configuration
public class BeanConfig {

    @Bean
    PokemonServicePort pokemonService(PokemonProviderPort provider, PokemonRepositoryPort repository) {
        return new PokemonService(provider, repository);
    }

    @Bean
    UserServicePort userService(UserRepositoryPort userRepository,
                                PasswordEncoderPort passwordEncoder,
                                TokenProviderPort tokenProvider) {
        return new UserService(userRepository, passwordEncoder, tokenProvider);
    }
}
