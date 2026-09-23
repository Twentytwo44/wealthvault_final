package com.wealthvault.notification_api.unregisterdevice


import com.wealthvault.config.Config
import com.wealthvault.notification_api.model.UnDeviceRequest
import com.wealthvault.notification_api.toDomain
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.post
import io.ktor.client.request.setBody

class UnDevicesApiImpl(private val client: HttpClient) : UnDevicesApi {
    override suspend fun unDevices(token: String): com.wealthvault.domain.notification.DeviceMutationResult {
        return client.post("${Config.localhost_android}devices/unregister/") {
            setBody(UnDeviceRequest(token = token))
        }.body<com.wealthvault.notification_api.model.DeviceResponse>().toDomain()
    }
}
