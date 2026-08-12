package com.pokedex.infrastructure.adapter.out.persistence;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OrderBy;
import jakarta.persistence.Table;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

/**
 * JPA mapping of the {@code pokemon} table. Lives only in the persistence adapter — never leaks
 * into the core, which speaks in the {@code Pokemon} domain model.
 */
@Entity
@Table(name = "pokemon")
public class PokemonEntity {

    @Id
    private Long id;

    @Column(nullable = false, unique = true)
    private String name;

    private String spriteUrl;
    private String imageUrl;
    private Integer weight;
    private Integer height;
    private String category;
    @Column(columnDefinition = "text")
    private String description;

    private Integer hp;
    private Integer attack;
    private Integer defense;
    private Integer specialAttack;
    private Integer specialDefense;
    private Integer speed;

    private String localizedName;
    private String region;
    private String internalTags;

    private Instant createdAt;
    private Instant updatedAt;

    @OneToMany(mappedBy = "pokemon", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    private List<PokemonAbilityEntity> abilities = new ArrayList<>();

    @OneToMany(mappedBy = "pokemon", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    @OrderBy("stage ASC")
    private List<PokemonEvolutionEntity> evolutions = new ArrayList<>();

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSpriteUrl() {
        return spriteUrl;
    }

    public void setSpriteUrl(String spriteUrl) {
        this.spriteUrl = spriteUrl;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public Integer getWeight() {
        return weight;
    }

    public void setWeight(Integer weight) {
        this.weight = weight;
    }

    public Integer getHeight() {
        return height;
    }

    public void setHeight(Integer height) {
        this.height = height;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Integer getHp() {
        return hp;
    }

    public void setHp(Integer hp) {
        this.hp = hp;
    }

    public Integer getAttack() {
        return attack;
    }

    public void setAttack(Integer attack) {
        this.attack = attack;
    }

    public Integer getDefense() {
        return defense;
    }

    public void setDefense(Integer defense) {
        this.defense = defense;
    }

    public Integer getSpecialAttack() {
        return specialAttack;
    }

    public void setSpecialAttack(Integer specialAttack) {
        this.specialAttack = specialAttack;
    }

    public Integer getSpecialDefense() {
        return specialDefense;
    }

    public void setSpecialDefense(Integer specialDefense) {
        this.specialDefense = specialDefense;
    }

    public Integer getSpeed() {
        return speed;
    }

    public void setSpeed(Integer speed) {
        this.speed = speed;
    }

    public String getLocalizedName() {
        return localizedName;
    }

    public void setLocalizedName(String localizedName) {
        this.localizedName = localizedName;
    }

    public String getRegion() {
        return region;
    }

    public void setRegion(String region) {
        this.region = region;
    }

    public String getInternalTags() {
        return internalTags;
    }

    public void setInternalTags(String internalTags) {
        this.internalTags = internalTags;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Instant updatedAt) {
        this.updatedAt = updatedAt;
    }

    public List<PokemonAbilityEntity> getAbilities() {
        return abilities;
    }

    public void setAbilities(List<PokemonAbilityEntity> abilities) {
        this.abilities = abilities;
    }

    public List<PokemonEvolutionEntity> getEvolutions() {
        return evolutions;
    }

    public void setEvolutions(List<PokemonEvolutionEntity> evolutions) {
        this.evolutions = evolutions;
    }
}
