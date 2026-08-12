# CLAUDE.md

Guidance for Claude Code (and any contributor) working in this repository.

## Project

**pokemon-clean-arch-api** — Pokémon RESTful API for the Ballast Lane technical
interview exercise.

A Java + Spring Boot service that integrates with the public
[PokeAPI](https://pokeapi.co/docs/v2) to **retrieve**, **replicate**, and
**locally modify** Pokémon data. It is backed by a relational database, exposes a
companion user-authentication API, and is consumed by a frontend.

### Design principles

- **Clean Architecture / Hexagonal Architecture** (ports & adapters). The domain
  and business logic must stay independent from the web layer and the persistence
  layer.
- **Test-Driven Development (TDD)** — write the failing test first, then the code.

## Language rule

Everything in this repository is written in **English**: source code, comments,
documentation, commit messages, branch names, and the prompts stored under
`prompts/`.

## Git workflow — Gitflow (MANDATORY)

This repository follows **Gitflow**. Branch structure:

| Branch        | Purpose                                                              |
|---------------|---------------------------------------------------------------------|
| `main`        | Stable/production line. Only receives merges from `dev` when verified. |
| `dev`         | Integration branch. All completed features are merged here first.   |
| `feature/*`   | One branch per change/feature, created from `dev`.                   |

**Rule for EVERY change:**

1. Create a `feature/<short-description>` branch from `dev`.
2. Do the work there, with tests.
3. Merge the feature into `dev` (prefer `--no-ff`) once it is correct.
4. When `dev` is stable and verified, merge `dev` into `main`.

- **Never** commit directly to `main`.
- **Never** commit work-in-progress straight to `dev` without a feature branch.
- Commit messages follow **Conventional Commits** (`feat:`, `fix:`, `docs:`,
  `chore:`, `test:`, `refactor:`).

## Documentation

- `docs/api_doc.md`   — PokeAPI integration reference (endpoints per user story), Markdown.
- `docs/api_doc.html` — same reference, styled and browsable in a browser.
- `prompts/`          — the GenAI prompts used to build this project (English).

## User stories (scope)

- **US01 — Pokemon Enumeration:** paginated list showing sprite, category, mass,
  and skills. Nice-to-have: caching.
- **US02 — Detailed View:** image, core stats, narrative description, and
  evolutionary lineage.
- **US03 — Data Synchronization:** persist Pokémon into a local relational store,
  enabling proprietary fields (localized name, geographical metadata, internal tags).
- **US04 — Local Data Modification:** update local records with robust validation
  (`404` for missing records, `400` for malformed payloads).

## Tech stack

- **Language / framework:** Java 21, Spring Boot 3, Maven.
- **Base package:** `com.jarcila.pokedex`.
- **Web / persistence:** Spring Web, Spring Data JPA, PostgreSQL, Bean Validation.
- **Testing (TDD):** JUnit 5, Mockito, AssertJ, Testcontainers (integration).
- **Caching:** Spring Cache (Caffeine or Redis).
- **Packaging:** Docker + docker-compose, with seed/demo data.
- **Frontend:** React or Vue.

See [`prompts/best_practices.md`](prompts/best_practices.md) for the mandatory
hexagonal package structure and dependency rules.
