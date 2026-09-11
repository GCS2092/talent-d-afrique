package com.talentdafrique.app.ui.components

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.talentdafrique.app.data.remote.dto.StatutCandidature

data class StatusStyle(val label: String, val color: Color)

fun statutCandidatureStyle(statut: StatutCandidature): StatusStyle = when (statut) {
    StatutCandidature.RECUE -> StatusStyle("Reçue", Color(0xFF6B6F6A))
    StatutCandidature.EN_COURS -> StatusStyle("En cours", Color(0xFFE8792E))
    StatutCandidature.ENTRETIEN -> StatusStyle("Entretien", Color(0xFF2E6BE8))
    StatutCandidature.REFUSEE -> StatusStyle("Refusée", Color(0xFFBA1A1A))
    StatutCandidature.ACCEPTEE -> StatusStyle("Acceptée", Color(0xFF1B7A5C))
}

@Composable
fun StatusChip(style: StatusStyle, modifier: Modifier = Modifier) {
    Surface(
        color = style.color.copy(alpha = 0.12f),
        contentColor = style.color,
        shape = RoundedCornerShape(8.dp),
        modifier = modifier,
    ) {
        Text(
            text = style.label,
            style = MaterialTheme.typography.labelMedium,
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
        )
    }
}