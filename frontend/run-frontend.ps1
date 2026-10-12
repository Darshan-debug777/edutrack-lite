# PowerShell script to start EduTrack Lite React Frontend
$env:Path = "C:\Program Files\nodejs;" + $env:Path

Write-Host "============================================================" -ForegroundColor Cyan
Write-Host " Starting EduTrack Lite React Frontend Development Server..." -ForegroundColor Cyan
Write-Host " URL: http://localhost:5173" -ForegroundColor Green
Write-Host "============================================================" -ForegroundColor Cyan

npm run dev
