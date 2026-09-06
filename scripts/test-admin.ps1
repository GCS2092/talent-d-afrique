# Script de test de l'administration (etape 10 du cahier des charges)
# Usage : .\scripts\test-admin.ps1
# Pre-requis : UPDATE users SET is_admin = true WHERE email = 'entreprise@example.com';

$baseUrl = "http://localhost:8000/api"

Write-Host ""
Write-Host "=== 1. Connexion en tant qu'admin (compte entreprise promu) ===" -ForegroundColor Cyan
$loginAdmin = Invoke-RestMethod -Uri "$baseUrl/auth/login" -Method Post `
    -ContentType "application/json" `
    -Body (@{ email = "entreprise@example.com"; mot_de_passe = "motdepasse123" } | ConvertTo-Json)
$tokenAdmin = $loginAdmin.access_token
$headersAdmin = @{ Authorization = "Bearer $tokenAdmin" }
Write-Host "Token admin recupere." -ForegroundColor Green

Write-Host ""
Write-Host "=== 2. Verification qu'un non-admin est bien rejete ===" -ForegroundColor Cyan
$loginEtudiant = Invoke-RestMethod -Uri "$baseUrl/auth/login" -Method Post `
    -ContentType "application/json" `
    -Body (@{ email = "etudiant@example.com"; mot_de_passe = "motdepasse123" } | ConvertTo-Json)
$headersEtudiant = @{ Authorization = "Bearer $($loginEtudiant.access_token)" }

try {
    Invoke-RestMethod -Uri "$baseUrl/admin/statistiques" -Method Get -Headers $headersEtudiant | Out-Null
    Write-Host "INATTENDU : un etudiant a pu acceder aux statistiques admin !" -ForegroundColor Red
} catch {
    Write-Host "OK - acces refuse pour un non-admin (403 attendu)." -ForegroundColor Green
}

Write-Host ""
Write-Host "=== 3. Statistiques globales ===" -ForegroundColor Cyan
$stats = Invoke-RestMethod -Uri "$baseUrl/admin/statistiques" -Method Get -Headers $headersAdmin
Write-Host "Total utilisateurs      : $($stats.total_utilisateurs)"
Write-Host "  dont etudiants        : $($stats.total_etudiants)"
Write-Host "  dont entreprises      : $($stats.total_entreprises)"
Write-Host "  dont ecoles           : $($stats.total_ecoles)"
Write-Host "  dont freelances       : $($stats.total_freelances)"
Write-Host "Total offres            : $($stats.total_offres) (dont $($stats.total_offres_actives) actives)"
Write-Host "Total candidatures      : $($stats.total_candidatures)"
Write-Host "Taux de matching moyen  : $($stats.taux_matching_moyen) %"

Write-Host ""
Write-Host "=== 4. Liste des utilisateurs (filtre etudiant) ===" -ForegroundColor Cyan
$etudiants = Invoke-RestMethod -Uri "$baseUrl/admin/utilisateurs?type_profil=etudiant" -Method Get -Headers $headersAdmin
Write-Host "$($etudiants.Count) etudiant(s) trouve(s)." -ForegroundColor Green
$premierEtudiantId = $etudiants[0].id

Write-Host ""
Write-Host "=== 5. Suspension d'un compte etudiant ===" -ForegroundColor Cyan
Invoke-RestMethod -Uri "$baseUrl/admin/utilisateurs/$premierEtudiantId/suspendre" -Method Patch -Headers $headersAdmin | Out-Null
Write-Host "Compte suspendu." -ForegroundColor Green

Write-Host ""
Write-Host "=== 6. Verification que le compte suspendu ne peut plus se connecter ===" -ForegroundColor Cyan
try {
    Invoke-RestMethod -Uri "$baseUrl/auth/login" -Method Post -ContentType "application/json" `
        -Body (@{ email = "etudiant@example.com"; mot_de_passe = "motdepasse123" } | ConvertTo-Json) | Out-Null
    Write-Host "INATTENDU : connexion reussie malgre la suspension !" -ForegroundColor Red
} catch {
    Write-Host "OK - connexion refusee pour le compte suspendu." -ForegroundColor Green
}

Write-Host ""
Write-Host "=== 7. Reactivation du compte ===" -ForegroundColor Cyan
Invoke-RestMethod -Uri "$baseUrl/admin/utilisateurs/$premierEtudiantId/reactiver" -Method Patch -Headers $headersAdmin | Out-Null
Write-Host "Compte reactive." -ForegroundColor Green

Write-Host ""
Write-Host "=== 8. Verification que la connexion refonctionne ===" -ForegroundColor Cyan
try {
    Invoke-RestMethod -Uri "$baseUrl/auth/login" -Method Post -ContentType "application/json" `
        -Body (@{ email = "etudiant@example.com"; mot_de_passe = "motdepasse123" } | ConvertTo-Json) | Out-Null
    Write-Host "OK - connexion de nouveau possible." -ForegroundColor Green
} catch {
    Write-Host "INATTENDU : connexion toujours refusee apres reactivation !" -ForegroundColor Red
}

Write-Host ""
Write-Host "=== 9. Historique des actions admin (logs) ===" -ForegroundColor Cyan
$logs = Invoke-RestMethod -Uri "$baseUrl/admin/logs" -Method Get -Headers $headersAdmin
Write-Host "$($logs.Count) log(s) trouve(s) :" -ForegroundColor Green
foreach ($log in $logs | Select-Object -First 5) {
    Write-Host "  [$($log.action)] cible: $($log.cible_type)/$($log.cible_id)"
}

Write-Host ""
Write-Host "=== Test termine ===" -ForegroundColor Cyan