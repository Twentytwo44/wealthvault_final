package com.example.liability_api.updateliability



import com.example.liability_api.model.LiabilityRequest
import com.example.liability_api.model.LiabilityResponse
import de.jensklingenberg.ktorfit.http.Body
import de.jensklingenberg.ktorfit.http.PATCH
import de.jensklingenberg.ktorfit.http.Path

interface UpdateLiabilityApi {
    @PATCH("asset/lia/{id}")
    suspend fun updateLiability(@Path("id") id: String, @Body request: LiabilityRequest): LiabilityResponse
}
