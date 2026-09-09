package com.talentdafrique.app.ui.screens.profile

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

/**
 * TODO : formulaire de profil qui s'adapte au type d'utilisateur (sealed
 * ProfileDto — un `when` exhaustif par sous-type), + pour un étudiant, le
 * bouton d'upload de CV (CvApi.uploadCv) avec un sélecteur de fichier PDF
 * via ActivityResultContracts.OpenDocument(arrayOf("application/pdf")).
 * Inclut aussi les actions RGPD (export, suppression de compte) via UsersApi.
 */
@Composable
fun ProfileScreen() {
    Column(modifier = Modifier.fillMaxSize().padding(24.dp)) {
        Text("Mon profil — à connecter à ProfilesApi / UsersApi / CvApi")
    }
}
