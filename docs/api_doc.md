# API Documentation — PokeAPI Integration

> Support document for the technical exercise **Java - BLA - Technical Interview Exercise (V2)**.
> External API base URL: `https://pokeapi.co/api/v2`
> Official docs: https://pokeapi.co/docs/v2
>
> All examples in this document were verified live against the real endpoints
> using Pokémon #1 (*bulbasaur*).

---

## Overview

The goal is a **RESTful API in Java + Spring Boot** using **Clean / Hexagonal
Architecture** and **TDD**, that:

1. Consumes the external **PokeAPI** to fetch Pokémon data.
2. **Replicates** that data into a local relational database (to add proprietary fields).
3. Exposes its own CRUD endpoints over the local data.
4. Includes an **auxiliary user API** (registration, authentication, public/protected routes).
5. Has a **frontend** (React/Vue) that consumes the API.

The PokeAPI is **read-only (GET)** and requires **no API key**. Our API acts as an
intermediary and as the replication / modification layer.

### Wording map: statement → PokeAPI field

- **sprite / image** → `sprites`
- **category** → `genera`
- **mass** → `weight`
- **skills** → `abilities`
- **narrative description** → `flavor_text_entries`
- **evolutionary lineage** → `evolution-chain`

---

## User Story 01 — Pokemon Enumeration

Browse Pokémon through paginated results, showing each entry's **sprite,
category, mass, and skills**. *(Nice-to-have: caching.)*

**Needs 3 endpoints.**

### 1. Paginated list

```
GET https://pokeapi.co/api/v2/pokemon?limit=20&offset=0
```

> *"The system should allow users to browse Pokemon via **paginated results**..."*
> — US01. The phrase "paginated results" is what requires this endpoint.

| Query param | Description | Default |
|-------------|-------------|---------|
| `limit`  | Results per page | 20 |
| `offset` | Starting index (pagination) | 0 |

**Response 200**
```json
{
  "count": 1351,
  "next": "https://pokeapi.co/api/v2/pokemon?offset=5&limit=5",
  "previous": null,
  "results": [
    { "name": "bulbasaur", "url": ".../pokemon/1/" },
    { "name": "ivysaur",   "url": ".../pokemon/2/" }
  ]
}
```

> ⚠️ **N+1 problem:** this endpoint returns only `name` and `url` — no sprite,
> abilities, or category. Each result forces a call to `/pokemon/{id}` and
> `/pokemon-species/{id}`. One page of 20 = dozens of calls → justifies the
> **caching layer**.

### 2. Pokémon detail — sprite, mass, skills

```
GET https://pokeapi.co/api/v2/pokemon/1
```

> *"...displaying each entry's **sprite**, category, **mass**, and a collection of their **skills**."*
> — US01.

**Response 200 (fields relevant to US01)**
```json
{
  "id": 1, "name": "bulbasaur",
  "weight": 69,
  "abilities": [ { "ability": { "name": "overgrow" }, "is_hidden": false } ],
  "sprites": { "front_default": ".../sprites/pokemon/1.png" },
  "species": { "url": ".../pokemon-species/1/" }
}
```

| Statement asks | Source JSON |
|----------------|-------------|
| sprite | `sprites.front_default` |
| mass | `weight` (hectograms ÷10 = kg) |
| skills | `abilities[].ability.name` |

> **Category is missing here:** the `category` field does not exist in this
> endpoint. `species.url` chains to step 3.

### 3. Species — category

```
GET https://pokeapi.co/api/v2/pokemon-species/1
```

> *"...displaying each entry's sprite, **category**, mass..."* — US01.

Category lives in `genera`, a **list with the category in several languages**;
walk it and filter by language.

**Response 200 (field relevant to US01)**
```json
{
  "genera": [
    { "genus": "Seed Pokémon",    "language": { "name": "en" } },
    { "genus": "Pokémon Semilla", "language": { "name": "es" } }
  ]
}
```

| Statement asks | Source JSON |
|----------------|-------------|
| category | `genera[]` where `language.name == "en"` → `.genus` |

---

## User Story 02 — Detailed View

Access full data for a chosen Pokémon: **image, core statistics, narrative
description, and evolutionary lineage.**

**Needs 3 endpoints.**

### 1. Pokémon detail — image and stats

```
GET https://pokeapi.co/api/v2/pokemon/1
```

> *"...viewing its **image**, **core statistics**, narrative description, and evolutionary lineage."*
> — US02.

**Response 200 (fields relevant to US02)**
```json
{
  "stats": [
    { "base_stat": 45, "stat": { "name": "hp" } },
    { "base_stat": 49, "stat": { "name": "attack" } },
    { "base_stat": 49, "stat": { "name": "defense" } }
  ],
  "sprites": {
    "other": { "official-artwork": { "front_default": ".../official-artwork/1.png" } }
  }
}
```

| Statement asks | Source JSON |
|----------------|-------------|
| image | `sprites.other.official-artwork.front_default` |
| core statistics | `stats[].base_stat` + `stats[].stat.name` |

> For US02 use the **high-quality image** (official-artwork), not the small US01 sprite.

### 2. Species — narrative description

```
GET https://pokeapi.co/api/v2/pokemon-species/1
```

> *"...viewing its image, core statistics, **narrative description**, and evolutionary lineage."*
> — US02.

**Response 200 (fields relevant to US02)**
```json
{
  "flavor_text_entries": [
    { "flavor_text": "A strange seed was\nplanted on its\nback at birth.",
      "language": { "name": "en" } }
  ],
  "evolution_chain": { "url": ".../evolution-chain/1/" }
}
```

| Statement asks | Source JSON |
|----------------|-------------|
| narrative description | `flavor_text_entries[]` where `language.name == "en"` |
| (evolution link) | `evolution_chain.url` → chains to step 3 |

> ⚠️ **Dirty description:** `flavor_text` contains line breaks (`\n`) and
> form-feeds (`\f`). Normalize: `text.replaceAll("[\\n\\f]", " ").trim()`.

### 3. Evolution chain — evolutionary lineage

```
GET https://pokeapi.co/api/v2/evolution-chain/1
```

> *"...viewing its image, core statistics, narrative description, and **evolutionary lineage**."*
> — US02.

**Response 200 (recursive structure)**
```json
{
  "chain": {
    "species": { "name": "bulbasaur" },
    "evolves_to": [
      { "species": { "name": "ivysaur" },
        "evolves_to": [
          { "species": { "name": "venusaur" }, "evolves_to": [] }
        ] }
    ]
  }
}
```

| Statement asks | Source JSON |
|----------------|-------------|
| evolutionary lineage | walk `chain.evolves_to[]` recursively |

> Flattened: `bulbasaur → ivysaur → venusaur`. The `id` comes from `species.url`.

---

## User Story 03 — Data Synchronization

Persist Pokémon into a local relational store so the data becomes *ours* —
enabling proprietary fields and later modification (US04).

> *"Develop a mechanism to **persist Pokemon data into a local relational store**.
> This replication layer is intended to facilitate the **addition of proprietary
> fields**... localized nomenclature, geographical metadata, or internal
> classification tags."* — US03.

**No new external endpoint is consumed.** It reuses the US01/US02 PokeAPI endpoints
(list → detail → species → evolution) and **stores the result in our own database**,
adding fields the PokeAPI does not have.

### What gets stored — (A) replicated from PokeAPI

| Field | Source (PokeAPI) |
|-------|------------------|
| `id` (PK) | `pokemon.id` |
| `name` | `pokemon.name` |
| `spriteUrl` / `imageUrl` | `sprites.front_default` / official-artwork |
| `weight`, `height` | `weight`, `height` |
| `category` | `genera` (en) |
| `description` | `flavor_text` (cleaned) |
| `abilities`, `stats`, `evolutions` | detail + evolution-chain |

### What gets stored — (B) proprietary fields

Fields the PokeAPI will never provide. They prove the local DB is the system of
record and are exactly what US04 edits. They also satisfy the DB requirement:
*"a minimum of two descriptive attributes"*.

| Proprietary field | Statement use case | Example |
|-------------------|--------------------|---------|
| `localizedName` | localized nomenclature | "Bulbasaur ES" |
| `region` | geographical metadata | "Kanto" |
| `internalTags` | internal classification tags | ["starter", "favorite"] |

### Endpoint — trigger the replication

```
POST /api/pokemon/sync
```

**Why:** it fetches from the PokeAPI and writes/updates the records in our
database. It is the only path that pulls PokeAPI data in; from here on the app
reads and edits the local copy.

Request body (optional — how much to replicate):
```json
{ "limit": 20, "offset": 0 }
```

Response `201 Created`:
```json
{
  "synced": 20,
  "created": 18,
  "updated": 2,
  "items": [
    { "id": 1, "name": "bulbasaur", "category": "Seed Pokémon" }
  ]
}
```

**Full CRUD is required** over this local resource (statement: *"comprehensive CRUD
operations"*):

| Verb | Endpoint | Story |
|------|----------|-------|
| `POST` | `/api/pokemon/sync` | replicate — US03 |
| `GET` | `/api/pokemon` | read — US01/US02 |
| `PUT` | `/api/pokemon/{id}` | update — US04 |
| `DELETE` | `/api/pokemon/{id}` | remove — CRUD |

---

## User Story 04 — Local Data Modification

Update any Pokémon stored in the local database, with robust validation.

> *"Enable **update operations** for any Pokemon currently stored within the local
> database. Ensure robust validation: provide **404** responses for missing
> records, **400** status codes for malformed payloads..."* — US04.

**Does not touch the PokeAPI.** It operates only on the local database.

**Suggested own endpoint (our API):** `PUT /api/pokemon/{id}`

| Situation | HTTP response |
|-----------|---------------|
| Record not found | `404 Not Found` |
| Malformed / invalid payload | `400 Bad Request` |
| Additional defensive logic | as needed |

---

## Full synchronization flow (US01 → US03)

```
1. GET /pokemon?limit=N&offset=M        → list of {name, url}
2. For each url:
     GET /pokemon/{id}                   → sprite, weight, abilities, stats
3. GET /pokemon-species/{id}            → category, description, evolution_chain.url
4. GET /evolution-chain/{id}            → evolutionary lineage (recursive)
5. Map to domain → persist to local DB (+ US03 proprietary fields)
6. (nice to have) Cache each response
```

Each full Pokémon ≈ **3 calls** (detail + species + evolution), plus 1 per page.
This strongly justifies the **caching layer**.

---

*Endpoints verified on 2026-08-12 by browsing `https://pokeapi.co/api/v2` with
Pokémon #1 (bulbasaur). Mirror of `docs/api_doc.html`.*
