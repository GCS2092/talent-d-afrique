package com.talentdafrique.app.data.remote.dto

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

/**
 * Miroir Kotlin de app/schemas/profiles.py : un compte a toujours exactement
 * un profil parmi ces 4 types. Le sealed interface permet un `when` exhaustif
 * côté UI (le compilateur t'oblige à gérer les 4 cas, jamais d'oubli).
 */
sealed interface ProfileDto {

    @JsonClass(generateAdapter = true)
    data class Etudiant(
        val id: String,
        @Json(name = "ecole_id") val ecoleId: String?,
        @Json(name = "cv_url") val cvUrl: String?,
        val competences: List<String> = emptyList(),
        val disponibilite: String? = null,
        val experience: Int? = null, // en années, si applicable
    ) : ProfileDto

    @JsonClass(generateAdapter = true)
    data class Entreprise(
        val id: String,
        val secteur: String?,
        val taille: String?,
        val description: String?,
        val localisation: String?,
        @Json(name = "logo_url") val logoUrl: String? = null,
    ) : ProfileDto

    @JsonClass(generateAdapter = true)
    data class Ecole(
        val id: String,
        @Json(name = "nom_etablissement") val nomEtablissement: String,
        val localisation: String?,
    ) : ProfileDto

    @JsonClass(generateAdapter = true)
    data class Freelance(
        val id: String,
        val competences: List<String> = emptyList(),
        val tjm: Double?,
        val disponibilite: String? = null,
    ) : ProfileDto
}

/** Statistiques d'employabilité renvoyées à une école (section 2.4 du cahier des charges). */
@JsonClass(generateAdapter = true)
data class StatsEcoleDto(
    @Json(name = "nb_etudiants") val nbEtudiants: Int,
    @Json(name = "candidatures_envoyees") val candidaturesEnvoyees: Int,
    @Json(name = "candidatures_acceptees") val candidaturesAcceptees: Int,
    @Json(name = "taux_placement") val tauxPlacement: Double,
)
