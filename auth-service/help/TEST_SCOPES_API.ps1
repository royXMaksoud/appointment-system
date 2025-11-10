# =====================================================
# PowerShell Test Script for /auth/me/permissions API
# Usage: .\TEST_SCOPES_API.ps1
# =====================================================

# Configuration
$BASE_URL = "http://localhost:6060"
$AUTH_ENDPOINT = "/auth/login"
$PERMS_ENDPOINT = "/auth/me/permissions"

# Credentials (✏️ CHANGE THESE!)
$USERNAME = "admin@example.com"
$PASSWORD = "your-password"

Write-Host "======================================================" -ForegroundColor Cyan
Write-Host "🧪 Testing Scopes in /auth/me/permissions API" -ForegroundColor Cyan
Write-Host "======================================================" -ForegroundColor Cyan

# Step 1: Login
Write-Host ""
Write-Host "Step 1: Logging in..." -ForegroundColor Yellow

$loginBody = @{
    username = $USERNAME
    password = $PASSWORD
} | ConvertTo-Json

try {
    $loginResponse = Invoke-RestMethod -Uri "$BASE_URL$AUTH_ENDPOINT" `
        -Method Post `
        -ContentType "application/json" `
        -Body $loginBody `
        -ErrorAction Stop
    
    $token = $loginResponse.token
    
    if (-not $token) {
        Write-Host "❌ Login failed! Token not found in response." -ForegroundColor Red
        Write-Host "Response: $($loginResponse | ConvertTo-Json)" -ForegroundColor Red
        exit 1
    }
    
    Write-Host "✅ Login successful!" -ForegroundColor Green
    Write-Host "Token: $($token.Substring(0, [Math]::Min(20, $token.Length)))..." -ForegroundColor Gray
} catch {
    Write-Host "❌ Login failed! Error: $($_.Exception.Message)" -ForegroundColor Red
    exit 1
}

# Step 2: Fetch permissions (cached)
Write-Host ""
Write-Host "Step 2: Fetching permissions (cached)..." -ForegroundColor Yellow

$headers = @{
    "Authorization" = "Bearer $token"
    "Accept" = "application/json"
}

try {
    $permsCached = Invoke-RestMethod -Uri "$BASE_URL$PERMS_ENDPOINT`?force=false" `
        -Method Get `
        -Headers $headers `
        -ErrorAction Stop
    
    Write-Host "Cached response - Systems count: $($permsCached.systems.Count)" -ForegroundColor Gray
} catch {
    Write-Host "⚠️  Warning: Could not fetch cached permissions: $($_.Exception.Message)" -ForegroundColor Yellow
}

# Step 3: Fetch permissions (force refresh)
Write-Host ""
Write-Host "Step 3: Fetching permissions (force refresh)..." -ForegroundColor Yellow

try {
    $permsFresh = Invoke-RestMethod -Uri "$BASE_URL$PERMS_ENDPOINT`?force=true" `
        -Method Get `
        -Headers $headers `
        -ErrorAction Stop
    
    # Save to file
    $permsFresh | ConvertTo-Json -Depth 10 | Out-File -FilePath "permissions_response.json" -Encoding UTF8
    Write-Host "✅ Saved full response to: permissions_response.json" -ForegroundColor Green
    
    Write-Host "Systems count: $($permsFresh.systems.Count)" -ForegroundColor Gray
    Write-Host "Generated at: $($permsFresh.generatedAt)" -ForegroundColor Gray
} catch {
    Write-Host "❌ Failed to fetch permissions: $($_.Exception.Message)" -ForegroundColor Red
    exit 1
}

# Step 4: Analyze scopes
Write-Host ""
Write-Host "Step 4: Analyzing scopes..." -ForegroundColor Yellow

$totalActions = 0
$actionsWithScopes = 0
$totalScopes = 0
$scopeDetails = @()

foreach ($system in $permsFresh.systems) {
    foreach ($section in $system.sections) {
        foreach ($action in $section.actions) {
            $totalActions++
            
            if ($action.scopes -and $action.scopes.Count -gt 0) {
                $actionsWithScopes++
                $totalScopes += $action.scopes.Count
                
                $scopeDetails += [PSCustomObject]@{
                    System = $system.name
                    Section = $section.name
                    Action = $action.name
                    ActionEffect = $action.effect
                    ScopeCount = $action.scopes.Count
                    Scopes = ($action.scopes | ForEach-Object { "$($_.scopeValueName) ($($_.effect))" }) -join ", "
                }
            }
        }
    }
}

Write-Host "Total actions: $totalActions" -ForegroundColor Cyan
Write-Host "Actions with scopes: $actionsWithScopes" -ForegroundColor Cyan
Write-Host "Total scopes: $totalScopes" -ForegroundColor Cyan

# Step 5: Display results
Write-Host ""
Write-Host "======================================================" -ForegroundColor Cyan
Write-Host "📊 Test Results" -ForegroundColor Cyan
Write-Host "======================================================" -ForegroundColor Cyan

if ($actionsWithScopes -gt 0) {
    Write-Host "✅ SUCCESS: Found scopes in $actionsWithScopes actions!" -ForegroundColor Green
    Write-Host ""
    Write-Host "Sample actions with scopes:" -ForegroundColor Yellow
    Write-Host "------------------------------------------------------" -ForegroundColor Gray
    
    $scopeDetails | Select-Object -First 10 | Format-Table -AutoSize
    
    if ($scopeDetails.Count -gt 10) {
        Write-Host "... and $($scopeDetails.Count - 10) more actions with scopes" -ForegroundColor Gray
    }
    
    Write-Host ""
    Write-Host "Scope details (first 5 actions):" -ForegroundColor Yellow
    Write-Host "------------------------------------------------------" -ForegroundColor Gray
    
    $count = 0
    foreach ($detail in $scopeDetails | Select-Object -First 5) {
        $count++
        Write-Host "$count. $($detail.Action) in $($detail.System)" -ForegroundColor White
        Write-Host "   Effect: $($detail.ActionEffect)" -ForegroundColor Gray
        Write-Host "   Scopes ($($detail.ScopeCount)): $($detail.Scopes)" -ForegroundColor Gray
        Write-Host ""
    }
    
} else {
    Write-Host "❌ FAIL: No scopes found in any action!" -ForegroundColor Red
    Write-Host ""
    Write-Host "Troubleshooting steps:" -ForegroundColor Yellow
    Write-Host "1. Check database view:" -ForegroundColor White
    Write-Host "   SELECT * FROM v_user_permissions_v2 WHERE `"permissionType`" = 'SCOPE' LIMIT 5;" -ForegroundColor Gray
    Write-Host ""
    Write-Host "2. Verify user_action_permission_nodes table has data" -ForegroundColor White
    Write-Host ""
    Write-Host "3. Check auth-service logs for errors" -ForegroundColor White
    Write-Host ""
    Write-Host "4. Restart auth-service:" -ForegroundColor White
    Write-Host "   cd C:\Java\care\Code\auth-service\auth-service" -ForegroundColor Gray
    Write-Host "   mvn spring-boot:run" -ForegroundColor Gray
}

Write-Host ""
Write-Host "======================================================" -ForegroundColor Cyan
Write-Host "📄 Full response saved in: permissions_response.json" -ForegroundColor Cyan
Write-Host "======================================================" -ForegroundColor Cyan

# Optional: Open file in default JSON viewer
$openFile = Read-Host "Open permissions_response.json? (y/n)"
if ($openFile -eq 'y') {
    Invoke-Item "permissions_response.json"
}

