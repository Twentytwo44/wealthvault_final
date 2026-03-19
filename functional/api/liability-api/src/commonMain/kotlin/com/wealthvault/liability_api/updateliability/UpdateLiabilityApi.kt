package com.wealthvault.liability_api.updateliability



import com.wealthvault.liability_api.model.LiabilityRequest
import com.wealthvault.liability_api.model.LiabilityResponse
import de.jensklingenberg.ktorfit.http.Body
import de.jensklingenberg.ktorfit.http.PATCH
import de.jensklingenberg.ktorfit.http.Path

interface UpdateLiabilityApi {
    @PATCH("ic_nav_asset/lia/{id}")
    suspend fun updateLiability(@Path("id") id: String, @Body request: LiabilityRequest): LiabilityResponse
}
