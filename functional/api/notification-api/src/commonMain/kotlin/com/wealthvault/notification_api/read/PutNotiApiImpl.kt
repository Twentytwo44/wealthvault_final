package com.wealthvault.notification_api.read


import com.wealthvault.config.Config
import com.wealthvault.notification_api.model.NotificationResponse
import de.jensklingenberg.ktorfit.Ktorfit
import io.ktor.client.call.body
import io.ktor.client.request.put

class PutNotiApiImpl(private val ktorfit: Ktorfit) : PutNotiApi {
    override suspend fun putNoti(id: String): NotificationResponse {
        // ใช้ HttpClient ที่อยู่ใน Ktorfit ส่งค่าออกไปจริงๆ
        val client = ktorfit.httpClient

        return client.put("${Config.localhost_android}notifications/${id}/") {

        }.body()
    }
}
