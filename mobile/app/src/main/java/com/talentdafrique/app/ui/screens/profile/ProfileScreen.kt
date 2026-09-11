package com.talentdafrique.app.ui.screens.profile

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
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
        uiState.isLoading -> Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }

        uiState.errorMessage != null && uiState.user == null -> Box(
            Modifier.fillMaxSize().padding(24.dp),
            contentAlignment = Alignment.Center,
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(uiState.errorMessage ?: "")
                TextButton(onClick = viewModel::loadProfile) { Text("Réessayer") }
            }
        }

        else -> Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(24.dp),
        ) {
            Text(text = uiState.user?.nom ?: "", style = MaterialTheme.typography.headlineSmall)
            Text(
                text = uiState.user?.email ?: "",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )

            val etudiantProfile = uiState.profile as? ProfileDto.Etudiant

            Text(text = "CV", style = MaterialTheme.typography.titleMedium, modifier = Modifier.padding(top = 24.dp))

            Text(
                text = if (etudiantProfile?.cvUrl != null) "CV déjà envoyé ✓" else "Aucun CV envoyé pour l'instant.",
                style = MaterialTheme.typography.bodyMedium,
                color = if (etudiantProfile?.cvUrl != null) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.padding(top = 4.dp),
            )

            uiState.cvUploadError?.let { message ->
                Text(text = message, color = MaterialTheme.colorScheme.error, modifier = Modifier.padding(top = 8.dp))
            }

            OutlinedButton(
                onClick = { cvPickerLauncher.launch("application/pdf") },
                enabled = !uiState.isUploadingCv,
                modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
            ) {
                if (uiState.isUploadingCv) {
                    CircularProgressIndicator(modifier = Modifier.padding(4.dp))
                } else {
                    Text(if (etudiantProfile?.cvUrl != null) "Remplacer mon CV" else "Envoyer mon CV")
                }
            }

            if (etudiantProfile != null && etudiantProfile.competences.isNotEmpty()) {
                Text(text = "Compétences détectées", style = MaterialTheme.typography.titleMedium, modifier = Modifier.padding(top = 24.dp))
                Text(text = etudiantProfile.competences.joinToString(", "), modifier = Modifier.padding(top = 8.dp))
            }

            etudiantProfile?.disponibilite?.let {
                Text(
                    text = "Disponibilité : $it",
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(top = 16.dp),
                )
            }
        }
    }
}