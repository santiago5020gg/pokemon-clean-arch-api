# 01 — Analyze requirements and document the PokeAPI

## Goal

Understand the technical exercise and produce a precise, verifiable reference of
the external PokeAPI endpoints needed for each user story.

## Prompt

> Analyze the exercise document `Java - BLA - Technical Interview Exercise - V2.pdf`
> and tell me what has to be built. Then use the browser to navigate the PokeAPI
> mentioned in the document and understand how its endpoints work. Create
> `docs/api_doc.md` where you specify the endpoints, how they work, and which ones
> must be used for the implementation according to the document.
>
> Organize the documentation **by user story**. For each user story, list the
> endpoints it needs, in order. For each endpoint include:
> - which part of the exercise statement requires it (quote the exact wording and
>   highlight the key phrase),
> - a full example URL to verify it (use a fixed sample Pokémon, e.g. bulbasaur),
> - the relevant response fields for that story and where each maps in the JSON.

## Context

- The exercise PDF (read directly).
- Live browsing of `https://pokeapi.co/api/v2` via the Playwright MCP browser.

## Validation

- Every documented endpoint was called live against the real PokeAPI using
  Pokémon #1 (bulbasaur), and the JSON responses were inspected before writing
  them down (not taken from memory).
- Field mappings were checked against the exercise wording:
  - `sprite` → `sprites`
  - `mass` → `weight` (hectograms)
  - `skills` → `abilities`
  - `category` → `genera` (list filtered by language)
  - `narrative description` → `flavor_text_entries`
  - `evolutionary lineage` → `evolution-chain` (recursive structure)

## Refinements

- Caught that the paginated `/pokemon` endpoint returns only `name` + `url`,
  which forces an N+1 call pattern and justifies the caching "nice-to-have".
- Caught that `category` does **not** exist in `/pokemon/{id}` and must come from
  `/pokemon-species/{id}`.
- Noted that `flavor_text` contains `\n` / `\f` control characters that must be
  normalized before display.
- Produced an HTML mirror of the reference (`docs/api_doc.html`) with clickable
  "try it" links to verify each endpoint.
