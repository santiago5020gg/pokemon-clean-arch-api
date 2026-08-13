# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project

**pokemon-clean-arch-api** — Pokémon RESTful API for the Ballast Lane technical
interview exercise.

A Java + Spring Boot service that integrates with the public
[PokeAPI](https://pokeapi.co/docs/v2) to **retrieve**, **replicate**, and
**locally modify** Pokémon data. It is backed by PostgreSQL, exposes a companion
JWT user-authentication API, and is consumed by a React frontend.

## Current state

**Full stack is implemented and green.** Backend (US01–US04 + auth) and the React
frontend both exist with passing tests, DB migrations, demo seed data, Docker
packaging, and a one-command launcher. Work now is incremental feature/fix work on
top of a working system, not greenfield scaffolding.

## Coding standards (skills)

Two project skills under `.claude/skills/` carry the coding standard and are the
authoritative, on-demand reference when writing code. Invoke the matching one
before adding or changing code:

- **`backend-hexagonal-standard`** — the Java + Spring Boot standard (hexagonal
  layout, framework-free `core`, aggregate services, Flyway, TDD). Use it for any
  backend change under `backend/`.
- **`frontend-component-standard`** — the React + TypeScript standard
  (container/presentational split, reuse of the `components/ui/` design system,
  responsive-by-default, TDD). Use it for any frontend change under `frontend/`.

## Repository layout

- `backend/` — the Java + Spring Boot service (its own `pom.xml`, `mvnw`, `src/`,
  `Dockerfile`). All backend code lives here.
- `frontend/` — the React + TypeScript + Vite SPA (its own `package.json`). Talks
  to the backend over `/api` (Vite dev proxy → `localhost:8080`).
- `docker-compose.yml` — repo root; orchestrates the backend (built from
  `./backend`) + PostgreSQL with a healthcheck.
- `start-dev.ps1` / `start-dev.sh` — **the fastest way to run everything.** One
  command brings up Postgres → backend → frontend in order, waiting for each layer
  to be healthy before starting the next, then validates end-to-end. Reuses any
  already-running Postgres/backend/frontend instead of duplicating them.
- `docs/api_doc.md` / `docs/api_doc.html` — the single source of truth for the API
  contract: PokeAPI endpoints per user story, our own CRUD, the auth API, the
  database entities and the architecture-layer mapping. **Read this before writing
  any endpoint or entity.** The two files are mirrors (Markdown + browsable HTML).
  `docs/pokedex.postman_collection.json` and `docs/smoke-test.ps1` exercise the API.
- `prompts/` — the GenAI prompts used to build the project (English). The master
  prompt `prompts/pokedex-api-implementation-prompt.md` is the authoritative
  specification of role, hexagonal structure, TDD flow and the API contract;
  `prompts/frontend.md` and `prompts/qa.md` cover the SPA and E2E testing.
- `ejemplos-solid/` — standalone SOLID teaching examples (not part of the app).
- `README.md` — public overview.

The exercise PDF is intentionally git-ignored (it is Ballast Lane's document).

## Architecture (hexagonal / ports & adapters)

The mandatory package structure and dependency rules live in
`prompts/pokedex-api-implementation-prompt.md`. The essentials:

- Base package `com.pokedex`.
- `core/` (domain, dto, mapper, ports, usecase) is the business core. It **must not
  depend on Spring, JPA, or `infrastructure`** — only on interfaces (ports).
  Dependencies always point inward (dependency inversion). This independence is
  what makes the core unit-testable without a Spring context, and it is the single
  most heavily-evaluated aspect of the exercise.
- Ports: `core/ports/in` are the driving ports the controllers call
  (`PokemonServicePort`, `UserServicePort`); `core/ports/out` are the driven ports
  the core needs (`PokemonRepositoryPort`, `PokemonProviderPort`,
  `UserRepositoryPort`, `TokenProviderPort`, `PasswordEncoderPort`).
- Use cases are **aggregate services**: `PokemonService` and `UserService`
  implement every operation of their `*ServicePort` (list/detail/sync/update/delete,
  register/login) — not one class per operation.
- `infrastructure/adapter/in` — REST controllers + `GlobalExceptionHandler`
  (`@RestControllerAdvice`) returning a shared `ErrorResponse`.
- `infrastructure/adapter/out` — `persistence` (JPA entities/repositories +
  `*Adapter` + `*PersistenceMapper`), `pokeapi` (the HTTP client), `security`
  (BCrypt + JWT filter/provider).
- `application/config` — the composition root. `BeanConfig` wires concrete adapters
  to core services explicitly; `SecurityConfig` and `CacheConfig` complete the setup.
  `PokedexApplication` is the Spring Boot entry point.

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
  → N+1 pattern → justifies the Caffeine caching layer.
- **Our resource `pokemon`:** `POST /api/pokemon/sync` (replicate — US03),
  `GET /api/pokemon` + `GET /api/pokemon/{id}` (US01/US02), `PUT /api/pokemon/{id}`
  (US04, `404`/`400`), `DELETE /api/pokemon/{id}`.
- **Auth API `users`:** `POST /api/auth/register`, `POST /api/auth/login` (JWT).
  Read routes are public; write routes are protected (Bearer token).
- Replicated records carry proprietary fields (`localizedName`, `region`,
  `internalTags`) that the PokeAPI does not have — this is the point of US03 and
  what US04 edits. On re-sync, `PokemonService.sync` **preserves** these proprietary
  fields on existing rows while refreshing the replicated PokeAPI data.

Data gotchas: `weight` is in hectograms (÷10 = kg); `flavor_text` contains `\n`/`\f`
that must be stripped; `genera`/`flavor_text_entries` are per-language lists (filter
by `language.name`).

## Database & config

- **Flyway owns the schema.** Migrations live in
  `backend/src/main/resources/db/migration` (`V1__init.sql` schema,
  `V2__seed_demo_data.sql` demo admin user + Pokémon). Hibernate runs with
  `ddl-auto: validate` — it never creates tables. Schema changes = a new `V*` migration.
- Config is in `backend/src/main/resources/application.yml`, all overridable via env:
  `SPRING_DATASOURCE_URL/USERNAME/PASSWORD` (defaults target `localhost:5432/pokedex`,
  user/pass `pokedex`), `POKEAPI_BASE_URL`, `POKEDEX_SEED_ENABLED` (default `true`),
  `SECURITY_JWT_SECRET`, `SECURITY_JWT_EXPIRES_IN`.
- Actuator health is at `/actuator/health` (used by the launcher and compose healthcheck).

## Development commands

### Backend (Maven wrapper — run from `backend/`)

Maven is NOT installed; use the wrapper (`.\mvnw.cmd` on Windows). Java 21 is required.
`spring-boot:run` needs a reachable PostgreSQL (schema is validated on boot) — start
`docker compose up -d db` first, or just use the launcher below.

```bash
cd backend
./mvnw clean verify                 # compile + run all tests (unit + Testcontainers IT)
./mvnw test                         # run tests
./mvnw test -Dtest=ClassName#method # a single test
./mvnw spring-boot:run              # run the app locally (needs Postgres on :5432)
```

Integration tests (`*IT`) spin up PostgreSQL via Testcontainers, so Docker must be
running for `clean verify`.

### Frontend (npm — run from `frontend/`)

```bash
cd frontend
npm install         # first run only
npm run dev         # Vite dev server on :5173, proxies /api → :8080
npm test            # vitest run (unit/component, MSW-mocked API)
npm run test:watch  # vitest watch
npm run lint        # oxlint
npm run typecheck   # tsc -b --noEmit
npm run build       # tsc -b && vite build
```

### Whole stack (from repo root)

```bash
./start-dev.ps1     # Windows: Postgres → backend → frontend, health-checked, end-to-end
./start-dev.sh      # macOS/Linux equivalent
docker compose up   # containerized backend + PostgreSQL + seed data (no frontend)
```

## Git workflow — Gitflow (MANDATORY)

| Branch | Purpose |
|--------|---------|
| `master` | Stable line. Only receives merges from `develop` once verified. |
| `develop`  | Integration branch (repo default). All completed features merge here first. |
| `feature/*` | One branch per change, cut from `develop`. |

For **every** change: create `feature/<desc>` from `develop` → work with tests →
merge into `develop` with `--no-ff` → **delete the feature branch** (`git branch -d`) →
merge `develop` into `master` only when verified. Deleting the feature branch right after
it lands in `develop` is part of the workflow — keep the branch list clean.
Never commit directly to `master`; never skip the feature branch.

Commit messages follow **Conventional Commits** (`feat:`, `fix:`, `docs:`,
`chore:`, `test:`, `refactor:`). On Windows PowerShell, pass multi-line commit
messages via `git commit -F <file>` (inline here-strings with parentheses break
the parser).

## Language rule

Everything in this repository is written in **English**: source code, comments,
documentation, commit messages, branch names, and the prompts under `prompts/`.
(The `ejemplos-solid/` teaching sample is the one Spanish exception.)

## Development methodology — TDD

Strict Red → Green → Refactor: write the failing test first, then the minimum code
to pass, then refactor. No production code without a test that motivates it.
Deliver incrementally, user story by user story (US01 → US04).

## Tech stack

**Backend:** Java 21 · Spring Boot 4.1 (`spring-boot-starter-parent`) · Maven ·
Spring Web MVC · Spring Data JPA · Spring Security · Bean Validation · PostgreSQL ·
Flyway · Caffeine cache · JJWT · Actuator · JUnit 5 + Testcontainers.

**Frontend:** React 19 · TypeScript · Vite · React Router · SWR · Tailwind CSS ·
Vitest + Testing Library + MSW · oxlint.

**Ops:** Docker + docker-compose.
