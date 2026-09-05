# Script de test du moteur de matching V1 (etape 5 du cahier des charges)
# Usage : .\scripts\test-matching.ps1

$baseUrl = "http://localhost:8000/api"

Write-Host ""
Write-Host "=== 1. Connexion en tant qu'etudiant ===" -ForegroundColor Cyan
$loginEtudiant = Invoke-RestMethod -Uri "$baseUrl/auth/login" -Method Post `
    -ContentType "application/json" `
    -Body (@{ email = "etudiant@example.com"; mot_de_passe = "motdepasse123" } | ConvertTo-Json)
$tokenEtudiant = $loginEtudiant.access_token
Write-Host "Token etudiant recupere." -ForegroundColor Green

$headersEtudiant = @{ Authorization = "Bearer $tokenEtudiant" }

Write-Host ""
Write-Host "=== 2. Connexion en tant qu'entreprise ===" -ForegroundColor Cyan
$loginEntreprise = Invoke-RestMethod -Uri "$baseUrl/auth/login" -Method Post `
    -ContentType "application/json" `
    -Body (@{ email = "entreprise@example.com"; mot_de_passe = "motdepasse123" } | ConvertTo-Json)
$tokenEntreprise = $loginEntreprise.access_token
Write-Host "Token entreprise recupere." -ForegroundColor Green
$headersEntreprise = @{ Authorization = "Bearer $tokenEntreprise" }

Write-Host ""
Write-Host "=== 3. Creation d'une offre de test propre ===" -ForegroundColor Cyan
$nouvelleOffre = Invoke-RestMethod -Uri "$baseUrl/offres" -Method Post -Headers $headersEntreprise `
    -ContentType "application/json" `
    -Body (@{
        titre = "Developpeur Full-Stack (test matching)"
        type_contrat = "cdi"
        competences_obligatoires = "Python, React"
        competences_souhaitees = "SQL"
        niveau_experience = "2-5 ans"
        disponibilite = "immediate"
        localisation = "Dakar"
    } | ConvertTo-Json)
$offreId = $nouvelleOffre.id
Write-Host "Offre creee : $($nouvelleOffre.titre) (id: $offreId)" -ForegroundColor Green

Write-Host ""
Write-Host "=== 4. Candidature de l'etudiant sur cette offre ===" -ForegroundColor Cyan
try {
    Invoke-RestMethod -Uri "$baseUrl/candidatures" -Method Post -Headers $headersEtudiant `
        -ContentType "application/json" `
        -Body (@{ offre_id = $offreId; message = "Test automatique" } | ConvertTo-Json) | Out-Null
    Write-Host "Candidature deposee." -ForegroundColor Green
} catch {
    Write-Host "Candidature deja existante ou erreur : $($_.Exception.Message)" -ForegroundColor Yellow
}

Write-Host ""
Write-Host "=== 5. Recommandations pour l'etudiant (sur les offres actives) ===" -ForegroundColor Cyan
$recommandations = Invoke-RestMethod -Uri "$baseUrl/offres/recommandees" -Method Get -Headers $headersEtudiant

foreach ($offre in $recommandations) {
    Write-Host ""
    Write-Host "Offre : $($offre.titre)" -ForegroundColor White
    Write-Host "  Score global   : $($offre.score_global) / 100" -ForegroundColor $(if ($offre.recommandee) { "Green" } else { "Red" })
    Write-Host "  Recommandee    : $($offre.recommandee)"
    Write-Host "  Detail du score :"
    $offre.detail_score.PSObject.Properties | ForEach-Object {
        Write-Host "    - $($_.Name) : $($_.Value)"
    }
}

Write-Host ""
Write-Host "=== 6. Candidats classes par score pour l'offre de test ===" -ForegroundColor Cyan
$classement = Invoke-RestMethod -Uri "$baseUrl/candidatures/offre/$offreId/classees" -Method Get -Headers $headersEntreprise

if ($classement.Count -eq 0) {
    Write-Host "Aucune candidature recue pour cette offre." -ForegroundColor Yellow
} else {
    $rang = 1
    foreach ($candidature in $classement) {
        Write-Host ""
        Write-Host "#$rang - Candidature $($candidature.id)" -ForegroundColor White
        Write-Host "  Score global : $($candidature.score_global) / 100" -ForegroundColor $(if ($candidature.recommandee) { "Green" } else { "Red" })
        Write-Host "  Statut       : $($candidature.statut)"
        $rang++
    }
}

Write-Host ""
Write-Host "=== Test termine ===" -ForegroundColor Cyan