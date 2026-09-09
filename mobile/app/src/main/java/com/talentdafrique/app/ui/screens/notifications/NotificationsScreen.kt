package com.talentdafrique.app.ui.screens.notifications

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

/**
 * TODO : NotificationsApi.listNotifications(), tap pour marquer comme lue
 * (markAsRead). Pour le badge de compteur affiché ailleurs dans l'app
 * (ex: sur l'icône de la bottom bar), appelle unreadCount() au retour au
 * premier plan (Lifecycle.Event.ON_RESUME) — c'est le point d'accroche si
 * tu ajoutes FCM plus tard : remplace juste ce polling par un vrai push,
 * le reste de l'écran ne change pas.
 */
@Composable
fun NotificationsScreen() {
    Column(modifier = Modifier.fillMaxSize().padding(24.dp)) {
        Text("Notifications — à connecter à NotificationsApi")
    }
}
