# Script de test des notifications in-app et email (etape 9 du cahier des charges)
# Usage : .\scripts\test-notifications.ps1

$baseUrl = "http://localhost:8000/api"

Write-Host ""
Write-Host "=== 1. Connexion en tant qu'entreprise ===" -ForegroundColor Cyan
$loginEntreprise = Invoke-RestMethod -Uri "$baseUrl/auth/login" -Method Post `
    -ContentType "application/json" `
    -Body (@{ email = "entreprise@example.com"; mot_de_passe = "motdepasse123" } | ConvertTo-Json)
$tokenEntreprise = $loginEntreprise.access_token
$headersEntreprise = @{ Authorization = "Bearer $tokenEntreprise" }
Write-Host "Token entreprise recupere." -ForegroundColor Green

Write-Host ""
Write-Host "=== 2. Creation d'une offre de test ===" -ForegroundColor Cyan
$offre = Invoke-RestMethod -Uri "$baseUrl/offres" -Method Post -Headers $headersEntreprise `
    -ContentType "application/json" `
    -Body (@{
        titre = "Test notifications"
        type_contrat = "cdi"
        competences_obligatoires = "Python"
        disponibilite = "immediate"
    } | ConvertTo-Json)
$offreId = $offre.id
Write-Host "Offre creee (id: $offreId)" -ForegroundColor Green

Write-Host ""
Write-Host "=== 3. Connexion en tant qu'etudiant ===" -ForegroundColor Cyan
$loginEtudiant = Invoke-RestMethod -Uri "$baseUrl/auth/login" -Method Post `
    -ContentType "application/json" `
    -Body (@{ email = "etudiant@example.com"; mot_de_passe = "motdepasse123" } | ConvertTo-Json)
$tokenEtudiant = $loginEtudiant.access_token
$headersEtudiant = @{ Authorization = "Bearer $tokenEtudiant" }
Write-Host "Token etudiant recupere." -ForegroundColor Green

Write-Host ""
Write-Host "=== 4. Depot d'une candidature (doit notifier l'entreprise) ===" -ForegroundColor Cyan
$candidature = Invoke-RestMethod -Uri "$baseUrl/candidatures" -Method Post -Headers $headersEtudiant `
    -ContentType "application/json" `
    -Body (@{ offre_id = $offreId; message = "Test notif" } | ConvertTo-Json)
$candidatureId = $candidature.id
Write-Host "Candidature deposee (id: $candidatureId)" -ForegroundColor Green

Write-Host ""
Write-Host "=== 5. Notifications de l'entreprise (doit contenir 'candidature recue') ===" -ForegroundColor Cyan
$notifsEntreprise = Invoke-RestMethod -Uri "$baseUrl/notifications" -Method Get -Headers $headersEntreprise
foreach ($n in $notifsEntreprise) {
    Write-Host "  [$($n.type_evenement)] $($n.titre) - lue: $($n.lue)"
    Write-Host "    $($n.message)"
}

Write-Host ""
Write-Host "=== 6. Changement de statut (doit notifier le candidat) ===" -ForegroundColor Cyan
Invoke-RestMethod -Uri "$baseUrl/candidatures/$candidatureId/statut" -Method Patch -Headers $headersEntreprise `
    -ContentType "application/json" `
    -Body (@{ statut = "entretien" } | ConvertTo-Json) | Out-Null
Write-Host "Statut change en 'entretien'." -ForegroundColor Green

Write-Host ""
Write-Host "=== 7. Notifications de l'etudiant (doit contenir 'entretien') ===" -ForegroundColor Cyan
$notifsEtudiant = Invoke-RestMethod -Uri "$baseUrl/notifications" -Method Get -Headers $headersEtudiant
foreach ($n in $notifsEtudiant) {
    Write-Host "  [$($n.type_evenement)] $($n.titre) - lue: $($n.lue)"
    Write-Host "    $($n.message)"
}

Write-Host ""
Write-Host "=== 8. Marquer la premiere notification de l'etudiant comme lue ===" -ForegroundColor Cyan
if ($notifsEtudiant.Count -gt 0) {
    $premiereNotifId = $notifsEtudiant[0].id
    $notifMaj = Invoke-RestMethod -Uri "$baseUrl/notifications/$premiereNotifId/lue" -Method Patch -Headers $headersEtudiant
    Write-Host "Notification marquee comme lue : $($notifMaj.lue)" -ForegroundColor Green
} else {
    Write-Host "Aucune notification a marquer." -ForegroundColor Yellow
}

Write-Host ""
Write-Host "=== 9. Filtre non_lues_seulement ===" -ForegroundColor Cyan
$nonLues = Invoke-RestMethod -Uri "$baseUrl/notifications?non_lues_seulement=true" -Method Get -Headers $headersEtudiant
Write-Host "$($nonLues.Count) notification(s) non lue(s) restante(s)." -ForegroundColor Green

Write-Host ""
Write-Host "=== Test termine ===" -ForegroundColor Cyan
Write-Host "Regarde aussi le terminal ou tourne uvicorn : tu dois y voir les logs" -ForegroundColor Yellow
Write-Host "'[EMAIL NON ENVOYE - pas de cle API]' si RESEND_API_KEY n'est pas configuree." -ForegroundColor Yellow