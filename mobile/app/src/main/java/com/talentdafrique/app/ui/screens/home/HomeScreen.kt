package com.talentdafrique.app.ui.screens.home

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.talentdafrique.app.data.remote.dto.TypeProfil

/**
 * Point d'entrée après connexion. Route vers un contenu différent selon le
 * type_profil renvoyé par GET /auth/me — c'est le `when` exhaustif permis
 * par l'enum TypeProfil : le compilateur t'oblige à traiter les 4 cas.
 *
 * TODO par type de profil :
 * - ETUDIANT / FREELANCE : liste des offres recommandées (triées par score
 *   de matching), accès à "mes candidatures", upload de CV
 * - ENTREPRISE : "mes offres", dashboard des candidatures reçues avec filtres
 *   (score min, disponibilité, statut) — voir CandidaturesApi.candidaturesRecues
 * - ECOLE : liste des étudiants rattachés, suggestions par étudiant, stats
 *   d'employabilité — voir ProfilesApi (rattacherEtudiant, listEtudiants,
 *   suggestionsPourEtudiant, statsEmployabilite)
 */
@Composable
fun HomeScreen(typeProfil: TypeProfil) {
    Column(modifier = Modifier.fillMaxSize().padding(24.dp)) {
        when (typeProfil) {
            TypeProfil.ETUDIANT -> Text("Accueil étudiant — offres recommandées à venir")
            TypeProfil.FREELANCE -> Text("Accueil freelance — missions recommandées à venir")
            TypeProfil.ENTREPRISE -> Text("Accueil entreprise — dashboard candidatures à venir")
            TypeProfil.ECOLE -> Text("Accueil école — étudiants rattachés à venir")
        }
    }
}
