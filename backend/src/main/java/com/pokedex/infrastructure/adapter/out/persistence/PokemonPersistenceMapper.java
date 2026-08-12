package com.pokedex.infrastructure.adapter.out.persistence;

import com.pokedex.core.domain.Ability;
import com.pokedex.core.domain.Evolution;
import com.pokedex.core.domain.Pokemon;
import com.pokedex.core.domain.Stats;

import java.util.Arrays;
import java.util.List;

/**
 * Converts between the {@link Pokemon} domain model and {@link PokemonEntity}. Kept inside the
 * persistence adapter so JPA types never leak into the core. Handles the CSV encoding of
 * {@code internalTags}.
 */
final class PokemonPersistenceMapper {

    private PokemonPersistenceMapper() {
    }

    static Pokemon toDomain(PokemonEntity e) {
        return new Pokemon(
                e.getId(),
                e.getName(),
                e.getSpriteUrl(),
                e.getImageUrl(),
                e.getWeight(),
                e.getHeight(),
                e.getCategory(),
                e.getDescription(),
                new Stats(
                        nz(e.getHp()), nz(e.getAttack()), nz(e.getDefense()),
                        nz(e.getSpecialAttack()), nz(e.getSpecialDefense()), nz(e.getSpeed())),
                e.getAbilities().stream()
                        .map(a -> new Ability(a.getName(), a.isHidden()))
                        .toList(),
                e.getEvolutions().stream()
                        .map(ev -> new Evolution(ev.getSpeciesName(), ev.getStage()))
                        .toList(),
                e.getLocalizedName(),
                e.getRegion(),
                fromCsv(e.getInternalTags()),
                e.getCreatedAt(),
                e.getUpdatedAt());
    }

    /**
     * Applies domain data onto a (possibly existing) entity, rebuilding the child collections.
     */
    static PokemonEntity applyToEntity(Pokemon p, PokemonEntity target) {
        target.setId(p.id());
        target.setName(p.name());
        target.setSpriteUrl(p.spriteUrl());
        target.setImageUrl(p.imageUrl());
        target.setWeight(p.weight());
        target.setHeight(p.height());
        target.setCategory(p.category());
        target.setDescription(p.description());
        if (p.stats() != null) {
            target.setHp(p.stats().hp());
            target.setAttack(p.stats().attack());
            target.setDefense(p.stats().defense());
            target.setSpecialAttack(p.stats().specialAttack());
            target.setSpecialDefense(p.stats().specialDefense());
            target.setSpeed(p.stats().speed());
        }
        target.setLocalizedName(p.localizedName());
        target.setRegion(p.region());
        target.setInternalTags(toCsv(p.internalTags()));

        target.getAbilities().clear();
        for (Ability a : p.abilities()) {
            target.getAbilities().add(new PokemonAbilityEntity(target, a.name(), a.hidden()));
        }
        target.getEvolutions().clear();
        for (Evolution ev : p.evolutions()) {
            target.getEvolutions().add(new PokemonEvolutionEntity(target, ev.speciesName(), ev.stage()));
        }
        return target;
    }

    private static int nz(Integer v) {
        return v == null ? 0 : v;
    }

    static String toCsv(List<String> tags) {
        if (tags == null || tags.isEmpty()) {
            return null;
        }
        return String.join(",", tags);
    }

    static List<String> fromCsv(String csv) {
        if (csv == null || csv.isBlank()) {
            return List.of();
        }
        return Arrays.stream(csv.split(","))
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .toList();
    }
}
