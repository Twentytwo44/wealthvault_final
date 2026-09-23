package com.wealthvault.data.auth

import com.wealthvault.data.auth.transport.registerdevice.RegisterDeviceApiImpl
import com.wealthvault.data.auth.transport.unregisterdevice.UnregisterDeviceApiImpl
import io.ktor.client.HttpClient
import io.ktor.client.engine.mock.MockEngine
import io.ktor.client.engine.mock.respond
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.request.header
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import io.ktor.http.headersOf
import io.ktor.serialization.kotlinx.json.json
import kotlinx.coroutines.test.runTest
import kotlinx.serialization.json.Json
import kotlin.test.Test
import kotlin.test.assertEquals

class DeviceApiImplTest {
    @Test
    fun registerDeviceKeepsEndpointAndMapsMutationResponse() = runTest {
        var path = ""
        val client = HttpClient(MockEngine { request ->
            path = request.url.encodedPath
            respond(
                content = "{\"message\":\"registered\",\"success\":true}",
                status = HttpStatusCode.OK,
                headers = headersOf(HttpHeaders.ContentType, "application/json"),
            )
        }) {
            install(ContentNegotiation) { json(Json { ignoreUnknownKeys = true }) }
            defaultRequest {
                header(HttpHeaders.ContentType, ContentType.Application.Json)
            }
        }

        val result = RegisterDeviceApiImpl(client).registerDevice("token", "android", "pixel")

        kotlin.test.assertTrue(path.endsWith("/devices/register/"), path)
        assertEquals("registered", result.message)
        assertEquals(true, result.success)
    }

    @Test
    fun unregisterDeviceKeepsEndpointAndMapsMutationResponse() = runTest {
        var path = ""
        val client = HttpClient(MockEngine { request ->
            path = request.url.encodedPath
            respond(
                content = "{\"message\":\"registered\",\"success\":true}",
                status = HttpStatusCode.OK,
                headers = headersOf(HttpHeaders.ContentType, "application/json"),
            )
        }) {
            install(ContentNegotiation) { json(Json { ignoreUnknownKeys = true }) }
            defaultRequest {
                header(HttpHeaders.ContentType, ContentType.Application.Json)
            }
        }

        val result = UnregisterDeviceApiImpl(client).unregisterDevice("token")

        kotlin.test.assertTrue(path.endsWith("/devices/unregister/"), path)
        assertEquals("registered", result.message)
        assertEquals(true, result.success)
    }

}
