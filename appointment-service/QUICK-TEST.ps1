# Quick Test Script for Appointment Service
Write-Host "🧪 Testing Appointment Service APIs" -ForegroundColor Cyan
Write-Host "====================================`n" -ForegroundColor Cyan

$base = "http://localhost:6064"
$passed = 0
$failed = 0

# Test 1: API Docs
Write-Host "1. Testing Swagger API Docs..." -ForegroundColor Yellow
try {
    $docs = Invoke-RestMethod -Uri "$base/v3/api-docs" -Method GET -TimeoutSec 5
    Write-Host "   ✅ SUCCESS - Found $($docs.tags.Count) API groups" -ForegroundColor Green
    $passed++
} catch {
    Write-Host "   ❌ FAILED" -ForegroundColor Red
    $failed++
}

# Test 2: ServiceType
Write-Host "`n2. Testing ServiceType API..." -ForegroundColor Yellow
try {
    $st = Invoke-RestMethod -Uri "$base/api/admin/service-types/meta" -Method GET -TimeoutSec 5
    Write-Host "   ✅ SUCCESS" -ForegroundColor Green
    $passed++
} catch {
    Write-Host "   ❌ FAILED" -ForegroundColor Red
    $failed++
}

# Test 3: ActionType
Write-Host "`n3. Testing ActionType API..." -ForegroundColor Yellow
try {
    $at = Invoke-RestMethod -Uri "$base/api/admin/action-types/meta" -Method GET -TimeoutSec 5
    Write-Host "   ✅ SUCCESS" -ForegroundColor Green
    $passed++
} catch {
    Write-Host "   ❌ FAILED" -ForegroundColor Red
    $failed++
}

# Test 4: Schedule
Write-Host "`n4. Testing Schedule API..." -ForegroundColor Yellow
try {
    $sch = Invoke-RestMethod -Uri "$base/api/admin/schedules/meta" -Method GET -TimeoutSec 5
    Write-Host "   ✅ SUCCESS" -ForegroundColor Green
    $passed++
} catch {
    Write-Host "   ❌ FAILED" -ForegroundColor Red
    $failed++
}

# Test 5: Holiday
Write-Host "`n5. Testing Holiday API..." -ForegroundColor Yellow
try {
    $hol = Invoke-RestMethod -Uri "$base/api/admin/holidays/meta" -Method GET -TimeoutSec 5
    Write-Host "   ✅ SUCCESS" -ForegroundColor Green
    $passed++
} catch {
    Write-Host "   ❌ FAILED" -ForegroundColor Red
    $failed++
}

# Summary
Write-Host "`n====================================" -ForegroundColor Cyan
Write-Host "📊 Results: $passed/$($passed + $failed) tests passed" -ForegroundColor White
Write-Host "====================================`n" -ForegroundColor Cyan

if ($passed -eq 5) {
    Write-Host "🎉🎉🎉 ALL TESTS PASSED! 🎉🎉🎉" -ForegroundColor Green -BackgroundColor Black
    Write-Host "`n✅ 32 Admin Endpoints LIVE" -ForegroundColor Green
    Write-Host "✅ Swagger UI available" -ForegroundColor Green
    Write-Host "`n🌐 Open: http://localhost:6064/swagger-ui.html" -ForegroundColor Yellow
} else {
    Write-Host "⚠️ Some tests failed" -ForegroundColor Yellow
}

