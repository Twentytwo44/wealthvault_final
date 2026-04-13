package com.wealthvault.share_api.unsharefriend


import com.wealthvault.config.Config
import com.wealthvault.share_api.model.ShareItemResponse
import de.jensklingenberg.ktorfit.Ktorfit
import io.ktor.client.call.body
import io.ktor.client.request.delete

class UnShareFriendApiImpl(private val ktorfit: Ktorfit) : UnShareFriendApi {
    override suspend fun unShareFriend(id:String): ShareItemResponse {
        val client = ktorfit.httpClient

        return client.delete("${Config.localhost_android}friend/item/${id}/") {
        }.body()
    }
}
