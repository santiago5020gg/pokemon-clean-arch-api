package com.pokedex.application;

import com.pokedex.core.domain.Ability;
import com.pokedex.core.domain.Evolution;
import com.pokedex.core.domain.Pokemon;
import com.pokedex.core.domain.Role;
import com.pokedex.core.domain.Stats;
import com.pokedex.core.domain.User;
import com.pokedex.core.ports.out.PasswordEncoderPort;
import com.pokedex.core.ports.out.PokemonRepositoryPort;
import com.pokedex.core.ports.out.UserRepositoryPort;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Idempotent demo seeder so a fresh database (e.g. {@code docker compose up}) is immediately
 * usable without hitting the PokeAPI: a demo admin plus a few Pokémon. Enabled by default;
 * disable with {@code pokedex.seed.enabled=false}. Only seeds when the stores are empty.
 */
@Component
@ConditionalOnProperty(name = "pokedex.seed.enabled", havingValue = "true", matchIfMissing = true)
public class DataSeeder implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(DataSeeder.class);

    private final PokemonRepositoryPort pokemonRepository;
    private final UserRepositoryPort userRepository;
    private final PasswordEncoderPort passwordEncoder;

    public DataSeeder(PokemonRepositoryPort pokemonRepository,
                      UserRepositoryPort userRepository,
                      PasswordEncoderPort passwordEncoder) {
        this.pokemonRepository = pokemonRepository;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) {
        seedAdmin();
        seedPokemon();
    }

    private void seedAdmin() {
        if (userRepository.existsByUsername("admin")) {
            return;
        }
        userRepository.save(new User(
                null, "admin", "admin@pokedex.io",
                passwordEncoder.encode("admin123"), Role.ADMIN, null));
        log.info("Seeded demo admin user (admin / admin123)");
    }

    private void seedPokemon() {
        if (!pokemonRepository.findAll(0, 1).content().isEmpty()) {
            return;
        }
        demoPokemon().forEach(pokemonRepository::save);
        log.info("Seeded {} demo Pokemon", demoPokemon().size());
    }

    private List<Pokemon> demoPokemon() {
        return List.of(
                new Pokemon(1L, "bulbasaur",
                        "https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/1.png",
                        "https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/other/official-artwork/1.png",
                        69, 7, "Seed Pokémon",
                        "A strange seed was planted on its back at birth.",
                        new Stats(45, 49, 49, 65, 65, 45),
                        List.of(new Ability("overgrow", false), new Ability("chlorophyll", true)),
                        List.of(new Evolution("bulbasaur", 1), new Evolution("ivysaur", 2), new Evolution("venusaur", 3)),
                        "Bulbasaur", "Kanto", List.of("starter", "grass"), null, null),
                new Pokemon(4L, "charmander",
                        "https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/4.png",
                        "https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/other/official-artwork/4.png",
                        85, 6, "Lizard Pokémon",
                        "It has a preference for hot things.",
                        new Stats(39, 52, 43, 60, 50, 65),
                        List.of(new Ability("blaze", false), new Ability("solar-power", true)),
                        List.of(new Evolution("charmander", 1), new Evolution("charmeleon", 2), new Evolution("charizard", 3)),
                        "Charmander", "Kanto", List.of("starter", "fire"), null, null),
                new Pokemon(7L, "squirtle",
                        "https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/7.png",
                        "https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/other/official-artwork/7.png",
                        90, 5, "Tiny Turtle Pokémon",
                        "It shelters itself in its shell then strikes back.",
                        new Stats(44, 48, 65, 50, 64, 43),
                        List.of(new Ability("torrent", false), new Ability("rain-dish", true)),
                        List.of(new Evolution("squirtle", 1), new Evolution("wartortle", 2), new Evolution("blastoise", 3)),
                        "Squirtle", "Kanto", List.of("starter", "water"), null, null));
    }
}
