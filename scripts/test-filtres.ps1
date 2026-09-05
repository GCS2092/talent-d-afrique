# Script de test des filtres sur le classement des candidatures (etape 6 du cahier des charges)
# Usage : .\scripts\test-filtres.ps1

$baseUrl = "http://localhost:8000/api"

Write-Host ""
Write-Host "=== 1. Connexion en tant qu'etudiant ===" -ForegroundColor Cyan
$loginEtudiant = Invoke-RestMethod -Uri "$baseUrl/auth/login" -Method Post `
    -ContentType "application/json" `
    -Body (@{ email = "etudiant@example.com"; mot_de_passe = "motdepasse123" } | ConvertTo-Json)
$tokenEtudiant = $loginEtudiant.access_token
$headersEtudiant = @{ Authorization = "Bearer $tokenEtudiant" }
Write-Host "Token etudiant recupere." -ForegroundColor Green

Write-Host ""
Write-Host "=== 2. Connexion en tant qu'entreprise ===" -ForegroundColor Cyan
$loginEntreprise = Invoke-RestMethod -Uri "$baseUrl/auth/login" -Method Post `
    -ContentType "application/json" `
    -Body (@{ email = "entreprise@example.com"; mot_de_passe = "motdepasse123" } | ConvertTo-Json)
$tokenEntreprise = $loginEntreprise.access_token
$headersEntreprise = @{ Authorization = "Bearer $tokenEntreprise" }
Write-Host "Token entreprise recupere." -ForegroundColor Green

Write-Host ""
Write-Host "=== 3. Creation d'une offre de test propre ===" -ForegroundColor Cyan
$nouvelleOffre = Invoke-RestMethod -Uri "$baseUrl/offres" -Method Post -Headers $headersEntreprise `
    -ContentType "application/json" `
    -Body (@{
        titre = "Developpeur Full-Stack (test filtres)"
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
        -Body (@{ offre_id = $offreId; message = "Test filtres" } | ConvertTo-Json) | Out-Null
    Write-Host "Candidature deposee." -ForegroundColor Green
} catch {
    Write-Host "Candidature deja existante ou erreur : $($_.Exception.Message)" -ForegroundColor Yellow
}

function Test-Filtre {
    param(
        [string]$Description,
        [string]$QueryString,
        [bool]$AttenduVide
    )

    Write-Host ""
    Write-Host "--- $Description ---" -ForegroundColor Cyan
    $url = "$baseUrl/candidatures/offre/$offreId/classees"
    if ($QueryString) { $url += "?$QueryString" }

    $resultat = Invoke-RestMethod -Uri $url -Method Get -Headers $headersEntreprise

    $estVide = ($resultat.Count -eq 0)
    $ok = ($estVide -eq $AttenduVide)

    if ($ok) {
        Write-Host "OK - $($resultat.Count) resultat(s)" -ForegroundColor Green
    } else {
        Write-Host "INATTENDU - $($resultat.Count) resultat(s)" -ForegroundColor Red
    }

    foreach ($c in $resultat) {
        Write-Host "  Candidature $($c.id) - score: $($c.score_global) - statut: $($c.statut)"
    }
}

Write-Host ""
Write-Host "======================================" -ForegroundColor White
Write-Host "  TESTS DES FILTRES" -ForegroundColor White
Write-Host "======================================" -ForegroundColor White

Test-Filtre -Description "Test 1 : sans filtre (doit contenir la candidature)" `
    -QueryString "" -AttenduVide $false

Test-Filtre -Description "Test 2 : score_min=95 (doit etre vide, score reel = 90)" `
    -QueryString "score_min=95" -AttenduVide $true

Test-Filtre -Description "Test 3 : score_min=50 (doit contenir la candidature)" `
    -QueryString "score_min=50" -AttenduVide $false

Test-Filtre -Description "Test 4 : disponibilite=weekend (doit etre vide, candidat = immediate)" `
    -QueryString "disponibilite=weekend" -AttenduVide $true

Test-Filtre -Description "Test 5 : statut=recue (doit contenir la candidature)" `
    -QueryString "statut=recue" -AttenduVide $false

Test-Filtre -Description "Test 6 : statut=acceptee (doit etre vide)" `
    -QueryString "statut=acceptee" -AttenduVide $true

Write-Host ""
Write-Host "=== Tests termines ===" -ForegroundColor Cyan