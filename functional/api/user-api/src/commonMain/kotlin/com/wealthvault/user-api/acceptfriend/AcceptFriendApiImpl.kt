package com.wealthvault.`user-api`.acceptfriend

import com.wealthvault.`user-api`.model.AcceptFriendRequest
import com.wealthvault.`user-api`.model.AcceptFriendResponse
import com.wealthvault.config.Config
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.post
import io.ktor.client.request.setBody

class AcceptFriendApiImpl(private val client: HttpClient) : AcceptFriendApi {
    override suspend fun acceptFriend(requesterId: String, action: String): String? {
        val response: AcceptFriendResponse = client.post("${Config.localhost_android}friend/accept") {
            setBody(AcceptFriendRequest(requesterId = requesterId, action = action))
        }.body()
        response.error?.let { error -> throw IllegalStateException(error) }
        return response.data?.success
    }
}
