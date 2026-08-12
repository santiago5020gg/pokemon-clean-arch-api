package com.pokedex.application;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.testcontainers.junit.jupiter.Testcontainers;

/**
 * Full-context integration test. Boots the whole application (Flyway migration + JPA validation +
 * security) against a real PostgreSQL. Skipped automatically when Docker is not available so the
 * build stays green on machines without a Docker daemon.
 */
@Testcontainers(disabledWithoutDocker = true)
@Import(TestcontainersConfiguration.class)
@SpringBootTest
class PokedexApplicationIT {

    @Test
    void contextLoads() {
    }
}
