package com.talentdafrique.app.ui.screens.offres

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

/**
 * TODO : LazyColumn qui liste OffreDto (via OffresApi.listOffres), avec pour
 * chaque carte : titre, entreprise, type de contrat, et le score de matching
 * (offre.matching?.scoreGlobal) affiché avec une petite barre de progression
 * et un badge "Recommandée" si offre.matching?.recommandee == true.
 */
@Composable
fun OffresListScreen(onOffreClick: (String) -> Unit) {
    Column(modifier = Modifier.fillMaxSize().padding(24.dp)) {
        Text("Liste des offres — à connecter à OffresApi.listOffres()")
    }
}
