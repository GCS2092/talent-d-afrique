package com.talentdafrique.app.ui.screens.offres

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.LocationOn
import androidx.compose.material.icons.outlined.Payments
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.talentdafrique.app.data.remote.dto.OffreDto
import com.talentdafrique.app.data.remote.dto.TypeContrat
import com.talentdafrique.app.ui.theme.PrimaryContainer
import com.talentdafrique.app.ui.theme.OnPrimaryContainer
import com.talentdafrique.app.ui.theme.ScoreHigh
import com.talentdafrique.app.ui.theme.ScoreLow
import com.talentdafrique.app.ui.theme.ScoreMedium
import com.talentdafrique.app.ui.theme.SecondaryContainer
import com.talentdafrique.app.ui.theme.OnSecondaryContainer

private fun libelleContrat(type: TypeContrat): String = when (type) {
    TypeContrat.STAGE -> "Stage"
    TypeContrat.CDD -> "CDD"
    TypeContrat.CDI -> "CDI"
    TypeContrat.MISSION -> "Mission"
}

private fun couleurScore(score: Double): androidx.compose.ui.graphics.Color = when {
    score >= 0.7 -> ScoreHigh
    score >= 0.4 -> ScoreMedium
    else -> ScoreLow
}

@Composable
fun OffreCard(
    offre: OffreDto,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Card(
        onClick = onClick,
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
    ) {
        Column(modifier = Modifier.padding(18.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = androidx.compose.foundation.layout.Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top,
            ) {
                Surface(
                    color = SecondaryContainer,
                    contentColor = OnSecondaryContainer,
                    shape = RoundedCornerShape(8.dp),
                ) {
                    Text(
                        text = libelleContrat(offre.typeContrat),
                        style = MaterialTheme.typography.labelMedium,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                    )
                }

                offre.scoreGlobal?.let { score ->
                    Surface(
                        color = couleurScore(score).copy(alpha = 0.15f),
                        contentColor = couleurScore(score),
                        shape = RoundedCornerShape(8.dp),
                    ) {
                        Text(
                            text = "${(score * 100).toInt()}% match",
                            style = MaterialTheme.typography.labelMedium,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = offre.titre,
                style = MaterialTheme.typography.titleMedium,
                maxLines = 2,
            )

            Spacer(modifier = Modifier.height(8.dp))

            offre.localisation?.let {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Outlined.LocationOn,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.height(16.dp),
                    )
                    Spacer(modifier = Modifier.height(0.dp))
                    Text(
                        text = it,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(start = 4.dp),
                    )
                }
            }

            offre.remuneration?.let {
                Spacer(modifier = Modifier.height(4.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Outlined.Payments,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.height(16.dp),
                    )
                    Text(
                        text = "$it €",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(start = 4.dp),
                    )
                }
            }
        }
    }
}