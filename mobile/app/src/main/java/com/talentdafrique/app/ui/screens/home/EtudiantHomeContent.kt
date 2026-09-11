package com.talentdafrique.app.ui.screens.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.SearchOff
import androidx.compose.material.icons.outlined.WifiOff
import androidx.compose.material.icons.outlined.WorkOutline
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.talentdafrique.app.ui.components.EmptyView
import com.talentdafrique.app.ui.components.ErrorView
import com.talentdafrique.app.ui.components.LoadingView
import com.talentdafrique.app.ui.screens.offres.OffreCard
import com.talentdafrique.app.ui.theme.PrimaryContainer
import com.talentdafrique.app.ui.theme.OnPrimaryContainer

@Composable
fun EtudiantHomeContent(
    onOffreClick: (String) -> Unit,
    onSeeAllClick: () -> Unit,
    viewModel: EtudiantHomeViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsState()

    when {
        uiState.isLoading -> LoadingView(modifier = Modifier.fillMaxSize())

        uiState.errorMessage != null -> ErrorView(
            message = uiState.errorMessage ?: "",
            onRetry = viewModel::loadOffresRecommandees,
            icon = Icons.Outlined.WifiOff,
        )

        else -> LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(bottom = 24.dp),
        ) {
            item {
                Surface(color = PrimaryContainer, contentColor = OnPrimaryContainer) {
                    Column(modifier = Modifier.fillMaxWidth().padding(20.dp)) {
                        Text(text = "Bonjour 👋", style = MaterialTheme.typography.headlineSmall)
                        Text(
                            text = "Voici les offres qui te correspondent le mieux",
                            style = MaterialTheme.typography.bodyMedium,
                            modifier = Modifier.padding(top = 4.dp),
                        )
                    }
                }
            }

            item {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp, vertical = 16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(text = "Recommandées pour toi", style = MaterialTheme.typography.titleLarge)
                    TextButton(onClick = onSeeAllClick) { Text("Tout voir") }
                }
            }

            if (uiState.offres.isEmpty()) {
                item {
                    EmptyView(
                        title = "Pas encore de recommandations",
                        subtitle = "Complète ton profil et ton CV pour recevoir des offres adaptées.",
                        icon = Icons.Outlined.SearchOff,
                        modifier = Modifier.height(320.dp),
                    )
                }
            } else {
                items(uiState.offres, key = { it.id }) { offre ->
                    Column(modifier = Modifier.padding(horizontal = 20.dp, vertical = 6.dp)) {
                        OffreCard(offre = offre, onClick = { onOffreClick(offre.id) })
                    }
                }
            }
        }
    }
}