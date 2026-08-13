---
name: frontend-component-standard
description: Use when writing, modifying, or reviewing React/TypeScript frontend code in this project — adding or changing components, pages, hooks, api modules, UI primitives, styling, or frontend tests under frontend/. Enforces the container/presentational split, reuse of the components/ui design system, responsive-by-default layout, and TDD as already applied under frontend/src.
---

# Frontend Component Standard (this project)

## Overview

The SPA (`frontend/src/`) is React 19 + TypeScript + Vite, styled with Tailwind,
data via SWR, tested with Vitest + Testing Library + MSW. It **consumes the backend
REST API over `/api`** (Vite proxies to `localhost:8080`) and never calls the PokeAPI
directly. New code must follow the container/presentational split, reuse the existing
design-system primitives, and stay responsive.

## Layers — where code goes

| Concern | Folder |
|---|---|
| Typed HTTP layer (the ONLY place `fetch`/endpoints live) | `api/` — `client.ts` (+ `ApiError`), `pokemon.ts`, `auth.ts`, `types.ts` |
| Data hooks (SWR keys, fetching/caching) | `hooks/` — `usePokemonList`, `usePokemonDetail` |
| Cross-cutting state providers | `context/` — `AuthContext`, `UIContext` |
| **General reusable presentational primitives (design system)** | `components/ui/` — `Button`, `Input`, `Spinner`, `EmptyState`, `ErrorState`, `Pagination`, `GlassCard`, `Icon` |
| Module-specific presentational components | `components/<module>/` — `pokemon/`, `auth/`, `layout/` |
| Container (smart) components | `pages/<Page>/index.tsx` |
| Pure helpers (no JSX) | `lib/` — `format`, `cn`, `accent`, `tokenStore` |

## Core rules

1. **Container vs presentational is mandatory.**
   - Pages (`pages/*`) are containers: they call hooks, hold state, handle
     events/navigation, and pass plain props down.
   - Components (`components/*`) are presentational: data + callbacks via props only.
     **No `fetch` and no SWR calls inside a presentational component.**
2. **Reuse before you create — for design consistency.** Build UI from the existing
   `components/ui/*` primitives. Do **not** hand-roll a one-off `<button>`, input, or
   empty/error/loading state — use `Button`, `Input`, `EmptyState`, `ErrorState`,
   `Spinner`, `Pagination`. If a truly generic primitive is missing, add it to
   `components/ui/` so the whole app reuses it; never duplicate styles.
3. **Responsive by default.** Design mobile-first and layer Tailwind breakpoints
   (`sm:`, `md:`, `lg:`). Use fluid layouts (flex/grid, `max-w-*`, `flex-wrap`); no
   fixed pixel widths that break on small screens. Not done until it works mobile→desktop.
4. **All API access goes through `api/*` and is consumed via `hooks/*`.** Types from
   `api/types.ts`. Handle failures with `ApiError` → `ErrorState`, empties → `EmptyState`,
   loading → `Spinner`.
5. **Accessibility:** interactive elements are real controls with `aria-*` labels
   (see `Button`, `PokemonCard`). Everything in English.

## Workflow (TDD + Gitflow)

- **TDD, strict Red → Green → Refactor.** Write the failing test first (Vitest +
  Testing Library, API mocked with MSW), co-located as `*.test.tsx` next to the
  component/hook, then minimum code, then refactor. No production code without a
  motivating test.
- **Gitflow:** `feature/<desc>` from `develop`, merge with `--no-ff`, delete the
  branch. Never commit to `master`. Conventional Commits.
- **Verify before "done" (from `frontend/`):** `npm test` · `npm run lint` ·
  `npm run typecheck` · `npm run build`.

## Review dependencies on every change

Changing an `api/*` module or a type in `api/types.ts` → update every hook, component
and page that consumes it. Changing a `components/ui/*` primitive → check every
consumer across modules/pages (they are reused widely) and re-run their tests. Keep
MSW handlers/fixtures in `test/` in sync with any API contract change.

## Self-review checklist

- [ ] Data-fetching/state lives in a page/hook (container); the component is presentational (props only).
- [ ] Reused `components/ui/*` instead of duplicating styles; any new generic primitive placed in `components/ui/`.
- [ ] Responsive from mobile to desktop (breakpoints, fluid layout).
- [ ] Goes through `api/*` + `hooks/*`, with loading/empty/error states handled.
- [ ] A test fails first, then passes (real TDD), with MSW handlers/fixtures updated.
- [ ] `npm test`, `npm run lint`, `npm run typecheck`, `npm run build` all pass.

## Common mistakes

| Mistake | Fix |
|---|---|
| `fetch`/SWR inside a presentational component | Move data to the page/hook; pass props down |
| Hand-rolled `<button className=...>` | Use `components/ui/Button` |
| One-off empty/error/loading markup | Use `EmptyState` / `ErrorState` / `Spinner` |
| New primitive dumped in a module folder | If it's generic, it belongs in `components/ui/` |
| Fixed widths / desktop-only layout | Mobile-first + Tailwind breakpoints + fluid layout |
| Component shipped without a co-located test | Write the failing `*.test.tsx` first (MSW-mocked) |
