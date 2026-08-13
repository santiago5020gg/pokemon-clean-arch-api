package com.pokedex.application.config;

import com.pokedex.core.ports.in.PokemonServicePort;
import com.pokedex.core.ports.in.UserServicePort;
import com.pokedex.core.usecase.PokemonService;
import com.pokedex.core.usecase.UserService;
import com.pokedex.infrastructure.adapter.out.persistence.PokemonRepositoryAdapter;
import com.pokedex.infrastructure.adapter.out.persistence.UserRepositoryAdapter;
import com.pokedex.infrastructure.adapter.out.pokeapi.PokeApiClient;
import com.pokedex.infrastructure.adapter.out.security.BcryptPasswordAdapter;
import com.pokedex.infrastructure.adapter.out.security.JwtTokenProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Composition root: wires the framework-free core services to their concrete output-port adapters.
 * The domain services still depend ONLY on the ports — their constructors take the port interfaces,
 * so naming the concrete adapters here just makes the wiring explicit and unambiguous. This is the
 * one place allowed to know both the domain (ports) and the infrastructure (adapters).
 * One cohesive service per aggregate.
 */
@Configuration
public class BeanConfig {

    @Bean
    PokemonServicePort pokemonService(PokeApiClient provider, PokemonRepositoryAdapter repository) {
        return new PokemonService(provider, repository);
    }

    @Bean
    UserServicePort userService(UserRepositoryAdapter userRepository,
                                BcryptPasswordAdapter passwordEncoder,
                                JwtTokenProvider tokenProvider) {
        return new UserService(userRepository, passwordEncoder, tokenProvider);
    }
}
