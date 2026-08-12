# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project

**pokemon-clean-arch-api** — Pokémon RESTful API for the Ballast Lane technical
interview exercise.

A Java + Spring Boot service that integrates with the public
[PokeAPI](https://pokeapi.co/docs/v2) to **retrieve**, **replicate**, and
**locally modify** Pokémon data. It is backed by a relational database, exposes a
companion user-authentication API, and is consumed by a frontend.

## Current state

**Documentation phase — the code is not scaffolded yet.** The repository currently
contains the requirements analysis, the PokeAPI integration reference and the
implementation prompts. There is no `pom.xml` or `src/` tree yet; the Maven
commands below apply once the Spring Boot project is generated.

## Repository layout

- `docs/api_doc.md` / `docs/api_doc.html` — the single source of truth for the API
  contract: PokeAPI endpoints per user story, our own CRUD, the auth API, the
  database entities and the architecture-layer mapping. **Read this before writing
  any endpoint or entity.** The two files are mirrors (Markdown + browsable HTML).
- `prompts/` — the GenAI prompts used to build the project (English). The master
  prompt `prompts/pokedex-api-implementation-prompt.md` is the authoritative
  specification of role, hexagonal structure, TDD flow and the API contract —
  follow it when implementing.
- `README.md` — public overview.

The exercise PDF is intentionally git-ignored (it is Ballast Lane's document).

## Architecture (hexagonal / ports & adapters)

The mandatory package structure and dependency rules live in
`prompts/pokedex-api-implementation-prompt.md`. The essentials:

- Base package `com.jarcila.pokedex`.
- `core/` (domain, dto, mapper, ports, usecase) is the business core. It **must not
  depend on Spring, JPA, or `infrastructure`** — only on interfaces (ports).
  Dependencies always point inward (dependency inversion). This independence is
  what makes the core unit-testable without a Spring context, and it is the single
  most heavily-evaluated aspect of the exercise.
- `infrastructure/adapter/in` — REST controllers + `@RestControllerAdvice`.
- `infrastructure/adapter/out` — JPA repositories and the PokeAPI HTTP client.
- `application/` — Spring Boot entry point and configuration/beans.

### How the requirement layers map

| Requirement (statement) | Hexagonal layer |
|-------------------------|-----------------|
| Core Business Logic | `core` |
| Data Layer | `infrastructure/adapter/out` |
| API | `infrastructure/adapter/in` |
| Testing | `test/` mirrors `main`: unit tests in `core` (no Spring), integration in adapters |

## API contract (summary — full spec in `docs/api_doc.md`)

- **PokeAPI (external, read-only, no key):** each full Pokémon needs 3 calls —
  `GET /pokemon/{id}` (sprite, weight, abilities, stats), `GET /pokemon-species/{id}`
  (category via `genera`, description via `flavor_text`), `GET /evolution-chain/{id}`
  (recursive lineage). The list `GET /pokemon?limit=&offset=` only returns `name`+`url`
  → N+1 pattern → justifies the caching layer.
- **Our resource `pokemon`:** `POST /api/pokemon/sync` (replicate — US03),
  `GET /api/pokemon` + `GET /api/pokemon/{id}` (US01/US02), `PUT /api/pokemon/{id}`
  (US04, `404`/`400`), `DELETE /api/pokemon/{id}`.
- **Auth API `users`:** `POST /api/auth/register`, `POST /api/auth/login` (JWT).
  Read routes are public; write routes are protected (Bearer token).
- Replicated records carry proprietary fields (`localizedName`, `region`,
  `internalTags`) that the PokeAPI does not have — this is the point of US03 and
  what US04 edits.

Data gotchas: `weight` is in hectograms (÷10 = kg); `flavor_text` contains `\n`/`\f`
that must be stripped; `genera`/`flavor_text_entries` are per-language lists (filter
by `language.name`).

## Development commands (once scaffolded — Maven)

```bash
mvn clean verify          # compile + run all tests
mvn test                  # unit tests only
mvn test -Dtest=ClassName#method   # a single test
mvn spring-boot:run       # run the app locally
docker compose up         # run app + PostgreSQL (with seed data)
```

## Git workflow — Gitflow (MANDATORY)

| Branch | Purpose |
|--------|---------|
| `main` | Stable line. Only receives merges from `dev` once verified. |
| `dev`  | Integration branch (repo default). All completed features merge here first. |
| `feature/*` | One branch per change, cut from `dev`. |

For **every** change: create `feature/<desc>` from `dev` → work with tests →
merge into `dev` with `--no-ff` → merge `dev` into `main` only when verified.
Never commit directly to `main`; never skip the feature branch.

Commit messages follow **Conventional Commits** (`feat:`, `fix:`, `docs:`,
`chore:`, `test:`, `refactor:`). On Windows PowerShell, pass multi-line commit
messages via `git commit -F <file>` (inline here-strings with parentheses break
the parser).

## Language rule

Everything in this repository is written in **English**: source code, comments,
documentation, commit messages, branch names, and the prompts under `prompts/`.

## Development methodology — TDD

Strict Red → Green → Refactor: write the failing test first, then the minimum code
to pass, then refactor. No production code without a test that motivates it.
Deliver incrementally, user story by user story (US01 → US04).

## Tech stack

Java 21 · Spring Boot 3 · Maven · Spring Web · Spring Data JPA · PostgreSQL ·
Bean Validation · JUnit 5 + Mockito + AssertJ · Testcontainers · Spring Cache
(Caffeine/Redis) · Docker + docker-compose · React/Vue frontend.
