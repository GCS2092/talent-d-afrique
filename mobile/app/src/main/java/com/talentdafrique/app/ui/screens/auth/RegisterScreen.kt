package com.talentdafrique.app.ui.screens.auth

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

/**
 * TODO : même pattern que LoginScreen — un RegisterViewModel qui appelle
 * authRepository.register(nom, email, motDePasse, typeProfil), avec un
 * sélecteur (ex: liste déroulante ou Segmented Button) pour choisir parmi
 * les 4 TypeProfil (ETUDIANT / ENTREPRISE / ECOLE / FREELANCE).
 */
@Composable
fun RegisterScreen(onRegisterSuccess: () -> Unit, onNavigateToLogin: () -> Unit) {
    Column(modifier = Modifier.fillMaxSize().padding(24.dp)) {
        Text("Écran d'inscription — à compléter sur le modèle de LoginScreen")
    }
}
