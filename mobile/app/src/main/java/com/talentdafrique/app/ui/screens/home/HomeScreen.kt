package com.talentdafrique.app.ui.screens.home

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.talentdafrique.app.data.remote.dto.TypeProfil

@Composable
fun HomeScreen(
    typeProfil: TypeProfil,
    onOffreClick: (String) -> Unit = {},
    onSeeAllOffresClick: () -> Unit = {},
) {
    Column(modifier = Modifier.fillMaxSize()) {
        when (typeProfil) {
            TypeProfil.ETUDIANT -> EtudiantHomeContent(
                onOffreClick = onOffreClick,
                onSeeAllClick = onSeeAllOffresClick,
            )
            TypeProfil.FREELANCE -> Text(
                "Accueil freelance — missions recommandées à venir",
                modifier = Modifier.padding(24.dp),
            )
            TypeProfil.ENTREPRISE -> Text(
                "Accueil entreprise — dashboard candidatures à venir",
                modifier = Modifier.padding(24.dp),
            )
            TypeProfil.ECOLE -> Text(
                "Accueil école — étudiants rattachés à venir",
                modifier = Modifier.padding(24.dp),
            )
        }
    }
}