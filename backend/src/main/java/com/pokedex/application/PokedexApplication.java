package com.pokedex.application;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.boot.persistence.autoconfigure.EntityScan;

/**
 * Spring Boot entry point. Lives in {@code application} per the hexagonal layout, so component,
 * entity and repository scanning are widened explicitly to the whole {@code com.pokedex} base.
 */
@SpringBootApplication
@EnableCaching
@ComponentScan(basePackages = "com.pokedex")
@EntityScan(basePackages = "com.pokedex.infrastructure.adapter.out.persistence")
@EnableJpaRepositories(basePackages = "com.pokedex.infrastructure.adapter.out.persistence")
public class PokedexApplication {

    public static void main(String[] args) {
        SpringApplication.run(PokedexApplication.class, args);
    }
}
