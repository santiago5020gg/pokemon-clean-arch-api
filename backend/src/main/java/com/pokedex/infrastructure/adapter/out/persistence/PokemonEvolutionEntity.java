package com.pokedex.infrastructure.adapter.out.persistence;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

/**
 * JPA mapping of the {@code pokemon_evolution} child table (US02 evolutionary lineage).
 */
@Entity
@Table(name = "pokemon_evolution")
public class PokemonEvolutionEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "pokemon_id", nullable = false)
    private PokemonEntity pokemon;

    @Column(name = "species_name", nullable = false)
    private String speciesName;

    @Column(nullable = false)
    private int stage;

    protected PokemonEvolutionEntity() {
    }

    public PokemonEvolutionEntity(PokemonEntity pokemon, String speciesName, int stage) {
        this.pokemon = pokemon;
        this.speciesName = speciesName;
        this.stage = stage;
    }

    public Long getId() {
        return id;
    }

    public PokemonEntity getPokemon() {
        return pokemon;
    }

    public void setPokemon(PokemonEntity pokemon) {
        this.pokemon = pokemon;
    }

    public String getSpeciesName() {
        return speciesName;
    }

    public int getStage() {
        return stage;
    }
}
