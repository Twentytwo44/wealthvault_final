package com.wealthvault.investment_api.createcash


import com.wealthvault.config.Config
import com.wealthvault.notification_api.model.DeviceRequest
import com.wealthvault.notification_api.model.DeviceResponse
import de.jensklingenberg.ktorfit.Ktorfit
import io.ktor.client.call.body
import io.ktor.client.request.post
import io.ktor.client.request.setBody

class AddDevicesApiImpl(private val ktorfit: Ktorfit) : AddDevicesApi {
    override suspend fun addDevices(request: DeviceRequest): DeviceResponse {
        // ใช้ HttpClient ที่อยู่ใน Ktorfit ส่งค่าออกไปจริงๆ
        val client = ktorfit.httpClient

        return client.post("${Config.localhost_android}devices/register/") {
            setBody(request)
        }.body()
    }
}
