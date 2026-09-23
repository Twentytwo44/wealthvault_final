package com.wealthvault.user_api.closefriend

import com.wealthvault.config.Config // 🌟 นำเข้า Config (ถ้ามี)
import com.wealthvault.`user-api`.closefriend.CloseFriendApi
import com.wealthvault.user_api.requireDomainCloseFriends
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
// 🌟 อย่าลืม import UserDataResponse ด้วยนะครับ

class CloseFriendApiImpl(private val client: HttpClient) : CloseFriendApi {
    override suspend fun getCloseFriend() =
        client.get("${Config.localhost_android}closefriend") {
            // Authentication is supplied by the shared client.
        }.body<com.wealthvault.`user-api`.model.CloseFriendResponse>().requireDomainCloseFriends()
}
