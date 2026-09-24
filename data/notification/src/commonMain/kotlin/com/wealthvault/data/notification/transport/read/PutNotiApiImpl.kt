package com.wealthvault.data.notification.transport.read


import com.wealthvault.config.Config
import com.wealthvault.data.notification.transport.model.NotificationResponse
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.put

class PutNotiApiImpl(private val client: HttpClient) : PutNotiApi {
    override suspend fun putNoti(id: String) {
        val response: NotificationResponse = client.put("${Config.localhost_android}notifications/$id").body()
        response.error?.let { error -> throw IllegalStateException(error) }
    }
}
