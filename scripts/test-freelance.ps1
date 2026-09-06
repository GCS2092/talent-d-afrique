# Script de test de l'espace freelance et du matching adapte aux missions (etape 8)
# Usage : .\scripts\test-freelance.ps1

$baseUrl = "http://localhost:8000/api"

Write-Host ""
Write-Host "=== 1. Creation d'un compte freelance ===" -ForegroundColor Cyan
try {
    Invoke-RestMethod -Uri "$baseUrl/auth/register" -Method Post `
        -ContentType "application/json" `
        -Body (@{
            nom = "Freelance Test"
            email = "freelance@example.com"
            mot_de_passe = "motdepasse123"
            type_profil = "freelance"
            consentement = $true
            consent_version = "2026-09-05"
        } | ConvertTo-Json) | Out-Null
    Write-Host "Compte freelance cree." -ForegroundColor Green
} catch {
    Write-Host "Compte deja existant (normal si le script a deja tourne)." -ForegroundColor Yellow
}

Write-Host ""
Write-Host "=== 2. Connexion en tant que freelance ===" -ForegroundColor Cyan
$loginFreelance = Invoke-RestMethod -Uri "$baseUrl/auth/login" -Method Post `
    -ContentType "application/json" `
    -Body (@{ email = "freelance@example.com"; mot_de_passe = "motdepasse123" } | ConvertTo-Json)
$tokenFreelance = $loginFreelance.access_token
$headersFreelance = @{ Authorization = "Bearer $tokenFreelance" }
Write-Host "Token freelance recupere." -ForegroundColor Green

Write-Host ""
Write-Host "=== 3. Creation du profil freelance (TJM = 150) ===" -ForegroundColor Cyan
Invoke-RestMethod -Uri "$baseUrl/profiles/me" -Method Put -Headers $headersFreelance `
    -ContentType "application/json" `
    -Body (@{
        competences = "Python, React"
        tjm = 150
        disponibilite = "immediate"
    } | ConvertTo-Json) | Out-Null
Write-Host "Profil freelance cree avec TJM = 150." -ForegroundColor Green

Write-Host ""
Write-Host "=== 4. Connexion en tant qu'entreprise ===" -ForegroundColor Cyan
$loginEntreprise = Invoke-RestMethod -Uri "$baseUrl/auth/login" -Method Post `
    -ContentType "application/json" `
    -Body (@{ email = "entreprise@example.com"; mot_de_passe = "motdepasse123" } | ConvertTo-Json)
$tokenEntreprise = $loginEntreprise.access_token
$headersEntreprise = @{ Authorization = "Bearer $tokenEntreprise" }
Write-Host "Token entreprise recupere." -ForegroundColor Green

Write-Host ""
Write-Host "=== 5. Creation d'une mission avec budget TJM = 200 (dans le budget) ===" -ForegroundColor Cyan
$missionDansBudget = Invoke-RestMethod -Uri "$baseUrl/offres" -Method Post -Headers $headersEntreprise `
    -ContentType "application/json" `
    -Body (@{
        titre = "Mission React (budget confortable)"
        type_contrat = "mission"
        competences_obligatoires = "Python, React"
        disponibilite = "immediate"
        budget_tjm = 200
    } | ConvertTo-Json)
Write-Host "Mission creee (id: $($missionDansBudget.id))" -ForegroundColor Green

Write-Host ""
Write-Host "=== 6. Creation d'une mission avec budget TJM = 80 (hors budget) ===" -ForegroundColor Cyan
$missionHorsBudget = Invoke-RestMethod -Uri "$baseUrl/offres" -Method Post -Headers $headersEntreprise `
    -ContentType "application/json" `
    -Body (@{
        titre = "Mission React (petit budget)"
        type_contrat = "mission"
        competences_obligatoires = "Python, React"
        disponibilite = "immediate"
        budget_tjm = 80
    } | ConvertTo-Json)
Write-Host "Mission creee (id: $($missionHorsBudget.id))" -ForegroundColor Green

Write-Host ""
Write-Host "=== 7. Recommandations pour le freelance ===" -ForegroundColor Cyan
$recommandations = Invoke-RestMethod -Uri "$baseUrl/offres/recommandees" -Method Get -Headers $headersFreelance

foreach ($offre in $recommandations) {
    if ($offre.type_contrat -eq "mission") {
        Write-Host ""
        Write-Host "Mission : $($offre.titre)" -ForegroundColor White
        Write-Host "  Budget TJM     : $($offre.budget_tjm)"
        Write-Host "  Score global   : $($offre.score_global) / 100" -ForegroundColor $(if ($offre.recommandee) { "Green" } else { "Red" })
        Write-Host "  Detail :"
        $offre.detail_score.PSObject.Properties | ForEach-Object {
            Write-Host "    - $($_.Name) : $($_.Value)"
        }
    }
}

Write-Host ""
Write-Host "=== 8. Filtre sur les missions uniquement (liste publique) ===" -ForegroundColor Cyan
$missionsPubliques = Invoke-RestMethod -Uri "$baseUrl/offres?type_contrat=mission" -Method Get
Write-Host "$($missionsPubliques.Count) mission(s) active(s) trouvee(s) via le filtre." -ForegroundColor Green

Write-Host ""
Write-Host "=== Test termine ===" -ForegroundColor Cyan
Write-Host "Attendu : la mission a 200 (budget confortable) doit avoir un meilleur score" -ForegroundColor Yellow
Write-Host "que celle a 80 (TJM du freelance = 150, donc hors budget pour la 2e mission)." -ForegroundColor Yellow