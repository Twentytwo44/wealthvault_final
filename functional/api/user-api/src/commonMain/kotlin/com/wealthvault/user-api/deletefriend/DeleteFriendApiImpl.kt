package com.wealthvault.`user-api`.deletefriend

import com.wealthvault.config.Config
import com.wealthvault.`user-api`.model.DeleteFriendResponse
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.delete
import io.ktor.http.ContentType
import io.ktor.http.contentType

class DeleteFriendApiImpl(private val client: HttpClient) : DeleteFriendApi {
    override suspend fun deleteFriend(id: String): Boolean {
        // 🌟 ยิง DELETE ไปที่ URL พร้อมแนบ id ไว้ท้ายสุด
        val response: DeleteFriendResponse = client.delete("${Config.localhost_android}friend/${id}") {
            contentType(ContentType.Application.Json)
        }.body()
        response.error?.let { error -> throw IllegalStateException(error) }
        return response.data?.success ?: true
    }
}
