package com.wealthvault.data.portfolio.land.transport.deleteland

interface DeleteLandApi {
    suspend fun deleteLand(id: String)
}
