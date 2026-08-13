# Pokédex — Frontend

React + TypeScript single-page app for the Pokémon Clean Architecture API. It
browses replicated Pokémon, shows stats/lineage, and lets authenticated users
replicate (US03), edit (US04) and delete records. Visual direction: **glassmorphism
+ neon**, dark-first.

## Stack

- **React 19** + **TypeScript** (strict, no `any`) on **Vite**
- **SWR** for data fetching + caching (GETs keyed by URL+params → served from cache)
- **React Router** for routing
- **Tailwind CSS** for styling
- **React Context API** for app state (auth/JWT + toasts) — no Redux/Zustand
- **Vitest + React Testing Library + MSW** for TDD (behavior + unit tests)

## Architecture (container / presentational)

```
src/
  api/         typed client (Bearer), endpoints, DTO types (mirror the backend)
  hooks/       SWR hooks (usePokemonList, usePokemonDetail)
  context/     AuthContext (JWT) + UIContext (toasts) = app state
  pages/       containers: state + data + logic (one folder per route)
  components/  presentational modules: pokemon/ · auth/ · ui/ · layout/
  lib/         pure helpers (hectograms→kg, accents, className, token store)
  router/      route table
```

Read routes are public; write actions (sync/edit/delete) are gated by auth state,
matching the backend's security model.

## Getting started

```bash
npm install
npm run dev        # http://localhost:5173
```

The dev server proxies `/api` → `http://localhost:8080` (the Spring Boot backend),
so no CORS setup is needed. See `.env` to change the API base URL for Docker.

### One command for the whole stack

From the repo root, `./start-dev.ps1` (Windows PowerShell) or `./start-dev.sh`
(macOS/Linux) boots the **entire stack in order, validating each layer**:

1. **PostgreSQL** via `docker compose up -d db` (waits for `pg_isready`)
2. **Backend** via `backend/mvnw spring-boot:run` (waits for `/actuator/health`)
3. **Frontend** via Vite (waits for the dev server)
4. **End-to-end check** (`GET /api/pokemon`), then prints the URL to open

Only Docker, Java 21 and Node.js are required — nothing else to set up first.
Ctrl+C stops the backend, frontend and the database container.

## Scripts

| Command | Description |
|---------|-------------|
| `npm run dev` | Start the Vite dev server |
| `npm run build` | Typecheck (`tsc`) + production build |
| `npm test` | Run the test suite once |
| `npm run test:watch` | Watch mode |
| `npm run test:coverage` | Coverage report |
| `npm run typecheck` | Types only |

## Environment

| Var | Default | Purpose |
|-----|---------|---------|
| `VITE_API_BASE_URL` | `/api` | Base URL the app calls (relative → uses the dev proxy) |
| `VITE_API_PROXY_TARGET` | `http://localhost:8080` | Where the dev proxy forwards `/api` |
