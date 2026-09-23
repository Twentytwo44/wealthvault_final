package com.wealthvault.data.auth.transport.registerdevice

import com.wealthvault.config.Config
import com.wealthvault.data.auth.transport.model.DeviceMutationResponse
import com.wealthvault.data.auth.transport.model.RegisterDeviceRequest
import com.wealthvault.data.auth.wire.toDomain
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.post
import io.ktor.client.request.setBody

internal class RegisterDeviceApiImpl(private val client: HttpClient) : RegisterDeviceApi {
    override suspend fun registerDevice(
        token: String?,
        platform: String?,
        deviceName: String?,
    ) = client.post("${Config.localhost_android}devices/register/") {
        setBody(RegisterDeviceRequest(token = token, platform = platform, deviceName = deviceName))
    }.body<DeviceMutationResponse>().toDomain()
}
