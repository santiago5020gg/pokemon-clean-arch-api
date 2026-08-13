<#
  One command to run the whole stack locally, in order, validating each layer
  before starting the next:

      1. PostgreSQL   -> docker compose up -d db     (waits until pg_isready)
      2. Backend      -> backend/mvnw spring-boot:run (waits until /actuator/health = UP)
      3. Frontend     -> frontend npm run dev         (waits until the dev server answers)
      4. Validation   -> GET /api/pokemon end-to-end, then announces the URL

  Requirements: Docker Desktop, Java 21, Node.js. No prior setup needed —
  dependencies install on first run.

  Usage (from the repo root):
      ./start-dev.ps1

  Press Ctrl+C to stop; the backend, frontend and the database container are all
  shut down on exit.
#>

$ErrorActionPreference = 'Stop'
$root = $PSScriptRoot
$backendHealth = 'http://localhost:8080/actuator/health'
$backendApi = 'http://localhost:8080/api/pokemon?page=0&size=1'
$frontendUrl = 'http://localhost:5173'

function Write-Step($msg) { Write-Host "  $msg" -ForegroundColor Cyan }
function Write-Ok($msg) { Write-Host "  [OK] $msg" -ForegroundColor Green }
function Write-Warn($msg) { Write-Host "  [!!] $msg" -ForegroundColor Yellow }
function Write-Err($msg) { Write-Host "  [XX] $msg" -ForegroundColor Red }

function Test-Url($url) {
    try { return (Invoke-WebRequest -Uri $url -UseBasicParsing -TimeoutSec 3).StatusCode -ge 200 }
    catch { return $false }
}

function Test-Tcp($tcpHost, $port) {
    try {
        $client = New-Object System.Net.Sockets.TcpClient
        $ok = $client.ConnectAsync($tcpHost, $port).Wait(1500)
        $client.Close()
        return $ok
    } catch { return $false }
}

$backend = $null
$frontend = $null
$script:startedDb = $false

function Stop-Everything {
    Write-Host ''
    Write-Step 'Stopping services...'
    foreach ($job in @($backend, $frontend)) {
        if ($job) { Stop-Job $job -ErrorAction SilentlyContinue; Remove-Job $job -Force -ErrorAction SilentlyContinue }
    }
    # Only stop the database if this script started it; leave a pre-existing one alone.
    if ($script:startedDb) {
        Push-Location $root
        docker compose stop db 2>&1 | Out-Null
        Pop-Location
    }
    Write-Ok 'All stopped.'
}

Write-Host ''
Write-Host '  ============================================' -ForegroundColor Magenta
Write-Host '   Pokedex - full stack dev launcher' -ForegroundColor Magenta
Write-Host '  ============================================' -ForegroundColor Magenta
Write-Host ''

# --- Preflight -------------------------------------------------------------
try { docker info 2>&1 | Out-Null; if ($LASTEXITCODE -ne 0) { throw } }
catch { Write-Err 'Docker is not running. Start Docker Desktop and try again.'; exit 1 }

try {
    # ---- 1. PostgreSQL ----------------------------------------------------
    # Reuse an already-running Postgres (e.g. one you run from your IDE); only
    # spin up the docker db when nothing is listening on 5432 (fresh machine).
    if (Test-Tcp 'localhost' 5432) {
        Write-Ok 'PostgreSQL already reachable on localhost:5432 - reusing it.'
    } else {
        Write-Step '[1/4] Starting PostgreSQL (docker compose up -d db)...'
        Push-Location $root
        docker compose up -d db | Out-Null
        Pop-Location
        $script:startedDb = $true

        $dbReady = $false
        for ($i = 0; $i -lt 30; $i++) {
            Push-Location $root
            docker compose exec -T db pg_isready -U pokedex 2>&1 | Out-Null
            $code = $LASTEXITCODE
            Pop-Location
            if ($code -eq 0) { $dbReady = $true; break }
            Start-Sleep -Seconds 2
        }
        if (-not $dbReady) { Write-Err 'PostgreSQL did not become ready.'; Stop-Everything; exit 1 }
        Write-Ok 'PostgreSQL is ready on localhost:5432.'
    }

    # ---- Ensure frontend deps --------------------------------------------
    if (-not (Test-Path (Join-Path $root 'frontend/node_modules'))) {
        Write-Step 'Installing frontend dependencies (first run)...'
        Push-Location (Join-Path $root 'frontend'); npm install; Pop-Location
    }

    # ---- 2. Backend -------------------------------------------------------
    if (Test-Url $backendHealth) {
        Write-Ok 'Backend already healthy on http://localhost:8080 - reusing it.'
    } else {
        Write-Step '[2/4] Starting the backend (Spring Boot)...'
        $backend = Start-Job -Name pokedex-backend -ScriptBlock {
            param($dir)
            Set-Location $dir
            if ($IsWindows) { & .\mvnw.cmd spring-boot:run } else { & ./mvnw spring-boot:run }
        } -ArgumentList (Join-Path $root 'backend')

        $backendReady = $false
        for ($i = 0; $i -lt 90; $i++) {
            if (Test-Url $backendHealth) { $backendReady = $true; break }
            if ((Get-Job $backend.Id).State -eq 'Failed') { break }
            Start-Sleep -Seconds 2
        }
        if (-not $backendReady) {
            Write-Err 'Backend did not become healthy. Recent logs:'
            Receive-Job $backend.Id | Select-Object -Last 25
            Stop-Everything; exit 1
        }
        Write-Ok 'Backend is healthy on http://localhost:8080.'
    }

    # ---- 3. Frontend ------------------------------------------------------
    if (Test-Url $frontendUrl) {
        Write-Ok 'Frontend already running - reusing it.'
    } else {
        Write-Step '[3/4] Starting the frontend (Vite)...'
        $frontend = Start-Job -Name pokedex-frontend -ScriptBlock {
            param($dir)
            Set-Location $dir
            npm run dev
        } -ArgumentList (Join-Path $root 'frontend')

        $frontendReady = $false
        for ($i = 0; $i -lt 30; $i++) {
            if (Test-Url $frontendUrl) { $frontendReady = $true; break }
            if ((Get-Job $frontend.Id).State -eq 'Failed') { break }
            Start-Sleep -Seconds 2
        }
        if (-not $frontendReady) {
            Write-Err 'Frontend did not start. Recent logs:'
            Receive-Job $frontend.Id | Select-Object -Last 25
            Stop-Everything; exit 1
        }
        Write-Ok 'Frontend dev server is up.'
    }

    # ---- 4. End-to-end validation ----------------------------------------
    Write-Step '[4/4] Validating the stack end-to-end...'
    if (Test-Url $backendApi) { Write-Ok 'API responded: GET /api/pokemon -> 200.' }
    else { Write-Warn 'API check did not return 200 (the app may still be usable).' }

    Write-Host ''
    Write-Host '  ============================================' -ForegroundColor Green
    Write-Host "   Ready! Open the app:  $frontendUrl" -ForegroundColor Green
    Write-Host '  ============================================' -ForegroundColor Green
    Write-Host ''
    if ($backend -or $frontend) {
        Write-Step 'Streaming logs (Ctrl+C to stop everything)...'
        Write-Host ''
        while ($true) {
            if ($backend) { Receive-Job $backend.Id }
            if ($frontend) { Receive-Job $frontend.Id }
            Start-Sleep -Seconds 1
        }
    } else {
        Write-Step 'All services were already running; nothing to stream. Press Enter to exit.'
        [void][System.Console]::ReadLine()
    }
}
finally {
    Stop-Everything
}
