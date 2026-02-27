package com.example.liability_api.deleteliability

import com.example.liability_api.model.DeleteLiabilityResponse
import de.jensklingenberg.ktorfit.http.DELETE
import de.jensklingenberg.ktorfit.http.Path

interface DeleteLiabilityApi {
    @DELETE("asset/lia/{id}")
    suspend fun deleteLiability(@Path("id") id: String): DeleteLiabilityResponse
}
