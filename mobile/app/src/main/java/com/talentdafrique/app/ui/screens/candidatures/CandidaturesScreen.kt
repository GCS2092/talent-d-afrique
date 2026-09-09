package com.talentdafrique.app.ui.screens.candidatures

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

/**
 * TODO : deux variantes selon le rôle connecté —
 * - Candidat : CandidaturesApi.mesCandidatures(), avec le statut affiché
 *   (reçue/en cours/entretien/refusée/acceptée)
 * - Entreprise : CandidaturesApi.candidaturesRecues(offreId, filtres...),
 *   avec les 3 filtres du dashboard (score min, disponibilité, statut) —
 *   voir scripts/test-filtres.ps1 côté backend pour les cas limites déjà
 *   validés à reproduire côté UI.
 */
@Composable
fun CandidaturesScreen() {
    Column(modifier = Modifier.fillMaxSize().padding(24.dp)) {
        Text("Candidatures — à connecter à CandidaturesApi")
    }
}
