package com.wealthvault.notification_api.getalldevice

import com.wealthvault.config.Config
import com.wealthvault.notification_api.toDomain
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get

class GetAllDeviceApiImpl(private val client: HttpClient) : GetAllDeviceApi {
    override suspend fun getAllDevices(): List<com.wealthvault.domain.notification.DeviceInfo> =
        client.get("${Config.localhost_android}/devices/") {

        }.body<com.wealthvault.notification_api.model.GetDeviceResponse>().toDomain()
}
