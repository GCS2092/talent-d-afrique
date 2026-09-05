# Script de test de l'espace ecole (etape 7 du cahier des charges)
# Usage : .\scripts\test-ecole.ps1

$baseUrl = "http://localhost:8000/api"

Write-Host ""
Write-Host "=== 1. Creation d'un compte ecole ===" -ForegroundColor Cyan
try {
    Invoke-RestMethod -Uri "$baseUrl/auth/register" -Method Post `
        -ContentType "application/json" `
        -Body (@{
            nom = "Ecole Test"
            email = "ecole@example.com"
            mot_de_passe = "motdepasse123"
            type_profil = "ecole"
            consentement = $true
            consent_version = "2026-09-05"
        } | ConvertTo-Json) | Out-Null
    Write-Host "Compte ecole cree." -ForegroundColor Green
} catch {
    Write-Host "Compte deja existant (normal si le script a deja tourne)." -ForegroundColor Yellow
}

Write-Host ""
Write-Host "=== 2. Connexion en tant qu'ecole ===" -ForegroundColor Cyan
$loginEcole = Invoke-RestMethod -Uri "$baseUrl/auth/login" -Method Post `
    -ContentType "application/json" `
    -Body (@{ email = "ecole@example.com"; mot_de_passe = "motdepasse123" } | ConvertTo-Json)
$tokenEcole = $loginEcole.access_token
$headersEcole = @{ Authorization = "Bearer $tokenEcole" }
Write-Host "Token ecole recupere." -ForegroundColor Green

Write-Host ""
Write-Host "=== 3. Creation du profil ecole ===" -ForegroundColor Cyan
$profilEcole = Invoke-RestMethod -Uri "$baseUrl/profiles/me" -Method Put -Headers $headersEcole `
    -ContentType "application/json" `
    -Body (@{ nom_etablissement = "Ecole Test"; localisation = "Dakar" } | ConvertTo-Json)
$ecoleId = $profilEcole.id
Write-Host "Profil ecole cree (id: $ecoleId)" -ForegroundColor Green

Write-Host ""
Write-Host "=== 4. Connexion en tant qu'etudiant ===" -ForegroundColor Cyan
$loginEtudiant = Invoke-RestMethod -Uri "$baseUrl/auth/login" -Method Post `
    -ContentType "application/json" `
    -Body (@{ email = "etudiant@example.com"; mot_de_passe = "motdepasse123" } | ConvertTo-Json)
$tokenEtudiant = $loginEtudiant.access_token
$headersEtudiant = @{ Authorization = "Bearer $tokenEtudiant" }
Write-Host "Token etudiant recupere." -ForegroundColor Green

Write-Host ""
Write-Host "=== 5. Rattachement de l'etudiant a l'ecole ===" -ForegroundColor Cyan
Invoke-RestMethod -Uri "$baseUrl/profiles/etudiant/rattacher-ecole/$ecoleId" -Method Post -Headers $headersEtudiant | Out-Null
Write-Host "Etudiant rattache." -ForegroundColor Green

Write-Host ""
Write-Host "=== 6. Liste des etudiants de l'ecole ===" -ForegroundColor Cyan
$etudiants = Invoke-RestMethod -Uri "$baseUrl/profiles/ecole/$ecoleId/etudiants" -Method Get -Headers $headersEcole
Write-Host "$($etudiants.Count) etudiant(s) trouve(s) :" -ForegroundColor Green
foreach ($e in $etudiants) {
    Write-Host "  - user_id: $($e.user_id), competences: $($e.competences)"
}

Write-Host ""
Write-Host "=== 7. Suggestions d'offres par etudiant ===" -ForegroundColor Cyan
$suggestions = Invoke-RestMethod -Uri "$baseUrl/profiles/ecole/$ecoleId/suggestions" -Method Get -Headers $headersEcole
$suggestions.PSObject.Properties | ForEach-Object {
    Write-Host ""
    Write-Host "Etudiant $($_.Name) :" -ForegroundColor White
    if ($_.Value.Count -eq 0) {
        Write-Host "  Aucune offre recommandee actuellement."
    } else {
        foreach ($offre in $_.Value) {
            Write-Host "  - $($offre.titre) (score: $($offre.score_global))"
        }
    }
}

Write-Host ""
Write-Host "=== 8. Statistiques d'employabilite ===" -ForegroundColor Cyan
$stats = Invoke-RestMethod -Uri "$baseUrl/profiles/ecole/$ecoleId/statistiques" -Method Get -Headers $headersEcole
Write-Host "Total etudiants          : $($stats.total_etudiants)"
Write-Host "Candidatures envoyees    : $($stats.total_candidatures_envoyees)"
Write-Host "Candidatures acceptees   : $($stats.total_candidatures_acceptees)"
Write-Host "Etudiants places         : $($stats.etudiants_places)"
Write-Host "Taux de placement        : $($stats.taux_placement) %"

Write-Host ""
Write-Host "=== Test termine ===" -ForegroundColor Cyan