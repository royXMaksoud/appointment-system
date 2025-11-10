# PowerShell script to start appointment-service

Write-Host "Starting Appointment Service..." -ForegroundColor Green

# Check if Java is installed
try {
    $javaVersion = java -version 2>&1
    Write-Host "Java is installed: $($javaVersion[0])" -ForegroundColor Green
} catch {
    Write-Host "ERROR: Java is not installed or not in PATH" -ForegroundColor Red
    exit 1
}

# Check if Maven is installed
try {
    $mavenVersion = mvn -version 2>&1 | Select-Object -First 1
    Write-Host "Maven is installed: $mavenVersion" -ForegroundColor Green
} catch {
    Write-Host "WARNING: Maven is not installed. Using existing JAR file..." -ForegroundColor Yellow
}

# Set environment variables (optional - can be overridden)
$env:DB_HOST = "localhost"
$env:DB_PORT = "5432"
$env:DB_NAME = "cms_db"
$env:DB_USERNAME = "postgres"
$env:DB_PASSWORD = "P@ssw0rd"

Write-Host "`nEnvironment Variables:" -ForegroundColor Cyan
Write-Host "  DB_HOST: $env:DB_HOST"
Write-Host "  DB_PORT: $env:DB_PORT"
Write-Host "  DB_NAME: $env:DB_NAME"
Write-Host "  DB_USERNAME: $env:DB_USERNAME"

# Check if JAR exists, if not build it
$jarPath = "target\appointment-service-0.0.1-SNAPSHOT.jar"
if (-not (Test-Path $jarPath)) {
    Write-Host "`nJAR file not found. Building the application..." -ForegroundColor Yellow
    mvn clean install -DskipTests
    if ($LASTEXITCODE -ne 0) {
        Write-Host "ERROR: Build failed!" -ForegroundColor Red
        exit 1
    }
}

Write-Host "`nStarting Appointment Service on port 6064..." -ForegroundColor Green
Write-Host "Press Ctrl+C to stop the service" -ForegroundColor Yellow
Write-Host "API Documentation: http://localhost:6064/swagger-ui.html" -ForegroundColor Cyan
Write-Host "Through Gateway: http://localhost:6060/appointment/api/appointments/health" -ForegroundColor Cyan

# Start the service
mvn spring-boot:run

