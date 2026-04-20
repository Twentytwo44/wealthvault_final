package com.wealthvault.notification_api.notification

import com.wealthvault.config.Config
import com.wealthvault.notification_api.model.NotificationResponse
import de.jensklingenberg.ktorfit.Ktorfit
import io.ktor.client.call.body
import io.ktor.client.request.get

class GetNotificationsApiImpl(private val ktorfit: Ktorfit) : GetNotificationsApi {
    override suspend fun getNotifications(): NotificationResponse {
        // ใช้ HttpClient ที่อยู่ใน Ktorfit ส่งค่าออกไปจริงๆ
        val client = ktorfit.httpClient

        return client.get("${Config.localhost_android}notifications/") {

        }.body()
    }
}
