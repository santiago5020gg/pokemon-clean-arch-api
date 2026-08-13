#!/usr/bin/env bash
#
# Starts backend + frontend for local development and announces the frontend URL
# once BOTH are ready. POSIX counterpart of start-dev.ps1 (for macOS/Linux/Git Bash).
#
#   Backend:  http://localhost:8080  (backend/mvnw spring-boot:run)
#   Frontend: http://localhost:5173  (Vite dev server)
#
# Requires Java 21 + a reachable PostgreSQL (docker compose up -d db) and Node.js.
# Usage (from repo root):  ./start-dev.sh
set -euo pipefail

ROOT="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
BACKEND_HEALTH="http://localhost:8080/actuator/health"
FRONTEND_URL="http://localhost:5173"

echo ""
echo "  Pokedex - starting backend + frontend..."

if [ ! -d "$ROOT/frontend/node_modules" ]; then
  echo "  Installing frontend dependencies (first run)..."
  (cd "$ROOT/frontend" && npm install)
fi

# Launch both; track PIDs for cleanup.
(cd "$ROOT/backend" && ./mvnw spring-boot:run) &
BACKEND_PID=$!
(cd "$ROOT/frontend" && npm run dev) &
FRONTEND_PID=$!

cleanup() {
  echo ""
  echo "  Stopping services..."
  kill "$BACKEND_PID" "$FRONTEND_PID" 2>/dev/null || true
}
trap cleanup EXIT INT TERM

wait_for() {
  local name="$1" url="$2" timeout="$3" elapsed=0
  echo "  Waiting for $name..."
  until curl -sf -o /dev/null "$url"; do
    sleep 2; elapsed=$((elapsed + 2))
    if [ "$elapsed" -ge "$timeout" ]; then
      echo "  $name did not become ready within ${timeout}s."; return 1
    fi
  done
  echo "  $name is ready."
}

wait_for "backend"  "$BACKEND_HEALTH" 120
wait_for "frontend" "$FRONTEND_URL"   60

echo ""
echo "  ============================================"
echo "   Ready! Open the app:  $FRONTEND_URL"
echo "  ============================================"
echo ""
echo "  Streaming logs (Ctrl+C to stop)..."
wait
