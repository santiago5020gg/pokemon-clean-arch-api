# QA — Use Cases (Pokédex web app)

End-to-end use-case catalog for the React frontend (`http://localhost:5173`) backed
by the Spring API (`http://localhost:8080`). Cases are exercised as an end user with
the **Playwright MCP** browser. Keep this file in sync whenever the site changes.

- **Stack launch:** `./start-dev.sh` (or `start-dev.ps1`) from the repo root.
- **Demo credentials (seed):** `admin` / `admin123`.
- **Routes:** `/` (list), `/pokemon/:id` (detail), `/login`, `/register`, unknown → 404.
- **Auth rule:** read routes are public; write actions (sync / edit / delete) require login.

**Status legend:** PASS · FAIL · NOT RUN (not exercised this session).

Last run: **2026-08-13** — full regression. See `qa/13_08_2026_08-36-issues.md`.

---

## 1. Pokémon list (`/`)

| ID | Case | Steps | Expected | Status |
|----|------|-------|----------|--------|
| LIST-01 | List renders | Open `/` | Grid shows replicated Pokémon cards (#id, sprite, name, category, abilities, weight in kg) | PASS |
| LIST-02 | Filter box narrows results | Type `char` in "Search by name…" | Only matching cards remain (client-side, current page only — label is "Filter this page") | PASS |
| LIST-03 | Card opens detail | Click a Pokémon card | Navigates to `/pokemon/:id` | PASS |
| LIST-04 | Empty filter restores list | Clear the filter box | All current-page cards shown again | PASS |
| LIST-05 | Weight formatting | Inspect card weight | Shown in kg (hectograms ÷10), e.g. Bulbasaur 6.9 kg | PASS |
| LIST-06 | Pagination | Have >1 page of data, use next/prev | Page changes; boundaries disable correctly | NOT RUN (≤20 items after sync; no pagination control shown) |
| LIST-07 | Backend-down error state | Stop backend, open `/` | Graceful error state (not a blank page) | NOT RUN |

## 2. Pokémon detail (`/pokemon/:id`)

| ID | Case | Steps | Expected | Status |
|----|------|-------|----------|--------|
| DET-01 | Detail renders | Open `/pokemon/1` | Shows #id, name, region, description, base stats (HP/Atk/Def/SpA/SpD/Spe) with meters, evolutions | PASS |
| DET-02 | Description cleaned | Read description | No leftover `\n` / `\f` control chars | PASS ("A strange seed was planted on its back at birth.") |
| DET-03 | Evolution lineage | Read Evolutions | Ordered chain (Bulbasaur → Ivysaur → Venusaur) | PASS |
| DET-04 | Regional data shown | Scroll to "Regional data" | Localized name, Region, Internal tags | PASS |
| DET-05 | Not-found id | Open `/pokemon/99999` | "Pokémon not found" message with guidance (no crash) | PASS |
| DET-06 | Edit/Delete gated | Open detail while logged out | Edit/Delete controls not available/usable until login | PASS (controls appear only when authenticated) |

## 3. Authentication — Login (`/login`)

| ID | Case | Steps | Expected | Status |
|----|------|-------|----------|--------|
| LOGIN-01 | Required fields | Submit empty form | "Username is required" / "Password is required"; fields flagged invalid | PASS |
| LOGIN-02 | Wrong credentials | `nouser` / `wrongpass123` → Sign in | "Invalid username or password" alert; stays on `/login` | PASS |
| LOGIN-03 | Valid login | `admin` / `admin123` → Sign in | Redirect to `/`; header shows "Sign out" | PASS |
| LOGIN-04 | Link to register | Click "Create one" | Navigates to `/register` | PASS |

## 4. Authentication — Register (`/register`)

| ID | Case | Steps | Expected | Status |
|----|------|-------|----------|--------|
| REG-01 | Username min length | Username `ab` → submit | "At least 3 characters" | PASS |
| REG-02 | Email format | Email `bad` → submit | "Enter a valid email" | PASS |
| REG-03 | Password min length | Password `123` → submit | "At least 6 characters" | PASS |
| REG-04 | Successful register | Valid unique user → Create account | Account created, auto-login, redirect to `/` (header "Sign out") | PASS |
| REG-05 | Duplicate username | Register an existing username | Server error surfaced to user | NOT RUN |
| REG-06 | Username/password max length | Username >50 / password >100 | "Max 50/100 characters" | NOT RUN |

## 5. Session / auth state

| ID | Case | Steps | Expected | Status |
|----|------|-------|----------|--------|
| SESS-01 | Session persists on reload | Log in, reload `/` | Still authenticated (header "Sign out") | PASS |
| SESS-02 | Sign out | Click "Sign out" | Returns to logged-out state (header "Sign in") | PASS |
| SESS-03 | Write action gated | Logged out, click "Replicate from PokeAPI" | Redirect to `/login` | PASS |
| SESS-04 | Expired/invalid JWT on write | Tamper/expire token, attempt a write | Graceful re-auth prompt / error | NOT RUN |

## 6. Write actions (require login)

| ID | Case | Steps | Expected | Status |
|----|------|-------|----------|--------|
| SYNC-01 | Replicate from PokeAPI | Logged in, click "Replicate from PokeAPI" | List populated with replicated Pokémon (synced #001–#020) | PASS |
| SYNC-02 | Sync feedback | Observe UI during/after sync | Progress/success feedback shown | FAIL — no confirmation/spinner/toast (see ISSUE-02) |
| EDIT-01 | Open edit form | Detail → "Edit" | Inline form for localizedName / region / internal tags | PASS |
| EDIT-02 | Required validation | Clear localizedName & region → Save | "Localized name is required" / "Region is required" | PASS |
| EDIT-03 | Valid save persists | Change fields → Save changes | Read view + page title reflect new values | PASS |
| EDIT-04 | Round-trip revert | Edit again → restore originals → Save | Values restored | PASS |
| EDIT-05 | Max length (120/50) | localizedName/region >120, tag >50 | "Max 120 characters" / "Each tag must be 50 characters or fewer" | NOT RUN |
| DEL-01 | Delete confirmation | Detail → "Delete" | Two-step inline confirm ("Cancel" / "Confirm delete") | PASS |
| DEL-02 | Delete executes | "Confirm delete" | Record removed, list refreshes | NOT RUN (cancelled to preserve seed data) |

## 7. Cross-cutting

| ID | Case | Expected | Status |
|----|------|----------|--------|
| X-01 | No unexpected console errors | Console clean on each page | PASS — only expected browser logs for handled 401 (bad login) / 404 (missing id) |
| X-02 | Network errors handled | Failed API calls surface as UI messages, not blank screens | PASS (login error alert, not-found state) |
| X-03 | Filter scope clarity | "Filter this page" filters only the loaded page | PASS (by design; UX note in ISSUE-03) |
