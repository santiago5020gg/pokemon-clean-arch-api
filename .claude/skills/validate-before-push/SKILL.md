---
name: validate-before-push
description: Use when about to push, merge-and-push, or otherwise ship code to ANY git branch in this project — i.e. immediately before running git push — including quick one-line fixes, changes whose tests "already passed", and pushes made under time pressure. Not for validation at any other time.
---

# Validate Before Push (this project)

## Overview

You are the delivery gate for this full-stack Pokémon app. **No code reaches a
remote branch until backend standards, frontend standards, security, and QA all
pass.** You do not run the push while anything is red — you fix the failures and
re-run until green.

**Core principle:** running Gitflow correctly and having tests pass is NOT the
same as validating. This gate runs the four project skills as subagents; passing
tests locally is not a substitute for any of them.

**Scope — pre-push ONLY.** Run this gate only immediately before a `git push`
(to `develop`, a `feature/*` branch, `master`, anywhere). It is not a CI step, not
a per-commit check, not a general review. If no push is about to happen, do not run it.

## The four validators

| Skill | Validates | Run when |
|---|---|---|
| `backend-hexagonal-standard` | Java/Spring Boot standards under `backend/` | the diff touches `backend/` |
| `frontend-component-standard` | React/TS standards under `frontend/` | the diff touches `frontend/` |
| `security-audit` | Vulnerabilities across backend / PostgreSQL / frontend | **always** |
| `qa-playwright-mcp` | End-to-end / UI behavior as a real user | **always** |

Skip a *code-standard* skill only when that layer has zero changes in the diff
(`git diff` proves it). Security and QA always run.

## The gate — mandatory order

1. **In parallel**, dispatch one background subagent per applicable validator from
   `backend-hexagonal-standard`, `frontend-component-standard`, and `security-audit`.
   These are independent — run them concurrently, do not serialize.
2. **Collect the three reports.** If any reports a failure, **fix it yourself**,
   then re-run *only the affected* validator. Repeat until all three are green.
3. **Only after steps 1–3 are all green**, dispatch `qa-playwright-mcp`. Testing a
   build that still violates standards or ships a vulnerability wastes the run.
4. **If QA reports any issue, fix it and restart from step 1** — a code fix can
   re-break a standard or introduce a vulnerability.
5. **Verdict.**
   - **All four green → GO.** Create the approval marker so the push is unblocked:
     write the file **`.claude/.push-approved`** (any content — an empty file or
     `GO` is fine) **using your file-writing tool (Write), NOT a shell command.**
     A `git push` is Bash, so it hits the gate; a Bash write into `.claude/` is also
     intercepted, so create the marker with Write. Then the push may proceed.
   - **Anything red → NO-GO:** do not push, do not run `git push` yourself, and do
     **not** create the marker. State exactly what is failing.

Each skill's own standard is authoritative for what "passing" means. Do not relax a
skill's criteria, sample instead of covering, or declare green without its report.

## How the push is enforced (marker + hook)

A `PreToolUse` hook (`.claude/settings.json` → `.claude/hooks/validate-before-push-gate.sh`)
intercepts every `git push` and **blocks it unless a fresh `.claude/.push-approved`
marker exists** — fresh meaning modified within the last 30 minutes (judged by the
file's mtime, so its contents don't matter). This gate creates that marker only on an
all-green GO (step 5). The hook **consumes** (deletes) the marker on the allowed push,
so **each push requires a new GO** — one validation run unlocks exactly one push.

- Only create `.claude/.push-approved` as the result of a real GO. Creating it to get
  past the hook without a green run defeats the entire gate.
- Create it with the **Write** tool, not a shell command — the hook only guards Bash,
  and a Bash write into `.claude/` is itself intercepted.
- If a push is blocked, the fix is to run this gate — not to hand-create the marker.
- The marker is machine-local ephemeral state (git-ignored); `settings.json` and the
  hook script are committed so the gate applies to everyone.

## Red flags — STOP, you are about to skip the gate

- About to `git push` without having run all four validators.
- "It's a one-line / trivial change, it doesn't need the full gate."
- "Tests already passed (earlier / locally), so it's validated."
- "We're late — skip QA / security / the full check just this once."
- "Gitflow + `npm test` / `mvnw test` is enough to push."
- "I'll push now and validate afterwards."
- Running `git push` while any validator is red.
- Writing `.claude/.push-approved` by hand (or before an all-green GO) to get past the hook.
- Running this gate when no push is imminent (out of scope).

## Common rationalizations

| Rationalization | Reality |
|---|---|
| "One-line fix, skip the gate" | Typos in labels and error strings still ship XSS, break QA flows, and violate standards. Size ≠ safety. The gate runs. |
| "Tests already passed earlier" | Passing unit tests is not a security audit, a standards review, or E2E QA. Different checks, different failures. |
| "We're late, skip full verification" | Time pressure is exactly when regressions ship. The gate is the point of a pre-push moment; a broken push costs more than the gate. |
| "Gitflow (branch + `--no-ff`) means it's ready" | Gitflow controls *how* code merges, not *whether* it's correct/secure. Orthogonal to this gate. |
| "I'll validate after pushing" | Once it's on the remote branch it's shared. Validate before, not after. |
| "Only frontend changed, but run everything anyway / nothing" | Skip a code-standard skill only if its layer has zero diff. Security and QA always run. |
| "A validator flagged something minor, I'll push anyway" | Any unresolved failure = NO-GO. Fix it or it doesn't ship. |
