package com.talentdafrique.app.data.remote.api

import com.talentdafrique.app.data.remote.dto.OffreDto
import com.talentdafrique.app.data.remote.dto.ProfileDto
import com.talentdafrique.app.data.remote.dto.StatsEcoleDto
import okhttp3.MultipartBody
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.PATCH
import retrofit2.http.PUT
import retrofit2.http.Part
import retrofit2.http.Path

/**
 * Miroir de app/routers/profiles.py. Une seule route de profil s'adapte au
 * type d'utilisateur connecté (le backend sait déjà qui pose la question via
 * son token) — c'est pour ça qu'il n'y a pas de sous-type dans l'URL ici.
 * Retrofit ne peut pas typer dynamiquement le retour selon le rôle, donc on
 * reçoit un JSON générique et on le désérialise manuellement dans le
 * repository vers le bon sous-type de ProfileDto (Etudiant / Entreprise /
 * Ecole / Freelance) en fonction de UserDto.typeProfil.
 */
interface ProfilesApi {

    @GET("profiles/me")
    suspend fun getMyProfileRaw(): Map<String, @JvmSuppressWildcards Any?>

    @PATCH("profiles/me")
    suspend fun updateMyProfile(@Body body: Map<String, @JvmSuppressWildcards Any?>): Map<String, @JvmSuppressWildcards Any?>

    // --- Espace école (section 2.4) ---

    @PUT("profiles/ecole/rattacher/{etudiantId}")
    suspend fun rattacherEtudiant(@Path("etudiantId") etudiantId: String)

    @GET("profiles/ecole/etudiants")
    suspend fun listEtudiants(): List<ProfileDto.Etudiant>

    @GET("profiles/ecole/suggestions/{etudiantId}")
    suspend fun suggestionsPourEtudiant(@Path("etudiantId") etudiantId: String): List<OffreDto>

    @GET("profiles/ecole/stats")
    suspend fun statsEmployabilite(): StatsEcoleDto
}

/** Miroir de app/routers/cv.py — upload et parsing automatique de CV. */
interface CvApi {
    @Multipart
    @retrofit2.http.POST("cv/upload")
    suspend fun uploadCv(@Part file: MultipartBody.Part): ProfileDto.Etudiant
}
