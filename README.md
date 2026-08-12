# pokemon-clean-arch-api

Pokémon RESTful API built with **Java + Spring Boot** for the Ballast Lane
technical interview. It integrates with the public
[PokeAPI](https://pokeapi.co/docs/v2) to **retrieve**, **replicate**, and
**locally modify** Pokémon data, following **Clean / Hexagonal Architecture** and
**TDD**.

## Status

Early stage — requirements analysis and PokeAPI integration documentation.

## Documentation

- [PokeAPI integration reference — Markdown](docs/api_doc.md)
- [PokeAPI integration reference — HTML](docs/api_doc.html)
- [Prompts used to build the project](prompts/)
- [Contributor guide & workflow rules](CLAUDE.md)

## User stories

| Story | Summary |
|-------|---------|
| **US01** | Paginated Pokémon list (sprite, category, mass, skills). Nice-to-have: caching. |
| **US02** | Detailed view (image, core stats, narrative description, evolutionary lineage). |
| **US03** | Data synchronization into a local relational store + proprietary fields. |
| **US04** | Local data modification with `404` / `400` validation. |

## Development workflow

This project uses **Gitflow**: `feature/*` → `dev` → `main`.
Every change starts on a `feature/*` branch cut from `dev`, is merged into `dev`,
and only reaches `main` once verified. See [CLAUDE.md](CLAUDE.md) for the full
rules.

## Tech stack

Java 21 · Spring Boot 3 · Maven · Spring Data JPA · PostgreSQL · Bean Validation ·
JUnit 5 + Mockito + AssertJ · Testcontainers · Spring Cache (Caffeine/Redis) ·
Docker + docker-compose · React/Vue frontend.

Base package: `com.jarcila.pokedex`. Architecture, dependency rules and the full
API contract are defined in
[`prompts/pokedex-api-implementation-prompt.md`](prompts/pokedex-api-implementation-prompt.md).
