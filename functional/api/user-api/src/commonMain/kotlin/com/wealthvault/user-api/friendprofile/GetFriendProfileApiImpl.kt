package com.wealthvault.`user-api`.friendprofile

import com.wealthvault.config.Config
import com.wealthvault.`user-api`.model.FriendProfileResponse
import de.jensklingenberg.ktorfit.Ktorfit
import de.jensklingenberg.ktorfit.ktorfit
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get

class GetFriendProfileApiImpl(private val ktorfit: Ktorfit) : GetFriendProfileApi {
    override suspend fun getFriendProfile(id: String): FriendProfileResponse {
        // ยิง API ไปที่ /friend/{id}/profile
        val client = ktorfit.httpClient
        return client.get("${Config.localhost_android}friend/${id}/profile").body()
    }
}