# Pokedex API — smoke test (no Postman needed)
# Run:  powershell -ExecutionPolicy Bypass -File .\docs\smoke-test.ps1
# Requires the app running on http://localhost:8080 (docker compose up  OR  cd backend; .\mvnw.cmd spring-boot:run)

$base = 'http://localhost:8080'

function Call($method, $path, $body, $token) {
  $headers = @{}
  if ($token) { $headers['Authorization'] = "Bearer $token" }
  $params = @{ Method = $method; Uri = "$base$path"; Headers = $headers }
  if ($null -ne $body) { $params['Body'] = ($body | ConvertTo-Json -Compress); $params['ContentType'] = 'application/json' }
  try {
    $r = Invoke-WebRequest @params -UseBasicParsing
    return @{ status = [int]$r.StatusCode; body = $r.Content }
  } catch {
    $resp = $_.Exception.Response
    if ($resp) { return @{ status = [int]$resp.StatusCode; body = '' } }
    return @{ status = -1; body = $_.Exception.Message }
  }
}

function Show($label, $result, $expected) {
  $ok = $result.status -eq $expected
  $mark = if ($ok) { 'OK ' } else { 'XX ' }
  $color = if ($ok) { 'Green' } else { 'Red' }
  Write-Host ("{0} {1,-42} -> {2}  (expected {3})" -f $mark, $label, $result.status, $expected) -ForegroundColor $color
}

Write-Host "== Pokedex API smoke test ==" -ForegroundColor Cyan

# Auth
$login = Call 'POST' '/api/auth/login' @{ username='admin'; password='admin123' } $null
Show 'POST /api/auth/login (admin)' $login 200
$token = ($login.body | ConvertFrom-Json).token

Show 'POST /api/auth/login (bad creds)'      (Call 'POST' '/api/auth/login' @{ username='admin'; password='nope' } $null) 401
Show 'POST /api/auth/register'               (Call 'POST' '/api/auth/register' @{ username='trainer2'; email='trainer2@pokedex.io'; password='pass1234' } $null) 201

# US01 / US02
Show 'GET  /api/pokemon (list, US01)'        (Call 'GET' '/api/pokemon?page=0&size=20' $null $null) 200
Show 'GET  /api/pokemon/1 (detail, US02)'    (Call 'GET' '/api/pokemon/1' $null $null) 200
Show 'GET  /api/pokemon/99999 (missing)'     (Call 'GET' '/api/pokemon/99999' $null $null) 404

# US03
Show 'POST /api/pokemon/sync (US03)'         (Call 'POST' '/api/pokemon/sync' @{ limit=5; offset=0 } $token) 201

# US04
Show 'PUT  /api/pokemon/1 (update, US04)'    (Call 'PUT' '/api/pokemon/1' @{ localizedName='Bulbasaur ES'; region='Kanto'; internalTags=@('starter') } $token) 200
Show 'PUT  /api/pokemon/99999 (missing)'     (Call 'PUT' '/api/pokemon/99999' @{ localizedName='Valid Name'; region='X' } $token) 404
Show 'PUT  /api/pokemon/1 (invalid payload)' (Call 'PUT' '/api/pokemon/1' @{ localizedName='' } $token) 400
Show 'PUT  /api/pokemon/1 (no token)'        (Call 'PUT' '/api/pokemon/1' @{ localizedName='x' } $null) 401
Show 'DEL  /api/pokemon/7 (delete)'          (Call 'DELETE' '/api/pokemon/7' $null $token) 204

# Health
Show 'GET  /actuator/health'                 (Call 'GET' '/actuator/health' $null $null) 200

Write-Host "== done ==" -ForegroundColor Cyan
