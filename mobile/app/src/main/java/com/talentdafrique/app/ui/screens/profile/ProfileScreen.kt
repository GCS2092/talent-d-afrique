package com.talentdafrique.app.ui.screens.profile

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Logout
import androidx.compose.material.icons.outlined.WifiOff
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.talentdafrique.app.data.remote.dto.ProfileDto
import com.talentdafrique.app.ui.components.ErrorView
import com.talentdafrique.app.ui.components.LoadingView
import com.talentdafrique.app.ui.theme.Primary
import com.talentdafrique.app.ui.theme.PrimaryContainer
import java.io.File
import java.io.FileOutputStream

@Composable
fun ProfileScreen(
    viewModel: ProfileViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current

    val cvPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent(),
    ) { uri: Uri? ->
        uri ?: return@rememberLauncherForActivityResult
        val inputStream = context.contentResolver.openInputStream(uri) ?: return@rememberLauncherForActivityResult
        val tempFile = File.createTempFile("cv_", ".pdf", context.cacheDir)
        FileOutputStream(tempFile).use { output -> inputStream.copyTo(output) }
        viewModel.uploadCv(tempFile)
    }

    when {
        uiState.isLoading -> LoadingView(modifier = Modifier.fillMaxSize())

        uiState.errorMessage != null && uiState.user == null -> ErrorView(
            message = uiState.errorMessage ?: "",
            onRetry = viewModel::loadProfile,
            icon = Icons.Outlined.WifiOff,
        )

        else -> Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(bottom = 24.dp),
        ) {
            // En-tête avatar
            Column(
                modifier = Modifier.fillMaxWidth().padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Surface(shape = CircleShape, color = PrimaryContainer, modifier = Modifier.size(84.dp)) {
                    Box(contentAlignment = Alignment.Center) {
                        Text(
                            text = uiState.user?.nom?.take(1)?.uppercase() ?: "?",
                            style = MaterialTheme.typography.headlineMedium,
                            color = Primary,
                        )
                    }
                }
                Text(
                    text = uiState.user?.nom ?: "",
                    style = MaterialTheme.typography.titleLarge,
                    modifier = Modifier.padding(top = 12.dp),
                )
                Text(
                    text = uiState.user?.email ?: "",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }

            val etudiantProfile = uiState.profile as? ProfileDto.Etudiant

            // Carte CV
            Card(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(text = "Mon CV", style = MaterialTheme.typography.titleMedium)

                    Text(
                        text = if (etudiantProfile?.cvUrl != null) "CV envoyé ✓" else "Aucun CV envoyé pour l'instant.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = if (etudiantProfile?.cvUrl != null) Primary else MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(top = 4.dp, bottom = 12.dp),
                    )

                    uiState.cvUploadError?.let { message ->
                        Text(text = message, color = MaterialTheme.colorScheme.error, modifier = Modifier.padding(bottom = 8.dp))
                    }

                    OutlinedButton(
                        onClick = { cvPickerLauncher.launch("application/pdf") },
                        enabled = !uiState.isUploadingCv,
                        modifier = Modifier.fillMaxWidth(),
                    ) {
                        if (uiState.isUploadingCv) {
                            androidx.compose.material3.CircularProgressIndicator(modifier = Modifier.size(18.dp))
                        } else {
                            Text(if (etudiantProfile?.cvUrl != null) "Remplacer mon CV" else "Envoyer mon CV")
                        }
                    }
                }
            }

            if (!etudiantProfile?.competences.isNullOrBlank() || !etudiantProfile?.disponibilite.isNullOrBlank()) {
                Card(
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp, vertical = 12.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(text = "Mon profil", style = MaterialTheme.typography.titleMedium)

                        if (!etudiantProfile?.competences.isNullOrBlank()) {
                            Text(
                                text = "Compétences",
                                style = MaterialTheme.typography.labelMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.padding(top = 12.dp),
                            )
                            Text(text = etudiantProfile?.competences ?: "", modifier = Modifier.padding(top = 2.dp))
                        }

                        if (!etudiantProfile?.disponibilite.isNullOrBlank()) {
                            Text(
                                text = "Disponibilité",
                                style = MaterialTheme.typography.labelMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.padding(top = 12.dp),
                            )
                            Text(text = etudiantProfile?.disponibilite ?: "", modifier = Modifier.padding(top = 2.dp))
                        }
                    }
                }
            }

            TextButton(
                onClick = viewModel::logout,
                modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp, vertical = 12.dp),
            ) {
                Icon(imageVector = Icons.Outlined.Logout, contentDescription = null, modifier = Modifier.size(18.dp))
                Text(text = "Se déconnecter", modifier = Modifier.padding(start = 8.dp))
            }
        }
    }
}