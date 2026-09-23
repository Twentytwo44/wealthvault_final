package com.wealthvault.notification_api.readall

import com.wealthvault.config.Config
import com.wealthvault.notification_api.model.NotificationResponse
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.put

class PutNotiReadAllApiImpl(private val client: HttpClient) : PutNotiReadAllApi {

    // 🌟 แก้ชื่อฟังก์ชันให้ตรงกับ Interface และเอา id ออก
    override suspend fun putNotiReadAll() {
        // 🌟 เรียก put ไปที่ endpoint
        val response: NotificationResponse = client.put("${Config.localhost_android}notifications/read-all") {
            // ถ้าอนาคตมี Body ให้ใส่ตรงนี้ (เช่น setBody(...))
        }.body()
        response.error?.let { error -> throw IllegalStateException(error) }
    }
}
