-- V2__seed_demo_data.sql — demo/seed data so a fresh database is immediately usable.
-- Runs via Flyway after V1 (schema), on any fresh DB (java -jar, docker compose, Testcontainers).
-- Replaces the former Java DataSeeder. The admin password_hash is a BCrypt hash of "admin123".

-- Demo admin (admin / admin123)
INSERT INTO users (username, email, password_hash, role, created_at)
VALUES ('admin', 'admin@pokedex.io',
        '$2a$10$kg157P2byDB8B62qUcZdNuQmBatCGGR0cZNmX1cXSUimMdAfXSAvS', 'ADMIN', now())
ON CONFLICT (username) DO NOTHING;

-- Demo Pokemon (the 3 Kanto starters), with proprietary fields (US03)
INSERT INTO pokemon (id, name, sprite_url, image_url, weight, height, category, description,
                     hp, attack, defense, special_attack, special_defense, speed,
                     localized_name, region, internal_tags, created_at)
VALUES
 (1, 'bulbasaur',
  'https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/1.png',
  'https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/other/official-artwork/1.png',
  69, 7, 'Seed Pokémon', 'A strange seed was planted on its back at birth.',
  45, 49, 49, 65, 65, 45, 'Bulbasaur', 'Kanto', 'starter,grass', now()),
 (4, 'charmander',
  'https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/4.png',
  'https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/other/official-artwork/4.png',
  85, 6, 'Lizard Pokémon', 'It has a preference for hot things.',
  39, 52, 43, 60, 50, 65, 'Charmander', 'Kanto', 'starter,fire', now()),
 (7, 'squirtle',
  'https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/7.png',
  'https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/other/official-artwork/7.png',
  90, 5, 'Tiny Turtle Pokémon', 'It shelters itself in its shell then strikes back.',
  44, 48, 65, 50, 64, 43, 'Squirtle', 'Kanto', 'starter,water', now())
ON CONFLICT (id) DO NOTHING;

-- Abilities (US01 "skills")
INSERT INTO pokemon_ability (pokemon_id, name, is_hidden) VALUES
 (1, 'overgrow', FALSE), (1, 'chlorophyll', TRUE),
 (4, 'blaze', FALSE),    (4, 'solar-power', TRUE),
 (7, 'torrent', FALSE),  (7, 'rain-dish', TRUE);

-- Evolutions (US02 "evolutionary lineage", ordered by stage)
INSERT INTO pokemon_evolution (pokemon_id, species_name, stage) VALUES
 (1, 'bulbasaur', 1), (1, 'ivysaur', 2),   (1, 'venusaur', 3),
 (4, 'charmander', 1),(4, 'charmeleon', 2),(4, 'charizard', 3),
 (7, 'squirtle', 1),  (7, 'wartortle', 2), (7, 'blastoise', 3);
