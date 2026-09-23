package com.wealthvault.`user-api`.friend

import com.wealthvault.config.Config
import com.wealthvault.`user-api`.model.FriendResponse
import com.wealthvault.user_api.requireDomainFriends
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get

class FriendApiImpl(private val client: HttpClient) : FriendApi {
    override suspend fun getFriend() = client
        .get("${Config.localhost_android}friend/")
        .body<FriendResponse>()
        .requireDomainFriends()
}
