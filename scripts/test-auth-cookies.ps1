# Script de test du systeme d'authentification par cookies httpOnly
# Usage : .\scripts\test-auth-cookies.ps1

$baseUrl = "http://localhost:8000/api"

Write-Host ""
Write-Host "=== 1. Connexion (une session par utilisateur, garde les cookies) ===" -ForegroundColor Cyan

$sessionEtudiant = New-Object Microsoft.PowerShell.Commands.WebRequestSession
$userEtudiant = Invoke-RestMethod -Uri "$baseUrl/auth/login" -Method Post -WebSession $sessionEtudiant `
    -ContentType "application/json" `
    -Body (@{ email = "etudiant@example.com"; mot_de_passe = "motdepasse123" } | ConvertTo-Json)
Write-Host "Etudiant connecte : $($userEtudiant.nom) ($($userEtudiant.type_profil))" -ForegroundColor Green

$sessionEntreprise = New-Object Microsoft.PowerShell.Commands.WebRequestSession
$userEntreprise = Invoke-RestMethod -Uri "$baseUrl/auth/login" -Method Post -WebSession $sessionEntreprise `
    -ContentType "application/json" `
    -Body (@{ email = "entreprise@example.com"; mot_de_passe = "motdepasse123" } | ConvertTo-Json)
Write-Host "Entreprise connectee : $($userEntreprise.nom) ($($userEntreprise.type_profil))" -ForegroundColor Green

Write-Host ""
Write-Host "=== 2. Verification que les cookies sont bien httpOnly ===" -ForegroundColor Cyan
$cookieAccess = $sessionEtudiant.Cookies.GetCookies("$baseUrl/") | Where-Object { $_.Name -eq "access_token" }
if ($cookieAccess -and $cookieAccess.HttpOnly) {
    Write-Host "OK - le cookie access_token est bien marque HttpOnly." -ForegroundColor Green
} else {
    Write-Host "ATTENTION - le cookie n'est pas marque HttpOnly ou est absent." -ForegroundColor Red
}

Write-Host ""
Write-Host "=== 3. Appel a /auth/me avec la session (doit fonctionner sans header manuel) ===" -ForegroundColor Cyan
$me = Invoke-RestMethod -Uri "$baseUrl/auth/me" -Method Get -WebSession $sessionEtudiant
Write-Host "OK - identifie comme : $($me.email)" -ForegroundColor Green

Write-Host ""
Write-Host "=== 4. Appel SANS session (doit etre rejete, 401) ===" -ForegroundColor Cyan
try {
    Invoke-RestMethod -Uri "$baseUrl/auth/me" -Method Get | Out-Null
    Write-Host "INATTENDU - acces autorise sans cookie !" -ForegroundColor Red
} catch {
    Write-Host "OK - acces refuse sans cookie (401 attendu)." -ForegroundColor Green
}

Write-Host ""
Write-Host "=== 5. Creation d'une offre avec la session entreprise ===" -ForegroundColor Cyan
$offre = Invoke-RestMethod -Uri "$baseUrl/offres" -Method Post -WebSession $sessionEntreprise `
    -ContentType "application/json" `
    -Body (@{
        titre = "Test cookies httpOnly"
        type_contrat = "cdi"
        competences_obligatoires = "Python"
        disponibilite = "immediate"
    } | ConvertTo-Json)
Write-Host "Offre creee (id: $($offre.id))" -ForegroundColor Green

Write-Host ""
Write-Host "=== 6. Candidature avec la session etudiant ===" -ForegroundColor Cyan
$candidature = Invoke-RestMethod -Uri "$baseUrl/candidatures" -Method Post -WebSession $sessionEtudiant `
    -ContentType "application/json" `
    -Body (@{ offre_id = $offre.id; message = "Test" } | ConvertTo-Json)
Write-Host "Candidature deposee (id: $($candidature.id))" -ForegroundColor Green

Write-Host ""
Write-Host "=== 7. Rafraichissement du token (refresh) ===" -ForegroundColor Cyan
$refreshed = Invoke-RestMethod -Uri "$baseUrl/auth/refresh" -Method Post -WebSession $sessionEtudiant
Write-Host "OK - token rafraichi pour : $($refreshed.email)" -ForegroundColor Green

Write-Host ""
Write-Host "=== 8. Deconnexion ===" -ForegroundColor Cyan
Invoke-RestMethod -Uri "$baseUrl/auth/logout" -Method Post -WebSession $sessionEtudiant | Out-Null
Write-Host "Deconnecte." -ForegroundColor Green

Write-Host ""
Write-Host "=== 9. Verification qu'apres logout, /auth/me est refuse ===" -ForegroundColor Cyan
try {
    Invoke-RestMethod -Uri "$baseUrl/auth/me" -Method Get -WebSession $sessionEtudiant | Out-Null
    Write-Host "INATTENDU - toujours connecte apres logout !" -ForegroundColor Red
} catch {
    Write-Host "OK - session bien terminee apres logout." -ForegroundColor Green
}

Write-Host ""
Write-Host "=== Test termine ===" -ForegroundColor Cyan