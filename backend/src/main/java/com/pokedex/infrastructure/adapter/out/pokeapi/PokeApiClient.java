package com.pokedex.infrastructure.adapter.out.pokeapi;

import com.pokedex.core.domain.Ability;
import com.pokedex.core.domain.Evolution;
import com.pokedex.core.domain.Pokemon;
import com.pokedex.core.domain.Stats;
import com.pokedex.core.ports.out.PokemonProviderPort;
import com.pokedex.infrastructure.adapter.out.pokeapi.PokeApiModels.ChainLink;
import com.pokedex.infrastructure.adapter.out.pokeapi.PokeApiModels.EvolutionChainResponse;
import com.pokedex.infrastructure.adapter.out.pokeapi.PokeApiModels.FlavorTextEntry;
import com.pokedex.infrastructure.adapter.out.pokeapi.PokeApiModels.Genus;
import com.pokedex.infrastructure.adapter.out.pokeapi.PokeApiModels.NamedApiResource;
import com.pokedex.infrastructure.adapter.out.pokeapi.PokeApiModels.PokemonResponse;
import com.pokedex.infrastructure.adapter.out.pokeapi.PokeApiModels.SpeciesResponse;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * Output adapter implementing {@link PokemonProviderPort}. Orchestrates the 3-calls-per-Pokémon
 * flow (detail + species + evolution) and maps the PokeAPI shapes into the {@code Pokemon} domain
 * model. Data gotchas handled here: description cleaning and English-language filtering.
 */
@Component
public class PokeApiClient implements PokemonProviderPort {

    private static final String ENGLISH = "en";

    private final PokeApiHttpClient http;

    public PokeApiClient(PokeApiHttpClient http) {
        this.http = http;
    }

    @Override
    public List<Pokemon> fetchPage(int limit, int offset) {
        var list = http.listPokemon(limit, offset);
        if (list == null || list.results() == null) {
            return List.of();
        }
        List<Pokemon> result = new ArrayList<>();
        for (NamedApiResource ref : list.results()) {
            long id = extractId(ref.url());
            PokemonResponse detail = http.getPokemon(id);
            SpeciesResponse species = http.getSpecies(id);
            EvolutionChainResponse evo = null;
            if (species != null && species.evolutionChain() != null && species.evolutionChain().url() != null) {
                evo = http.getEvolutionChain(extractId(species.evolutionChain().url()));
            }
            result.add(toDomain(detail, species, evo));
        }
        return result;
    }

    static long extractId(String url) {
        if (url == null || url.isBlank()) {
            return -1;
        }
        String trimmed = url.endsWith("/") ? url.substring(0, url.length() - 1) : url;
        String last = trimmed.substring(trimmed.lastIndexOf('/') + 1);
        return Long.parseLong(last);
    }

    static Pokemon toDomain(PokemonResponse detail, SpeciesResponse species, EvolutionChainResponse evo) {
        String spriteUrl = detail.sprites() != null ? detail.sprites().frontDefault() : null;
        String imageUrl = detail.sprites() != null && detail.sprites().other() != null
                && detail.sprites().other().officialArtwork() != null
                ? detail.sprites().other().officialArtwork().frontDefault()
                : null;

        List<Ability> abilities = detail.abilities() == null ? List.of()
                : detail.abilities().stream()
                .map(a -> new Ability(a.ability().name(), a.isHidden()))
                .toList();

        Stats stats = toStats(detail);
        String category = extractCategory(species);
        String description = extractDescription(species);
        List<Evolution> evolutions = flattenEvolution(evo);

        return new Pokemon(
                detail.id(),
                detail.name(),
                spriteUrl,
                imageUrl,
                detail.weight(),
                detail.height(),
                category,
                description,
                stats,
                abilities,
                evolutions,
                null, null, List.of(),
                null, null);
    }

    private static Stats toStats(PokemonResponse detail) {
        int hp = 0, attack = 0, defense = 0, spAtk = 0, spDef = 0, speed = 0;
        if (detail.stats() != null) {
            for (var s : detail.stats()) {
                String name = s.stat() != null ? s.stat().name() : "";
                switch (name) {
                    case "hp" -> hp = s.baseStat();
                    case "attack" -> attack = s.baseStat();
                    case "defense" -> defense = s.baseStat();
                    case "special-attack" -> spAtk = s.baseStat();
                    case "special-defense" -> spDef = s.baseStat();
                    case "speed" -> speed = s.baseStat();
                    default -> {
                    }
                }
            }
        }
        return new Stats(hp, attack, defense, spAtk, spDef, speed);
    }

    private static String extractCategory(SpeciesResponse species) {
        if (species == null || species.genera() == null) {
            return null;
        }
        return species.genera().stream()
                .filter(g -> g.language() != null && ENGLISH.equals(g.language().name()))
                .map(Genus::genus)
                .findFirst()
                .orElse(null);
    }

    private static String extractDescription(SpeciesResponse species) {
        if (species == null || species.flavorTextEntries() == null) {
            return null;
        }
        return species.flavorTextEntries().stream()
                .filter(f -> f.language() != null && ENGLISH.equals(f.language().name()))
                .map(FlavorTextEntry::flavorText)
                .filter(t -> t != null)
                .map(t -> t.replaceAll("[\\n\\f\\r]", " ").replaceAll("\\s+", " ").trim())
                .findFirst()
                .orElse(null);
    }

    static List<Evolution> flattenEvolution(EvolutionChainResponse evo) {
        if (evo == null || evo.chain() == null) {
            return List.of();
        }
        List<Evolution> out = new ArrayList<>();
        walk(evo.chain(), 1, out);
        return out;
    }

    private static void walk(ChainLink link, int stage, List<Evolution> out) {
        if (link == null || link.species() == null) {
            return;
        }
        out.add(new Evolution(link.species().name(), stage));
        if (link.evolvesTo() != null) {
            for (ChainLink next : link.evolvesTo()) {
                walk(next, stage + 1, out);
            }
        }
    }
}
