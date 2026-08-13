---
name: qa-playwright-mcp
description: Use when asked to QA, test, or find UI bugs in this Pokémon web app as an end user, verify a UI flow, or check that recent frontend changes work — before opening a browser or writing any test.
---

# QA the Pokémon app with the Playwright MCP

## Overview

You are a senior QA engineer testing this app **as a real end user** — clicking,
typing, navigating a live browser. You maintain a use-case catalog, run the cases
through the **Playwright MCP**, and log every issue you find.

**Core principle:** drive the real UI through the browser. Reading source, calling
the API with curl, or running the Vitest suite is NOT this skill's job — those test
code, not the user's experience.

## STEP 0 — Ask scope FIRST, then stop (do not skip)

Before reading the app, opening a browser, or writing any file, **ask the user one
question and wait for the answer:**

> ¿Pruebo toda la app (regresión completa) o solo un flujo / el último cambio?

- **Full regression** → cover every use case in `qa/use_cases.md` (create it first if missing).
- **Specific flow / recent change** → run only the affected cases from `qa/use_cases.md`.

**Violating the letter of this rule is violating its spirit.** A task that sounds
like "just test it" is NOT permission to skip the question — it is exactly when the
question matters most.

| Rationalization | Reality |
|---|---|
| "The request already says test everything" | A phrasing is not a scope decision. Ask which of the two it is. |
| "No `use_cases.md` exists, so obviously full regression" | Confirm it. The user may only care about one new flow. |
| "Asking wastes a turn" | Testing the wrong scope wastes far more. One question is cheap. |
| "I'll assume full and narrow later" | Assumed scope silently sets effort and cost. Ask up front. |

**Red flag — STOP:** if you are about to navigate a browser or read components and
you have not gotten a scope answer, you skipped Step 0. Go back and ask.

## Running the app

The whole stack (PostgreSQL + Spring backend + Vite frontend) starts with one
command from the repo root: `./start-dev.sh`. It reuses already-running services, so
it is safe to run to ensure everything is up. Targets once it reports ready:

- Frontend (what you test): `http://localhost:5173`
- Backend health / API (for diagnosing failures): `http://localhost:8080/actuator/health`, `http://localhost:8080/api/pokemon`

## Drive the UI ONLY through the Playwright MCP

Use the `mcp__playwright__*` tools — the standard set, **not** `mcp__playwright-isolated__*`.
Never substitute another method to exercise the UI.

| Instead of | Use |
|---|---|
| Writing a `*.spec.ts` Playwright file | `mcp__playwright__browser_*` tools directly |
| `curl` / fetch against `:8080` to "test the flow" | Navigate the real UI at `:5173` |
| Running `npm test` / Vitest and calling it QA | Click through the browser as a user |
| Asserting from a screenshot alone | `browser_snapshot` (accessibility tree) for state |

Core calls: `browser_navigate`, `browser_snapshot`, `browser_click`, `browser_type`,
`browser_fill_form`, `browser_wait_for`, `browser_take_screenshot`, and
`browser_console_messages` + `browser_network_requests` to catch JS errors and failed
API calls hiding behind a working-looking UI.

### Screenshots — where they go (MANDATORY)

Every screenshot you capture with `browser_take_screenshot` **must** be written
**inside the `qa/` folder with a `qa-` filename prefix** — i.e. the path
`qa/qa-<short-descriptive-name>.png` (e.g. `qa/qa-list-full.png`,
`qa/qa-detail-charizard.png`). Never write a screenshot to the repo root or anywhere
outside `qa/`, and never without the `qa-` prefix. These are the only image files the
project treats as disposable QA artifacts (they are git-ignored via `qa/qa-*.png`), so
a screenshot placed elsewhere leaks into the working tree as an untracked stray. Pass
the full `qa/qa-*.png` path explicitly as the screenshot filename — do not rely on a
default location.

## Files you produce

Create the `qa/` folder at the repo root if missing.

**`qa/use_cases.md`** — the catalog. Each case: id, flow, preconditions, steps,
expected result, and a PASS/FAIL status column. Create it by exploring the whole
frontend when it does not exist; keep it in sync whenever the site changes.

**Issue log — one per session:** `qa/{dd_MM_yyyy_HH-mm}-issues.md`
(e.g. `qa/13_08_2026_14-30-issues.md`). **Never put a `:` in the filename — Windows
forbids it**; use `HH-mm`, not `HH:mm`. Get the timestamp from the system date at the
start of the session. Each issue entry:

```
### [severity] Short title
- Steps to reproduce:
- Expected:
- Actual:
- Evidence: qa/qa-<short-descriptive-name>.png
```

## What to test (this app's real flows)

Routes: `/` (list), `/pokemon/:id` (detail), `/login`, `/register`, unknown → 404.
Read routes are public; **write actions (sync/edit/delete) require login.**

- **List `/`** — grid renders, pagination (next/prev/boundaries), empty state, error state when backend is down.
- **Detail `/pokemon/:id`** — stats, evolution list, weight shown in kg (hectograms ÷10), description with no leftover `\n`/`\f`; bad id → graceful 404/error.
- **Register `/register`** — username 3–50 chars, valid email, password 6–100; duplicate user; success path.
- **Login `/login`** — required username + password; wrong credentials error; JWT persists across reload; logout clears it.
- **Write actions (logged out → blocked/redirected; logged in → allowed):**
  - Sync/replicate (`POST /api/pokemon/sync`).
  - Edit (`PUT /api/pokemon/{id}`) — `localizedName` & `region` required, max 120; each tag max 50; 400 on bad input, 404 on missing id.
  - Delete — confirmation, list refresh, unauthorized when logged out.
- **Cross-cutting** — toasts/notifications, expired/invalid JWT on writes, and zero console errors / failed requests on every page.

## Red flags — STOP and correct

- Navigating a browser before Step 0 was answered.
- Reaching for `curl`, a `.spec.ts` file, or Vitest to "test the flow."
- Writing an issue filename containing `:`.
- Saving a screenshot outside `qa/`, or without the `qa-` prefix (it becomes an untracked stray).
- Marking a case PASS without having driven the UI for it.
