# pokemon-clean-arch-api

A full-stack **Pokédex** built for the Ballast Lane technical interview. A
**Java 21 + Spring Boot** service integrates with the public
[PokeAPI](https://pokeapi.co/docs/v2) to **retrieve**, **replicate** and **locally
modify** Pokémon data, backed by **PostgreSQL**, exposing a companion **JWT
auth API**, and consumed by a **React + TypeScript** single-page app. It follows
**Clean / Hexagonal Architecture** and **strict TDD**.

> **Status: implemented and green.** Backend (US01–US04 + auth) and the React
> frontend both exist with passing tests, Flyway migrations, demo seed data,
> Docker packaging and a one-command launcher.

---

## Table of contents

- [Features](#features)
- [Tech stack](#tech-stack)
- [Architecture](#architecture)
- [Repository layout](#repository-layout)
- [Prerequisites](#prerequisites)
- [Quick start (one command)](#quick-start-one-command)
- [Running it another way](#running-it-another-way)
- [Configuration](#configuration)
- [Demo data & credentials](#demo-data--credentials)
- [API reference](#api-reference)
- [Testing](#testing)
- [Development workflow](#development-workflow)
- [GenAI prompts](#genai-prompts)
- [Further documentation](#further-documentation)

---

## Features

| Story | What it does |
|-------|--------------|
| **US01 — Enumeration** | Paginated Pokémon list (sprite, category, mass in kg, abilities), with **server-side name search** and a **numbered pager**. Caching via Caffeine. |
| **US02 — Detailed view** | Image, core stats, cleaned narrative description, and evolutionary lineage. |
| **US03 — Synchronization** | Replicate Pokémon from the PokeAPI into the local store; choose how many (1–151). Adds proprietary fields (`localizedName`, `region`, `internalTags`). |
| **US04 — Local modification** | Edit the proprietary fields (`404` for missing, `400` for invalid). Re-sync **preserves** proprietary fields while refreshing PokeAPI data. |
| **Auth** | `register` / `login` with JWT. Read routes are public; write routes (sync/edit/delete) require a Bearer token. |

---

## Tech stack

**Backend** — Java 21 · Spring Boot 4.1 · Maven (wrapper) · Spring Web MVC ·
Spring Data JPA · Spring Security · Bean Validation · PostgreSQL · Flyway ·
Caffeine cache · JJWT · Actuator · JUnit 5 + Mockito + AssertJ + Testcontainers.

**Frontend** — React 19 · TypeScript · Vite · React Router · SWR · Tailwind CSS ·
Vitest + Testing Library + MSW · oxlint.

**Ops** — Docker + docker-compose.

---

## Architecture

Hexagonal (ports & adapters), base package `com.pokedex`. **Dependencies always
point inward** — the `core` is framework-free and unit-testable without Spring.

```
core/                       business core (no Spring / JPA / infrastructure)
 ├─ domain, dto, mapper      domain model + DTO projections + pure mappers
 ├─ ports/in                 driving ports (PokemonServicePort, UserServicePort)
 ├─ ports/out                driven ports (repository, PokeAPI provider, token, encoder)
 └─ usecase                  aggregate services (PokemonService, UserService)

infrastructure/adapter/
 ├─ in                       REST controllers + GlobalExceptionHandler
 └─ out                      persistence (JPA + adapters), pokeapi (HTTP client), security (JWT/BCrypt)

application/config           composition root (BeanConfig, SecurityConfig, CacheConfig)
```

The mandatory structure and dependency rules live in
[`prompts/pokedex-api-implementation-prompt.md`](prompts/pokedex-api-implementation-prompt.md).

---

## Repository layout

| Path | What it is |
|------|------------|
| `backend/` | Java + Spring Boot service (`pom.xml`, `mvnw`, `src/`, `Dockerfile`). |
| `frontend/` | React + TypeScript + Vite SPA (`package.json`). Talks to the backend over `/api`. |
| `docker-compose.yml` | Orchestrates the backend + PostgreSQL with a healthcheck. |
| `start-dev.ps1` / `start-dev.sh` | One-command launcher: Postgres → backend → frontend, health-checked. |
| `docs/` | API contract (`api_doc.md` / `.html`), Postman collection, smoke test. |
| `prompts/` | The GenAI prompts used to build the project. |
| `.claude/` | Project coding-standard skills + the pre-push validation gate. |

---

## Prerequisites

- **Java 21** (the backend uses the Maven wrapper — Maven itself is not required).
- **Node.js 20+** and npm (for the frontend).
- **Docker Desktop** — required for PostgreSQL and for the Testcontainers
  integration tests (`*IT`).

---

## Quick start (one command)

The fastest way to run **everything**. From the repo root it brings up
PostgreSQL → backend → frontend in order, waiting for each layer to be healthy,
then validates end-to-end. It reuses anything already running.

```bash
# Windows (PowerShell)
./start-dev.ps1

# macOS / Linux
./start-dev.sh
```

Then open:

- **Frontend:** http://localhost:5173
- **Backend API:** http://localhost:8080/api/pokemon
- **Health:** http://localhost:8080/actuator/health

---

## Running it another way

### Backend only (Maven wrapper, run from `backend/`)

Needs a reachable PostgreSQL (the schema is validated on boot), so start the DB
first: `docker compose up -d db`.

```bash
cd backend
./mvnw spring-boot:run          # run the app on :8080
./mvnw clean verify             # compile + all tests (unit + Testcontainers IT)
./mvnw test                     # unit tests only
```

On Windows use `./mvnw.cmd`.

### Frontend only (npm, run from `frontend/`)

```bash
cd frontend
npm install                     # first run only
npm run dev                     # Vite dev server on :5173, proxies /api → :8080
npm run build                   # production build
```

### Containerized (backend + PostgreSQL + seed data)

```bash
docker compose up               # backend on :8080 + PostgreSQL, with demo data
```

> `docker compose` runs the backend and the database only — start the frontend
> with `npm run dev`, or use the launcher above for the whole stack.

---

## Configuration

All settings live in `backend/src/main/resources/application.yml` and are
overridable via environment variables:

| Variable | Default | Purpose |
|----------|---------|---------|
| `SPRING_DATASOURCE_URL` | `jdbc:postgresql://localhost:5432/pokedex` | Database URL. |
| `SPRING_DATASOURCE_USERNAME` | `pokedex` | Database user. |
| `SPRING_DATASOURCE_PASSWORD` | `pokedex` | Database password. |
| `POKEAPI_BASE_URL` | `https://pokeapi.co/api/v2` | External PokeAPI base URL. |
| `POKEDEX_SEED_ENABLED` | `true` | Whether demo seed data is applied. |
| `SECURITY_JWT_SECRET` | *(dev default)* | JWT signing secret — **set this in production.** |
| `SECURITY_JWT_EXPIRES_IN` | *(dev default)* | JWT lifetime (seconds). |

> **Flyway owns the schema.** Migrations live in
> `backend/src/main/resources/db/migration` (`V1__init.sql`, `V2__seed_demo_data.sql`).
> Hibernate runs with `ddl-auto: validate` — it never creates tables; schema
> changes are new `V*` migrations.

> ⚠️ The shipped JWT secret and database credentials are **development defaults**.
> Override `SECURITY_JWT_SECRET` and the datasource credentials via environment
> variables before any real deployment.

---

## Demo data & credentials

When `POKEDEX_SEED_ENABLED=true` (default), migration `V2__seed_demo_data.sql`
pre-populates a demo admin user and some Pokémon.

- **Username:** `admin`
- **Password:** `admin123`

Sign in at `/login` to unlock the protected actions (replicate, edit, delete).

---

## API reference

Public read routes need no token; write routes require `Authorization: Bearer <jwt>`.

| Method | Endpoint | Auth | Purpose |
|--------|----------|------|---------|
| `GET` | `/api/pokemon?q=&page=&size=` | public | List (US01) — optional case-insensitive name search + pagination. |
| `GET` | `/api/pokemon/{id}` | public | Detail (US02). |
| `POST` | `/api/pokemon/sync` | **Bearer** | Replicate from PokeAPI (US03). Body: `{ "limit": 100, "offset": 0 }`. |
| `PUT` | `/api/pokemon/{id}` | **Bearer** | Edit proprietary fields (US04). `404`/`400` on error. |
| `DELETE` | `/api/pokemon/{id}` | **Bearer** | Delete a replicated Pokémon. |
| `POST` | `/api/auth/register` | public | Register a user. |
| `POST` | `/api/auth/login` | public | Log in → JWT. |
| `GET` | `/actuator/health` | public | Liveness/readiness. |

The full contract (PokeAPI calls per user story, data model, layer mapping) is in
[`docs/api_doc.md`](docs/api_doc.md) (browsable mirror: `docs/api_doc.html`).
Exercise the API with [`docs/pokedex.postman_collection.json`](docs/pokedex.postman_collection.json)
or [`docs/smoke-test.ps1`](docs/smoke-test.ps1).

---

## Testing

```bash
# Backend (from backend/) — Docker must be running for the Testcontainers *IT
./mvnw clean verify
./mvnw test -Dtest=ClassName#method   # a single test

# Frontend (from frontend/)
npm test          # Vitest (unit/component, MSW-mocked API)
npm run lint      # oxlint
npm run typecheck # tsc -b --noEmit
```

Integration tests (`*IT`) spin up PostgreSQL via Testcontainers. Unit tests in
`core` run with no Spring context — that independence is the point of the
hexagonal design.

---

## Development workflow

**Gitflow (mandatory):** `feature/*` → `develop` → `master`. Every change starts
on a `feature/*` branch cut from `develop`, merges into `develop` with `--no-ff`,
and only reaches `master` once verified. **TDD is strict** (Red → Green →
Refactor). Everything is written in English.

Two project skills under `.claude/skills/` carry the coding standards
(`backend-hexagonal-standard`, `frontend-component-standard`), and a pre-push gate
validates backend + frontend standards, security and QA before any push. Full
rules are in [CLAUDE.md](CLAUDE.md).

---

## GenAI prompts

This project was built with an AI coding tool (Claude Code); the prompts are kept
in [`prompts/`](prompts/) as the exercise's **"Generative AI tools"** deliverable.
Each documents the instruction, context, validation and refinements.

- [`pokedex-api-implementation-prompt.md`](prompts/pokedex-api-implementation-prompt.md)
  — master prompt: engineer role, mandatory hexagonal architecture, TDD, SOLID,
  the full API contract, the data model and the definition of done.
- [`01-analyze-requirements-and-document-pokeapi.md`](prompts/01-analyze-requirements-and-document-pokeapi.md)
  — analyze the exercise and document the PokeAPI endpoints per user story.
- [`frontend.md`](prompts/frontend.md) — the React SPA.
- [`backend-stantdard-coding.md`](prompts/backend-stantdard-coding.md) /
  [`frontend-stantdard-coding.md`](prompts/frontend-stantdard-coding.md) — the
  backend and frontend coding standards.
- [`qa.md`](prompts/qa.md) · [`security.md`](prompts/security.md) ·
  [`validate-all-cycle.md`](prompts/validate-all-cycle.md) — end-to-end QA,
  security audit, and the pre-push validation cycle.

See [`prompts/README.md`](prompts/README.md) for the full index.

---

## Further documentation

- [API contract — Markdown](docs/api_doc.md) · [HTML](docs/api_doc.html)
- [Contributor guide & workflow rules](CLAUDE.md)
- [Prompts](prompts/)
