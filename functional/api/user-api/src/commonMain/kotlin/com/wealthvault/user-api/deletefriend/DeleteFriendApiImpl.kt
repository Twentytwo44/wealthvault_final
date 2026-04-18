package com.wealthvault.`user-api`.deletefriend

import com.wealthvault.config.Config
import com.wealthvault.`user-api`.model.DeleteFriendResponse
import de.jensklingenberg.ktorfit.Ktorfit
import io.ktor.client.call.body
import io.ktor.client.request.delete
import io.ktor.http.ContentType
import io.ktor.http.contentType

class DeleteFriendApiImpl(private val ktorfit: Ktorfit) : DeleteFriendApi {
    override suspend fun deleteFriend(id: String): DeleteFriendResponse {
        val client = ktorfit.httpClient
        // 🌟 ยิง DELETE ไปที่ URL พร้อมแนบ id ไว้ท้ายสุด
        return client.delete("${Config.localhost_android}friend/${id}") {
            contentType(ContentType.Application.Json)
        }.body()
    }
}