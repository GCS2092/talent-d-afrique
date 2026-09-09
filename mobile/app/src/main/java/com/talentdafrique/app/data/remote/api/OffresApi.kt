package com.talentdafrique.app.data.remote.api

import com.talentdafrique.app.data.remote.dto.OffreDto
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

/** Miroir de app/routers/offres.py */
interface OffresApi {

    /** Liste publique — offres actives par défaut, recommandations triées si candidat connecté. */
    @GET("offres")
    suspend fun listOffres(
        @Query("statut") statut: String? = null,
        @Query("type_contrat") typeContrat: String? = null,
    ): List<OffreDto>

    @GET("offres/{id}")
    suspend fun getOffre(@Path("id") id: String): OffreDto

    /** "Mes offres" côté entreprise, inclut les archivées. */
    @GET("offres/mine")
    suspend fun getMyOffres(): List<OffreDto>

    @POST("offres")
    suspend fun createOffre(@Body body: OffreDto): OffreDto

    @PATCH("offres/{id}")
    suspend fun updateOffre(@Path("id") id: String, @Body body: Map<String, @JvmSuppressWildcards Any?>): OffreDto

    @DELETE("offres/{id}")
    suspend fun deleteOffre(@Path("id") id: String)
}
