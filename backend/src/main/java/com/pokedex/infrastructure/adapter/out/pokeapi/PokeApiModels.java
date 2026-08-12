package com.pokedex.infrastructure.adapter.out.pokeapi;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

/**
 * PokeAPI JSON response models. All package-private so these external shapes never leak into the
 * core — the {@link PokeApiClient} maps them into the {@code Pokemon} domain model. Snake_case
 * fields are bound with explicit {@code @JsonProperty} so no custom ObjectMapper is required.
 */
final class PokeApiModels {

    private PokeApiModels() {
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    record NamedApiResource(String name, String url) {
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    record ListResponse(int count, List<NamedApiResource> results) {
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    record AbilityEntry(NamedApiResource ability, @JsonProperty("is_hidden") boolean isHidden) {
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    record StatEntry(@JsonProperty("base_stat") int baseStat, NamedApiResource stat) {
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    record OfficialArtwork(@JsonProperty("front_default") String frontDefault) {
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    record OtherSprites(@JsonProperty("official-artwork") OfficialArtwork officialArtwork) {
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    record Sprites(@JsonProperty("front_default") String frontDefault, OtherSprites other) {
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    record PokemonResponse(
            long id,
            String name,
            int weight,
            int height,
            List<AbilityEntry> abilities,
            Sprites sprites,
            List<StatEntry> stats,
            NamedApiResource species) {
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    record Genus(String genus, NamedApiResource language) {
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    record FlavorTextEntry(@JsonProperty("flavor_text") String flavorText, NamedApiResource language) {
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    record EvolutionChainRef(String url) {
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    record SpeciesResponse(
            List<Genus> genera,
            @JsonProperty("flavor_text_entries") List<FlavorTextEntry> flavorTextEntries,
            @JsonProperty("evolution_chain") EvolutionChainRef evolutionChain) {
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    record ChainLink(NamedApiResource species, @JsonProperty("evolves_to") List<ChainLink> evolvesTo) {
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    record EvolutionChainResponse(ChainLink chain) {
    }
}
