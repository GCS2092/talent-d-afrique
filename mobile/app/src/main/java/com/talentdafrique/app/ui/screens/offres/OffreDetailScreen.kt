package com.talentdafrique.app.ui.screens.offres

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

/**
 * TODO : détail d'une offre (OffresApi.getOffre(id)) + le détail du score
 * par critère (compétences obligatoires/souhaitées, expérience ou TJM,
 * disponibilité, soft skills) pour la transparence demandée par le cahier
 * des charges, + bouton "Postuler" qui appelle CandidaturesApi.postuler().
 */
@Composable
fun OffreDetailScreen(offreId: String) {
    Column(modifier = Modifier.fillMaxSize().padding(24.dp)) {
        Text("Détail de l'offre $offreId — à connecter à OffresApi.getOffre()")
    }
}
