package com.wealthvault.liability_api.deleteliability

interface DeleteLiabilityApi {
    suspend fun deleteLiability(id: String)
}
