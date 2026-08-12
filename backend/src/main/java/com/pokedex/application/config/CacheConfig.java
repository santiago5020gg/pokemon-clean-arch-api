package com.pokedex.application.config;

import com.github.benmanes.caffeine.cache.Caffeine;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.caffeine.CaffeineCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;

/**
 * Caffeine-backed cache for PokeAPI reads (US01 N+1 mitigation). Entries expire after a while so
 * a re-sync eventually refreshes upstream data. {@code @EnableCaching} lives here (not on the main
 * class) so web-slice tests without a CacheManager are unaffected.
 */
@Configuration
@EnableCaching
public class CacheConfig {

    @Bean
    CacheManager cacheManager() {
        CaffeineCacheManager manager = new CaffeineCacheManager(
                "pokeapi-list", "pokeapi-pokemon", "pokeapi-species", "pokeapi-evolution");
        manager.setCaffeine(Caffeine.newBuilder()
                .maximumSize(2_000)
                .expireAfterWrite(Duration.ofHours(24)));
        return manager;
    }
}
