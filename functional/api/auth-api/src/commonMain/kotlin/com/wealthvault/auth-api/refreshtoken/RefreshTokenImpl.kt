package com.wealthvault.`auth-api`.refreshtoken


import com.wealthvault.`auth-api`.model.RefreshRequest
import com.wealthvault.`auth-api`.model.RefreshResponse
import com.wealthvault.config.Config
import de.jensklingenberg.ktorfit.Ktorfit
import io.ktor.client.call.body
import io.ktor.client.request.post
import io.ktor.client.request.setBody

class RefreshTokenImpl(private val ktorfit: Ktorfit) : RefreshTokenApi {
    override suspend fun refresh(request: RefreshRequest): RefreshResponse{

        val client = ktorfit.httpClient

        return client.post("${Config.localhost_ios}auth/refresh") {
            setBody(request)
        }.body()
    }
}
