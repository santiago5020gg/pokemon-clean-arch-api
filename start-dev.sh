#!/usr/bin/env bash
#
# One command to run the whole stack locally, in order, validating each layer
# before starting the next (POSIX counterpart of start-dev.ps1):
#
#   1. PostgreSQL -> docker compose up -d db      (waits until pg_isready)
#   2. Backend    -> backend/mvnw spring-boot:run (waits until /actuator/health)
#   3. Frontend   -> frontend npm run dev         (waits until the dev server answers)
#   4. Validation -> GET /api/pokemon end-to-end, then announces the URL
#
# Requires Docker, Java 21 and Node.js. Usage (from repo root):  ./start-dev.sh
# Ctrl+C stops the backend, frontend and the database container.
set -euo pipefail

ROOT="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
BACKEND_HEALTH="http://localhost:8080/actuator/health"
BACKEND_API="http://localhost:8080/api/pokemon?page=0&size=1"
FRONTEND_URL="http://localhost:5173"

step() { echo "  $1"; }

STARTED_DB=false

cleanup() {
  echo ""
  step "Stopping services..."
  [ -n "${BACKEND_PID:-}" ] && kill "$BACKEND_PID" 2>/dev/null || true
  [ -n "${FRONTEND_PID:-}" ] && kill "$FRONTEND_PID" 2>/dev/null || true
  # Only stop the database if this script started it.
  if [ "$STARTED_DB" = true ]; then
    (cd "$ROOT" && docker compose stop db >/dev/null 2>&1) || true
  fi
  echo "  [OK] All stopped."
}
trap cleanup EXIT INT TERM

tcp_open() { (exec 3<>"/dev/tcp/$1/$2") 2>/dev/null && exec 3>&- && return 0 || return 1; }

echo ""
echo "  ============================================"
echo "   Pokedex - full stack dev launcher"
echo "  ============================================"

# Preflight
if ! docker info >/dev/null 2>&1; then
  echo "  [XX] Docker is not running. Start Docker and try again."; exit 1
fi

# 1. PostgreSQL — reuse an already-running instance, else start the docker db.
if tcp_open localhost 5432; then
  echo "  [OK] PostgreSQL already reachable on localhost:5432 - reusing it."
else
  step "[1/4] Starting PostgreSQL (docker compose up -d db)..."
  (cd "$ROOT" && docker compose up -d db >/dev/null)
  STARTED_DB=true
  db_ready=false
  for _ in $(seq 1 30); do
    if (cd "$ROOT" && docker compose exec -T db pg_isready -U pokedex >/dev/null 2>&1); then
      db_ready=true; break
    fi
    sleep 2
  done
  $db_ready || { echo "  [XX] PostgreSQL did not become ready."; exit 1; }
  echo "  [OK] PostgreSQL is ready on localhost:5432."
fi

# Ensure frontend deps
if [ ! -d "$ROOT/frontend/node_modules" ]; then
  step "Installing frontend dependencies (first run)..."
  (cd "$ROOT/frontend" && npm install)
fi

# 2. Backend — reuse if already healthy, else start it.
if curl -sf -o /dev/null "$BACKEND_HEALTH"; then
  echo "  [OK] Backend already healthy on http://localhost:8080 - reusing it."
else
  step "[2/4] Starting the backend (Spring Boot)..."
  (cd "$ROOT/backend" && ./mvnw spring-boot:run) &
  BACKEND_PID=$!
  backend_ready=false
  for _ in $(seq 1 90); do
    if curl -sf -o /dev/null "$BACKEND_HEALTH"; then backend_ready=true; break; fi
    sleep 2
  done
  $backend_ready || { echo "  [XX] Backend did not become healthy."; exit 1; }
  echo "  [OK] Backend is healthy on http://localhost:8080."
fi

# 3. Frontend — reuse if already running, else start it.
if curl -sf -o /dev/null "$FRONTEND_URL"; then
  echo "  [OK] Frontend already running - reusing it."
else
  step "[3/4] Starting the frontend (Vite)..."
  (cd "$ROOT/frontend" && npm run dev) &
  FRONTEND_PID=$!
  frontend_ready=false
  for _ in $(seq 1 30); do
    if curl -sf -o /dev/null "$FRONTEND_URL"; then frontend_ready=true; break; fi
    sleep 2
  done
  $frontend_ready || { echo "  [XX] Frontend did not start."; exit 1; }
  echo "  [OK] Frontend dev server is up."
fi

# 4. Validation
step "[4/4] Validating the stack end-to-end..."
if curl -sf -o /dev/null "$BACKEND_API"; then
  echo "  [OK] API responded: GET /api/pokemon -> 200."
else
  echo "  [!!] API check did not return 200 (the app may still be usable)."
fi

echo ""
echo "  ============================================"
echo "   Ready! Open the app:  $FRONTEND_URL"
echo "  ============================================"
echo ""
if [ -n "${BACKEND_PID:-}" ] || [ -n "${FRONTEND_PID:-}" ]; then
  step "Streaming logs (Ctrl+C to stop everything)..."
  wait
else
  step "All services were already running; nothing to launch."
fi
