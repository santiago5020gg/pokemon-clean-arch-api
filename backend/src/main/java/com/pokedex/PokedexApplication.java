package com.pokedex;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Spring Boot entry point. Placed at the base package {@code com.pokedex} so component, entity and
 * repository scanning cover the whole hexagonal tree (application, core wiring, infrastructure
 * adapters) and Spring Boot test slices can discover the configuration. Application-layer
 * configuration classes live under {@code com.pokedex.application.config}.
 */
@SpringBootApplication
public class PokedexApplication {

    public static void main(String[] args) {
        SpringApplication.run(PokedexApplication.class, args);
    }
}
