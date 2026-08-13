---
name: backend-hexagonal-standard
description: Use when writing, modifying, or reviewing Java/Spring Boot backend code in this project — adding or changing endpoints, services, ports, adapters, JPA entities, Flyway migrations, or backend tests. Enforces the hexagonal (ports & adapters) layout, aggregate services, and TDD as already applied under backend/.
---

# Backend Hexagonal Standard (this project)

## Overview

The backend (`backend/`, base package `com.pokedex`) is a Java 21 + Spring Boot
hexagonal app. **Dependencies always point inward.** New code must match the
layers and conventions already in the codebase — the `core` must stay framework-free
and unit-testable without Spring. This is the single most heavily-evaluated aspect
of the project. Read `docs/api_doc.md` before writing any endpoint or entity.

## Layers — where code goes

| Concern | Package |
|---|---|
| Domain, DTOs, mappers, use cases | `core/` (no Spring, no JPA, no `infrastructure` imports) |
| Driving ports (called by controllers) | `core/ports/in` — `PokemonServicePort`, `UserServicePort` |
| Driven ports (needed by core) | `core/ports/out` — `PokemonRepositoryPort`, `PokemonProviderPort`, `UserRepositoryPort`, `TokenProviderPort`, `PasswordEncoderPort` |
| REST controllers + `GlobalExceptionHandler` | `infrastructure/adapter/in` |
| Persistence (JPA entities/repos, `*Adapter`, `*PersistenceMapper`), PokeAPI client, security | `infrastructure/adapter/out` |
| Composition root (wire adapters → core in `BeanConfig`) | `application/config` |

## Core rules

1. **`core` is framework-free.** No `org.springframework.*`, no `jakarta.persistence.*`,
   no import from `infrastructure` inside any `core` class. If a core unit test needs
   a Spring context, the design is wrong.
2. **Aggregate services, not one-class-per-operation.** A new operation on Pokémon
   goes as a method into the existing `PokemonService` (which implements the whole
   `PokemonServicePort`: list/detail/sync/update/delete); user operations into
   `UserService`. Do **not** create `CountPokemonService`, `GetPokemonUseCase`, etc.
   Add the method to the port interface and to the aggregate service.
3. **New driven capability = new method on an existing `*Port` in `core/ports/out`**,
   implemented by its adapter in `infrastructure/adapter/out`. The core depends on the
   interface, never the adapter.
4. **Schema is owned by Flyway** (`ddl-auto: validate`). Any schema change is a new
   `V*__*.sql` migration under `backend/src/main/resources/db/migration` — never rely
   on Hibernate to create/alter tables.
5. **`sync` preserves proprietary fields** (`localizedName`, `region`, `internalTags`)
   on existing rows while refreshing PokeAPI data.
6. Wire every new adapter to its core service explicitly in `BeanConfig`.
7. Everything in English: code, comments, commits, branch names.

## Workflow (TDD + Gitflow)

- **TDD, strict Red → Green → Refactor.** Write the failing test first (unit test in
  `core` with no Spring; integration `*IT` in adapters via Testcontainers), then the
  minimum code to pass, then refactor. No production code without a motivating test.
- **Gitflow:** branch `feature/<desc>` from `develop`, merge back with `--no-ff`,
  delete the branch. Never commit to `master`. Conventional Commits (`feat:`, `fix:`,
  `test:`, `refactor:`…). On Windows PowerShell pass multi-line messages via `git commit -F`.
- **Verify before "done":** run `./mvnw clean verify` from `backend/` (Docker must be
  up for the Testcontainers `*IT`).

## Review dependencies on every change

When you touch a port or a method, trace the impact across layers: check every caller
and implementer of that port, update the wiring in `BeanConfig`, update/add tests for
everything affected (not just the file you edited), and confirm no new dependency leaks
into `core`.

## Self-review checklist

- [ ] No `core` class imports Spring / JPA / `infrastructure`.
- [ ] New operation added to the existing aggregate service (+ its `*ServicePort`), not a new loose class.
- [ ] Any DB change is a new `V*` Flyway migration.
- [ ] New adapters wired in `BeanConfig`.
- [ ] A test fails first, then passes (real TDD); affected tests updated.
- [ ] `./mvnw clean verify` passes green.

## Common mistakes

| Mistake | Fix |
|---|---|
| New `XxxUseCase`/`XxxService` per operation | Add the method to the existing aggregate service + its port |
| Business logic in the controller | Controller only adapts HTTP → calls the driving port |
| `@Entity`/`@Autowired`/Spring types in `core` | Keep `core` pure; those belong in adapters/config |
| Letting Hibernate create the table | Write a `V*` Flyway migration |
| Controller calling a JPA repository directly | Go through the driven port + adapter |
