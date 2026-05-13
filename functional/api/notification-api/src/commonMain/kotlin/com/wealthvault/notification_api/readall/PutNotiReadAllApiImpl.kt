package com.wealthvault.notification_api.readall

import com.wealthvault.config.Config
import com.wealthvault.notification_api.model.NotificationResponse
import de.jensklingenberg.ktorfit.Ktorfit
import io.ktor.client.call.body
import io.ktor.client.request.put

class PutNotiReadAllApiImpl(private val ktorfit: Ktorfit) : PutNotiReadAllApi {

    // 🌟 แก้ชื่อฟังก์ชันให้ตรงกับ Interface และเอา id ออก
    override suspend fun putNotiReadAll(): NotificationResponse {
        val client = ktorfit.httpClient

        // 🌟 เรียก put ไปที่ endpoint
        return client.put("${Config.localhost_android}notifications/read-all") {
            // ถ้าอนาคตมี Body ให้ใส่ตรงนี้ (เช่น setBody(...))
        }.body()
    }
}