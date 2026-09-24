package com.wealthvault.data.auth.transport

import com.wealthvault.config.Config
import com.wealthvault.data.auth.transport.refreshtoken.RefreshTokenImpl
import io.ktor.client.HttpClient
import io.ktor.client.engine.mock.MockEngine
import io.ktor.client.engine.mock.respond
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import io.ktor.http.headersOf
import io.ktor.serialization.kotlinx.json.json
import kotlinx.coroutines.test.runTest
import kotlinx.serialization.json.Json
import kotlin.test.Test
import kotlin.test.assertEquals

class RefreshTokenApiTest {
    @Test
    fun refreshUsesThePlatformNeutralApiEndpointAndMapsTheSession() = runTest {
        val client = HttpClient(
            MockEngine { request ->
                assertEquals("${Config.apiBaseUrl}auth/refresh", request.url.toString())
                respond(
                    content = """
                        {
                          "status": "refresh success",
                          "data": {
                            "success": true,
                            "access_token": "new-access",
                            "refresh_token": "new-refresh",
                            "user_id": "user-1"
                          }
                        }
                    """.trimIndent(),
                    status = HttpStatusCode.OK,
                    headers = headersOf(HttpHeaders.ContentType, ContentType.Application.Json.toString()),
                )
            },
        ) {
            install(ContentNegotiation) {
                json(Json { ignoreUnknownKeys = true })
            }
        }

        val session = RefreshTokenImpl(client).refresh("old-refresh")

        assertEquals("user-1", session.userId)
        assertEquals("new-access", session.accessToken)
        assertEquals("new-refresh", session.refreshToken)
        client.close()
    }
}
