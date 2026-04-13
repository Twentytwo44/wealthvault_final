package com.wealthvault.share_api.unsharegroup


import com.wealthvault.config.Config
import com.wealthvault.share_api.model.ShareItemResponse
import de.jensklingenberg.ktorfit.Ktorfit
import io.ktor.client.call.body
import io.ktor.client.request.delete

class UnShareGroupApiImpl(private val ktorfit: Ktorfit) : UnShareGroupApi {
    override suspend fun unShareGroup(id:String): ShareItemResponse {
        val client = ktorfit.httpClient

        return client.delete("${Config.localhost_android}group/item/${id}/") {
        }.body()
    }
}
