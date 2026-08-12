package com.pokedex.application.config;

import com.pokedex.core.ports.in.DeletePokemonUseCase;
import com.pokedex.core.ports.in.GetPokemonDetailUseCase;
import com.pokedex.core.ports.in.ListPokemonUseCase;
import com.pokedex.core.ports.in.LoginUseCase;
import com.pokedex.core.ports.in.RegisterUserUseCase;
import com.pokedex.core.ports.in.SyncPokemonUseCase;
import com.pokedex.core.ports.in.UpdatePokemonUseCase;
import com.pokedex.core.ports.out.PasswordEncoderPort;
import com.pokedex.core.ports.out.PokemonProviderPort;
import com.pokedex.core.ports.out.PokemonRepositoryPort;
import com.pokedex.core.ports.out.TokenProviderPort;
import com.pokedex.core.ports.out.UserRepositoryPort;
import com.pokedex.core.usecase.DeletePokemonService;
import com.pokedex.core.usecase.GetPokemonDetailService;
import com.pokedex.core.usecase.ListPokemonService;
import com.pokedex.core.usecase.LoginService;
import com.pokedex.core.usecase.RegisterUserService;
import com.pokedex.core.usecase.SyncPokemonService;
import com.pokedex.core.usecase.UpdatePokemonService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Composition root: wires the framework-free core use cases to their output-port adapters.
 * Keeping this here lets the {@code core} package stay entirely free of Spring annotations
 * (dependency inversion driven from the application layer).
 */
@Configuration
public class BeanConfig {

    @Bean
    ListPokemonUseCase listPokemonUseCase(PokemonRepositoryPort repository) {
        return new ListPokemonService(repository);
    }

    @Bean
    GetPokemonDetailUseCase getPokemonDetailUseCase(PokemonRepositoryPort repository) {
        return new GetPokemonDetailService(repository);
    }

    @Bean
    SyncPokemonUseCase syncPokemonUseCase(PokemonProviderPort provider, PokemonRepositoryPort repository) {
        return new SyncPokemonService(provider, repository);
    }

    @Bean
    UpdatePokemonUseCase updatePokemonUseCase(PokemonRepositoryPort repository) {
        return new UpdatePokemonService(repository);
    }

    @Bean
    DeletePokemonUseCase deletePokemonUseCase(PokemonRepositoryPort repository) {
        return new DeletePokemonService(repository);
    }

    @Bean
    RegisterUserUseCase registerUserUseCase(UserRepositoryPort userRepository, PasswordEncoderPort passwordEncoder) {
        return new RegisterUserService(userRepository, passwordEncoder);
    }

    @Bean
    LoginUseCase loginUseCase(UserRepositoryPort userRepository,
                              PasswordEncoderPort passwordEncoder,
                              TokenProviderPort tokenProvider) {
        return new LoginService(userRepository, passwordEncoder, tokenProvider);
    }
}
