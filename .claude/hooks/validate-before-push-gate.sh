#!/usr/bin/env bash
# validate-before-push gate: allow `git push` only when a fresh GO marker exists.
# The marker (.claude/.push-approved) is created by the validate-before-push skill
# on an all-green verdict, and consumed here so each push needs a new validation.
# Freshness is judged by the marker's modification time (30 min), so the file's
# contents do not matter — an empty file created by the Write tool is enough.
cat >/dev/null 2>&1   # drain the hook-input JSON on stdin
ROOT="${CLAUDE_PROJECT_DIR:-.}"
M="$ROOT/.claude/.push-approved"

if [ -f "$M" ]; then
  fresh=$(find "$M" -mmin -30 2>/dev/null)
  rm -f "$M"
  if [ -n "$fresh" ]; then
    printf '{"hookSpecificOutput":{"hookEventName":"PreToolUse","permissionDecision":"allow","permissionDecisionReason":"validate-before-push GO marker present and fresh; consumed."}}'
    exit 0
  fi
fi
printf '{"hookSpecificOutput":{"hookEventName":"PreToolUse","permissionDecision":"deny","permissionDecisionReason":"Push blocked: no fresh validate-before-push GO verdict. Run the validate-before-push skill (it runs backend + frontend + security in parallel, then QA); on an all-green GO it creates .claude/.push-approved. Then retry the push."}}'
exit 0
