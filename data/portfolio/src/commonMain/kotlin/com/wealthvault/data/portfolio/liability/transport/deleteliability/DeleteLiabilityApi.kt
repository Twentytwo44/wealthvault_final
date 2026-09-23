package com.wealthvault.data.portfolio.liability.transport.deleteliability

interface DeleteLiabilityApi {
    suspend fun deleteLiability(id: String)
}
