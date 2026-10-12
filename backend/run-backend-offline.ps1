# PowerShell script to start EduTrack Lite Backend in Standalone Mode
$env:JAVA_HOME = "C:\Program Files\Microsoft\jdk-17.0.20.101-hotspot"
$env:Path = "$env:JAVA_HOME\bin;C:\Users\Darshan jain\AppData\Local\Programs\apache-maven-3.9.6\bin;" + $env:Path

Write-Host "============================================================" -ForegroundColor Green
Write-Host " Starting EduTrack Lite Spring Boot Backend (Standalone / Viva Demo Mode)..." -ForegroundColor Green
Write-Host " Using pure JDBC with in-memory MySQL-compatible persistence" -ForegroundColor Yellow
Write-Host " REST API available at: http://localhost:8080/api" -ForegroundColor Green
Write-Host "============================================================" -ForegroundColor Green

mvn spring-boot:run "-Dspring-boot.run.profiles=h2"
