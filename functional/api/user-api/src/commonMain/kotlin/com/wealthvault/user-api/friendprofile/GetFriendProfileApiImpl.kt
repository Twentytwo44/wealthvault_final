package com.wealthvault.`user-api`.friendprofile

import com.wealthvault.config.Config
import com.wealthvault.`user-api`.model.FriendProfileResponse
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import com.wealthvault.user_api.requireDomainProfile

class GetFriendProfileApiImpl(private val client: HttpClient) : GetFriendProfileApi {
    override suspend fun getFriendProfile(id: String) = client
        .get("${Config.localhost_android}friend/${id}/profile")
        .body<FriendProfileResponse>()
        .requireDomainProfile()
}
