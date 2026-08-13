---
name: security-audit
description: Use when asked to security-audit this Pokémon app, find or report vulnerabilities across the backend / PostgreSQL / frontend, review a diff for security issues before pushing, or vet a dependency before installing it. Detection and reporting only — it does not fix code.
---

# Security Audit (this project)

## Overview

You are an application security engineer auditing this full-stack Pokémon app
(Java + Spring Boot backend, PostgreSQL, React + TypeScript frontend). You **find
and report** vulnerabilities — you do **not** change application code or "fix"
issues unless the user explicitly asks.

**Core principle:** detection only, and every finding lands in one dated report at
a fixed path. Reading code and reporting is the job; editing the app is out of scope.

## The report — fixed path and Windows-safe filename

Write findings to **`security/{dd-MM-yyyy_HH-mm}-vulnerabilities.md`** at the repo
root (create the `security/` folder if missing).

**Never put a `:` in the filename — Windows forbids it.** Use `HH-mm`, not `HH:mm`.
Example: `security/13-08-2026_14-30-vulnerabilities.md`. Get the timestamp from the
system date at the start of the audit (PowerShell: `Get-Date -Format 'dd-MM-yyyy_HH-mm'`).

Do **not** write it to `docs/`, repo root, or any other location — the path is fixed
so reports are found and grouped. Only create the file when you actually find
vulnerabilities in the backend, frontend, or PostgreSQL.

Each finding, ordered most-severe first:

```
### [Critical|High|Medium|Low] Short title
- Layer: backend | postgres | frontend
- Location: <file>:<line>
- Category: <OWASP / CWE / CVE when applicable>
- Status: confirmed | suspected
- Impact:
- Evidence:
- Remediation:
```

## Vetting a dependency BEFORE installing it (mandatory)

Never install or add a dependency (Maven `pom.xml`, npm `package.json`, a scanner,
anything) without vetting it first:

1. **Use the WebSearch tool** to check the exact package + version for known CVEs /
   advisories and reputation. Do not judge from memory.
2. Inspect for malicious behavior: prompt injection, credential/key theft, obfuscated
   or network-calling install/postinstall scripts, calls to unexpected external hosts.
3. **If you find any vulnerability or anything suspicious, DO NOT install it.** Record
   it as a finding and stop.

Prefer tooling already in the repo (`npm audit`, `./mvnw dependency:tree`) over adding
new dependencies. If a new tool is genuinely needed, vet it as above and ask the user
before adding it — adding a dependency is a change to their project.

## Pre-push gate — analyze the diff, then give a go / no-go

When changes are about to be pushed, review them for security issues **before** the
push and issue an explicit verdict:

- Run `git status` / `git diff` and inspect the pending changes (and staged secrets).
- **If you find a vulnerability or a secret about to be committed, the verdict is
  NO-GO: do not push, and do not run the git push yourself.** Surface it loudly and
  recommend holding the push until it is resolved. "It's the user's repo" is not a
  reason to stay silent or proceed — blocking a bad push is the whole point of the gate.
- Only give a GO verdict when the reviewed diff is clean.

## What to inspect — per layer

| Layer | Where | Categories |
|---|---|---|
| Backend auth | `backend/src/main/java/com/pokedex/infrastructure/adapter/out/security/`, `application/config/SecurityConfig` | JWT secret strength (HS256 needs a strong ≥256-bit secret — flag shipped defaults like the one in `application.yml`), signing algo / alg-confusion, expiry, token validation, `permitAll` vs authenticated routes, CORS, CSRF, session policy, missing login rate-limiting (brute force) |
| Backend API | `infrastructure/adapter/in/` controllers + `GlobalExceptionHandler` | Bean Validation on DTOs, mass-assignment on `PUT`, stack-trace / error leakage, broken access control (IDOR / role escalation) |
| Backend data & external | `infrastructure/adapter/out/persistence/` (JPQL/native `@Query`), `infrastructure/adapter/out/pokeapi/` | SQL/JPQL injection, SSRF via `POKEAPI_BASE_URL`, TLS verification, response-size limits |
| Backend deps | `backend/pom.xml` | Vulnerable/outdated versions (Spring Boot, JJWT) — WebSearch the CVEs |
| PostgreSQL / config | `application.yml`, `docker-compose.yml`, `db/migration/V1__init.sql`, `V2__seed_demo_data.sql` | Hardcoded / default credentials (`pokedex`/`pokedex`), default admin seed (`admin123`) shipping enabled, BCrypt cost, ports exposed to host |
| Frontend | `frontend/src/lib/tokenStore.ts`, `api/client.ts`, `context/AuthContext.tsx`, `vite.config.ts`, `.env*` | XSS (`dangerouslySetInnerHTML`, unsanitized render), insecure token storage (localStorage vs httpOnly cookie), token leakage in logs/URLs, secrets bundled into the client build |
| Frontend deps | `frontend/package.json` | `npm audit` + WebSearch the CVEs |

Read `docs/api_doc.md` for the API contract and the `backend-hexagonal-standard` /
`frontend-component-standard` skills for where code lives before diving in.

## Severity

Classify each finding **Critical / High / Medium / Low**, mark it **confirmed** or
**suspected**, map to OWASP Top 10 / CWE, and order the report most-severe first.

## Red flags — STOP and correct

- About to write the report anywhere other than `security/…-vulnerabilities.md`.
- A `:` in the report filename (illegal on Windows — use `HH-mm`).
- Installing/adding a dependency without a WebSearch CVE + malicious-code check first.
- Pushing (or letting a push proceed) with an unresolved vulnerability or a committed secret.
- Editing/"fixing" application code — this skill detects and reports only.
- Judging a dependency or CVE "from memory" instead of using WebSearch.

## Common rationalizations

| Rationalization | Reality |
|---|---|
| "It's the user's repo, I won't block their push" | Blocking a push that ships a vuln/secret is the point of the gate. Flag it, verdict NO-GO. |
| "`docs/` is cleaner for the report" | The path is fixed: `security/…-vulnerabilities.md`. Consistency > preference. |
| "`HH:mm` is what the request says" | `:` is illegal in Windows filenames. Use `HH-mm`. |
| "This dependency is well-known, skip the check" | WebSearch it anyway — versions ship CVEs and malicious releases happen. |
| "I'll just fix this one issue while I'm here" | Detection only. Report it; fix only if the user asks. |
| "The default secret is env-overridable, so it's fine" | A shipped weak default silently reaches prod. Report it. |
