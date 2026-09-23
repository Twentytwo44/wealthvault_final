package com.wealthvault.dashboard.data

import com.wealthvault.core.architecture.AppResult
import com.wealthvault.core.architecture.toAppError
import com.wealthvault.core.model.DashboardData
import com.wealthvault.config.Config
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import kotlinx.coroutines.CancellationException

interface DashboardRemoteDataSource {
    suspend fun fetchDashboardData(): AppResult<DashboardData>
}

internal class DashboardDataSource(
    private val client: HttpClient,
) : DashboardRemoteDataSource {
    override suspend fun fetchDashboardData(): AppResult<DashboardData> = try {
        val response = client
            .get("${Config.localhost_android}dashboard")
            .body<DashboardWireResponse>()
        AppResult.Success(response.toDomain())
    } catch (error: CancellationException) {
        throw error
    } catch (error: Throwable) {
        AppResult.Failure(error.toAppError())
    }
}
