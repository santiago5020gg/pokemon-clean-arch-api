<#
  Starts the full stack for local development and announces the frontend URL
  once BOTH the backend and the frontend are actually ready to open.

    - Backend:  Spring Boot on http://localhost:8080  (backend/mvnw spring-boot:run)
    - Frontend: Vite dev server on http://localhost:5173

  Requirements:
    - Java 21 and the backend's PostgreSQL reachable (see backend/application.yml).
      The quickest way to get the DB is:  docker compose up -d db
    - Node.js + a prior `npm install` in ./frontend

  Usage (from repo root):
      ./start-dev.ps1
  Press Ctrl+C to stop; both child processes are cleaned up on exit.
#>

$ErrorActionPreference = 'Stop'
$root = $PSScriptRoot
$backendUrl = 'http://localhost:8080/actuator/health'
$frontendUrl = 'http://localhost:5173'

Write-Host ''
Write-Host '  Pokedex - starting backend + frontend...' -ForegroundColor Cyan
Write-Host ''

# Ensure frontend dependencies are present.
if (-not (Test-Path (Join-Path $root 'frontend/node_modules'))) {
    Write-Host '  Installing frontend dependencies (first run)...' -ForegroundColor Yellow
    Push-Location (Join-Path $root 'frontend')
    npm install
    Pop-Location
}

# Launch backend and frontend as background jobs.
$backend = Start-Job -Name pokedex-backend -ScriptBlock {
    param($dir)
    Set-Location $dir
    if ($IsWindows) { & .\mvnw.cmd spring-boot:run } else { & ./mvnw spring-boot:run }
} -ArgumentList (Join-Path $root 'backend')

$frontend = Start-Job -Name pokedex-frontend -ScriptBlock {
    param($dir)
    Set-Location $dir
    npm run dev
} -ArgumentList (Join-Path $root 'frontend')

function Test-Url($url) {
    try { (Invoke-WebRequest -Uri $url -UseBasicParsing -TimeoutSec 3).StatusCode -ge 200 }
    catch { $false }
}

function Wait-For($name, $url, $timeoutSec) {
    Write-Host "  Waiting for $name..." -ForegroundColor Gray
    $deadline = (Get-Date).AddSeconds($timeoutSec)
    while ((Get-Date) -lt $deadline) {
        if (Test-Url $url) { Write-Host "  $name is ready." -ForegroundColor Green; return $true }
        if ((Get-Job -Id $backend.Id).State -eq 'Failed' -or (Get-Job -Id $frontend.Id).State -eq 'Failed') {
            return $false
        }
        Start-Sleep -Seconds 2
    }
    Write-Host "  $name did not become ready within $timeoutSec s." -ForegroundColor Red
    return $false
}

try {
    $backendOk = Wait-For 'backend'  $backendUrl  120
    $frontendOk = Wait-For 'frontend' $frontendUrl 60

    Write-Host ''
    if ($backendOk -and $frontendOk) {
        Write-Host '  ============================================' -ForegroundColor Green
        Write-Host "   Ready! Open the app:  $frontendUrl" -ForegroundColor Green
        Write-Host '  ============================================' -ForegroundColor Green
    } else {
        Write-Host '  One or more services failed to start. Recent logs:' -ForegroundColor Red
        Receive-Job -Id $backend.Id  | Select-Object -Last 20
        Receive-Job -Id $frontend.Id | Select-Object -Last 20
    }
    Write-Host ''
    Write-Host '  Streaming logs (Ctrl+C to stop)...' -ForegroundColor Gray

    # Stream logs until interrupted.
    while ($true) {
        Receive-Job -Id $backend.Id
        Receive-Job -Id $frontend.Id
        Start-Sleep -Seconds 1
    }
}
finally {
    Write-Host ''
    Write-Host '  Stopping services...' -ForegroundColor Yellow
    Stop-Job -Id $backend.Id, $frontend.Id -ErrorAction SilentlyContinue
    Remove-Job -Id $backend.Id, $frontend.Id -Force -ErrorAction SilentlyContinue
}
