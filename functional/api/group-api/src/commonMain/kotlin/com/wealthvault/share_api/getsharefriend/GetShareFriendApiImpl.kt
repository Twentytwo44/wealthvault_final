package com.wealthvault.share_api.getsharefriend

import com.wealthvault.config.Config
import com.wealthvault.share_api.requireDomainData
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get

class GetShareFriendApiImpl(private val client: HttpClient) : GetShareFriendApi {
    override suspend fun getShareFriend(id:String) = client
        .get("${Config.localhost_android}friend/$id/item/")
        .body<com.wealthvault.share_api.model.ShareFriendResponse>()
        .requireDomainData()
}
