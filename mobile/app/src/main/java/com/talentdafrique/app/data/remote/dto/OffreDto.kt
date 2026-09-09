package com.talentdafrique.app.data.remote.dto

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

enum class TypeContrat {
    @Json(name = "stage") STAGE,
    @Json(name = "cdd") CDD,
    @Json(name = "cdi") CDI,
    @Json(name = "mission") MISSION, // freelance — étape 8
}

enum class StatutOffre {
    @Json(name = "active") ACTIVE,
    @Json(name = "expiree") EXPIREE,
    @Json(name = "archivee") ARCHIVEE,
}

@JsonClass(generateAdapter = true)
data class OffreDto(
    val id: String,
    @Json(name = "entreprise_id") val entrepriseId: String,
    val titre: String,
    val description: String,
    @Json(name = "type_contrat") val typeContrat: TypeContrat,
    @Json(name = "competences_obligatoires") val competencesObligatoires: List<String> = emptyList(),
    @Json(name = "competences_souhaitees") val competencesSouhaitees: List<String> = emptyList(),
    @Json(name = "soft_skills") val softSkills: List<String> = emptyList(),
    @Json(name = "niveau_experience") val niveauExperience: Int? = null,
    val disponibilite: String? = null,
    val localisation: String? = null,
    val remuneration: String? = null,
    @Json(name = "budget_tjm") val budgetTjm: Double? = null, // pertinent si type_contrat = MISSION
    val statut: StatutOffre,
    // Présent uniquement quand l'offre est renvoyée dans un contexte de matching
    // (ex: liste de recommandations pour un candidat)
    val matching: MatchingDetailDto? = null,
)

@JsonClass(generateAdapter = true)
data class MatchingDetailDto(
    @Json(name = "score_global") val scoreGlobal: Double,
    @Json(name = "score_competences_obligatoires") val scoreCompetencesObligatoires: Double,
    @Json(name = "score_competences_souhaitees") val scoreCompetencesSouhaitees: Double,
    @Json(name = "score_experience") val scoreExperience: Double, // ou score TJM si mission
    @Json(name = "score_disponibilite") val scoreDisponibilite: Double,
    @Json(name = "score_soft_skills") val scoreSoftSkills: Double,
    val recommandee: Boolean,
)

enum class StatutCandidature {
    @Json(name = "recue") RECUE,
    @Json(name = "en_cours") EN_COURS,
    @Json(name = "entretien") ENTRETIEN,
    @Json(name = "refusee") REFUSEE,
    @Json(name = "acceptee") ACCEPTEE,
}

@JsonClass(generateAdapter = true)
data class CandidatureDto(
    val id: String,
    @Json(name = "offre_id") val offreId: String,
    @Json(name = "candidat_id") val candidatId: String,
    val statut: StatutCandidature,
    val message: String?,
    val matching: MatchingDetailDto? = null,
    // Enrichissement pratique côté UI (facultatif selon ta route)
    val offre: OffreDto? = null,
)

@JsonClass(generateAdapter = true)
data class CandidatureCreateRequest(
    @Json(name = "offre_id") val offreId: String,
    val message: String? = null,
)

@JsonClass(generateAdapter = true)
data class StatutUpdateRequest(
    val statut: StatutCandidature,
)

@JsonClass(generateAdapter = true)
data class NotificationDto(
    val id: String,
    val titre: String,
    val message: String,
    val lue: Boolean,
    @Json(name = "created_at") val createdAt: String,
)
