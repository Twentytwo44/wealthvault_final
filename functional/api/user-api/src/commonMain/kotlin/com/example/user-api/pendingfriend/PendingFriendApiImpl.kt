package com.example.`user-api`.pendingfriend

import com.example.`user-api`.model.PendingFriendResponse
import com.wealthvault.config.Config
import de.jensklingenberg.ktorfit.Ktorfit
import io.ktor.client.call.body
import io.ktor.client.request.get

class PendingFriendApiImpl(private val ktorfit: Ktorfit) : PendingFriendApi {
    override suspend fun pendingfriend(): PendingFriendResponse {
        // ใช้ HttpClient ที่อยู่ใน Ktorfit ส่งค่าออกไปจริงๆ
        val client = ktorfit.httpClient

        // ยิงเองตรงๆ แบบไม่ง้อ Generator
        return client.get("${Config.localhost_android}/friend/pending") {
            // สำหรับ GET ไม่ต้องใส่ setBody() ครับ
            // หากต้องการส่งค่าเพิ่ม ให้ใช้ parameter() แทน เช่น parameter("page", 1)
        }.body()
    }
}
