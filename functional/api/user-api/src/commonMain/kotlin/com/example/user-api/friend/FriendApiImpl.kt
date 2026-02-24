package com.example.`user-api`.friend

import com.example.`user-api`.model.FriendResponse
import com.wealthvault.config.Config
import de.jensklingenberg.ktorfit.Ktorfit
import io.ktor.client.call.body
import io.ktor.client.request.get

class FriendApiImpl(private val ktorfit: Ktorfit) : FriendApi {
    override suspend fun getFriend(): FriendResponse {
        // ใช้ HttpClient ที่อยู่ใน Ktorfit ส่งค่าออกไปจริงๆ
        val client = ktorfit.httpClient

        return client.get("${Config.localhost_android}/friend") {

        }.body()
    }
}
