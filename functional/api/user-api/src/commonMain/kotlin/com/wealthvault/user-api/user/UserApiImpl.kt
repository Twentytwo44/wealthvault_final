package com.wealthvault.`user-api`.user

import com.wealthvault.config.Config
import com.wealthvault.user_api.requireDomainUser
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get

class UserApiImpl(private val client: HttpClient) : UserApi {
    override suspend fun getUser() =
        client.get("${Config.localhost_android}user/") {

        }.body<com.wealthvault.`user-api`.model.UserDataResponse>().requireDomainUser()
}
