$root = Split-Path -Parent $PSScriptRoot

Write-Host ""
Write-Host "=== Installation du backend ===" -ForegroundColor Cyan
Set-Location "$root\backend"

if (-Not (Test-Path "venv")) {
    Write-Host "Creation de l'environnement virtuel..." -ForegroundColor Yellow
    python -m venv venv
}

.\venv\Scripts\Activate.ps1
pip install -r requirements.txt

Write-Host ""
Write-Host "=== Installation du frontend ===" -ForegroundColor Cyan
Set-Location "$root\frontend"
npm install

Set-Location $root
Write-Host ""
Write-Host "Installation terminee avec succes." -ForegroundColor Green
Write-Host "Lance '.\scripts\dev.ps1' pour demarrer le projet." -ForegroundColor Green