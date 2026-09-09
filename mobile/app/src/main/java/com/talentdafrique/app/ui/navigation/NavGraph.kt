package com.talentdafrique.app.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.talentdafrique.app.data.remote.dto.TypeProfil
import com.talentdafrique.app.ui.screens.auth.LoginScreen
import com.talentdafrique.app.ui.screens.auth.RegisterScreen
import com.talentdafrique.app.ui.screens.candidatures.CandidaturesScreen
import com.talentdafrique.app.ui.screens.home.HomeScreen
import com.talentdafrique.app.ui.screens.notifications.NotificationsScreen
import com.talentdafrique.app.ui.screens.offres.OffreDetailScreen
import com.talentdafrique.app.ui.screens.offres.OffresListScreen
import com.talentdafrique.app.ui.screens.profile.ProfileScreen

object Routes {
    const val LOGIN = "login"
    const val REGISTER = "register"
    const val HOME = "home"
    const val OFFRES = "offres"
    const val OFFRE_DETAIL = "offre/{offreId}"
    const val CANDIDATURES = "candidatures"
    const val NOTIFICATIONS = "notifications"
    const val PROFILE = "profile"

    fun offreDetail(offreId: String) = "offre/$offreId"
}

/**
 * Graphe racine. `startDestination` dépend de isLoggedIn, observé depuis
 * TokenManager.isLoggedInFlow (via AuthRepository) au niveau de l'appelant
 * (MainActivity) — voir le TODO là-bas pour brancher ça proprement avec un
 * état de "chargement initial" pendant qu'on lit le DataStore.
 *
 * `userTypeProfil` : à récupérer via AuthRepository.me() une fois connecté,
 * pour savoir quel type d'accueil (HomeScreen) afficher.
 */
@Composable
fun TalentDAfriqueNavGraph(
    navController: NavHostController = rememberNavController(),
    startDestination: String = Routes.LOGIN,
    userTypeProfil: TypeProfil = TypeProfil.ETUDIANT, // valeur par défaut tant que non chargé
) {
    NavHost(navController = navController, startDestination = startDestination) {

        composable(Routes.LOGIN) {
            LoginScreen(
                onLoginSuccess = {
                    navController.navigate(Routes.HOME) {
                        popUpTo(Routes.LOGIN) { inclusive = true }
                    }
                },
                onNavigateToRegister = { navController.navigate(Routes.REGISTER) },
            )
        }

        composable(Routes.REGISTER) {
            RegisterScreen(
                onRegisterSuccess = {
                    navController.navigate(Routes.HOME) {
                        popUpTo(Routes.LOGIN) { inclusive = true }
                    }
                },
                onNavigateToLogin = { navController.popBackStack() },
            )
        }

        composable(Routes.HOME) {
            HomeScreen(typeProfil = userTypeProfil)
        }

        composable(Routes.OFFRES) {
            OffresListScreen(
                onOffreClick = { offreId -> navController.navigate(Routes.offreDetail(offreId)) },
            )
        }

        composable(Routes.OFFRE_DETAIL) { backStackEntry ->
            val offreId = backStackEntry.arguments?.getString("offreId") ?: return@composable
            OffreDetailScreen(offreId = offreId)
        }

        composable(Routes.CANDIDATURES) {
            CandidaturesScreen()
        }

        composable(Routes.NOTIFICATIONS) {
            NotificationsScreen()
        }

        composable(Routes.PROFILE) {
            ProfileScreen()
        }
    }
}
