package com.talentdafrique.app.ui.screens.offres

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel

@Composable
fun OffreDetailScreen(
    onCandidatureEnvoyee: () -> Unit = {},
    viewModel: OffreDetailViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(uiState.candidatureEnvoyee) {
        if (uiState.candidatureEnvoyee) onCandidatureEnvoyee()
    }

    when {
        uiState.isLoading -> Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }

        uiState.errorMessage != null -> Box(Modifier.fillMaxSize().padding(24.dp), contentAlignment = Alignment.Center) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(uiState.errorMessage ?: "")
                TextButton(onClick = viewModel::loadOffre) { Text("Réessayer") }
            }
        }

        uiState.offre != null -> {
            val offre = uiState.offre!!
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(24.dp),
            ) {
                Text(text = offre.titre, style = MaterialTheme.typography.headlineSmall)

                Text(
                    text = listOfNotNull(offre.localisation, offre.typeContrat.name).joinToString(" · "),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(top = 4.dp),
                )

                offre.remuneration?.let {
                    Text(text = "$it €", style = MaterialTheme.typography.bodyMedium, modifier = Modifier.padding(top = 4.dp))
                }

                offre.scoreGlobal?.let { score ->
                    Text(
                        text = "Score de correspondance : ${(score * 100).toInt()}%",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.padding(top = 16.dp),
                    )
                }

                Text(text = "Description", style = MaterialTheme.typography.titleMedium, modifier = Modifier.padding(top = 24.dp))
                Text(text = offre.description ?: "Aucune description fournie.", modifier = Modifier.padding(top = 8.dp))

                offre.competencesObligatoires?.let {
                    Text(text = "Compétences requises", style = MaterialTheme.typography.titleMedium, modifier = Modifier.padding(top = 24.dp))
                    Text(text = it, modifier = Modifier.padding(top = 8.dp))
                }

                offre.competencesSouhaitees?.let {
                    Text(text = "Compétences souhaitées", style = MaterialTheme.typography.titleMedium, modifier = Modifier.padding(top = 16.dp))
                    Text(text = it, modifier = Modifier.padding(top = 8.dp))
                }

                uiState.postulerError?.let { message ->
                    Text(text = message, color = MaterialTheme.colorScheme.error, modifier = Modifier.padding(top = 16.dp))
                }

                Button(
                    onClick = { viewModel.postuler() },
                    enabled = !uiState.isPostulating && !uiState.candidatureEnvoyee,
                    modifier = Modifier.fillMaxWidth().padding(top = 24.dp),
                ) {
                    when {
                        uiState.isPostulating -> CircularProgressIndicator(modifier = Modifier.padding(4.dp))
                        uiState.candidatureEnvoyee -> Text("Candidature envoyée ✓")
                        else -> Text("Postuler")
                    }
                }
            }
        }
    }
}