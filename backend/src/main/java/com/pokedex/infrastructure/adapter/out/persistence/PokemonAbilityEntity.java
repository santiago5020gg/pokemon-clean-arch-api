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
 * JPA mapping of the {@code pokemon_ability} child table (US01 skills).
 */
@Entity
@Table(name = "pokemon_ability")
public class PokemonAbilityEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "pokemon_id", nullable = false)
    private PokemonEntity pokemon;

    @Column(nullable = false)
    private String name;

    @Column(name = "is_hidden", nullable = false)
    private boolean hidden;

    protected PokemonAbilityEntity() {
    }

    public PokemonAbilityEntity(PokemonEntity pokemon, String name, boolean hidden) {
        this.pokemon = pokemon;
        this.name = name;
        this.hidden = hidden;
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

    public String getName() {
        return name;
    }

    public boolean isHidden() {
        return hidden;
    }
}
