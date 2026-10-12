# PowerShell script to start EduTrack Lite Backend connecting to MySQL
$env:JAVA_HOME = "C:\Program Files\Microsoft\jdk-17.0.20.101-hotspot"
$env:Path = "$env:JAVA_HOME\bin;C:\Users\Darshan jain\AppData\Local\Programs\apache-maven-3.9.6\bin;" + $env:Path

Write-Host "============================================================" -ForegroundColor Cyan
Write-Host " Starting EduTrack Lite Spring Boot Backend (MySQL Mode)..." -ForegroundColor Cyan
Write-Host " Connecting to MySQL at localhost:3306/edutrack" -ForegroundColor Yellow
Write-Host "============================================================" -ForegroundColor Cyan

mvn spring-boot:run
