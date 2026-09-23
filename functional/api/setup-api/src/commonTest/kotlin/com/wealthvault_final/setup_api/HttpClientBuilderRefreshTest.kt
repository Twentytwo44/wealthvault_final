package com.wealthvault.setup_api

import com.wealthvault.domain.auth.SessionTokenStore
import com.wealthvault.domain.auth.SessionTokens
import io.ktor.client.HttpClient
import io.ktor.client.engine.mock.MockEngine
import io.ktor.client.engine.mock.respond
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.http.HttpStatusCode
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import kotlinx.serialization.json.Json
import io.ktor.serialization.kotlinx.json.json
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class HttpClientBuilderRefreshTest {
    @Test
    fun transportFailurePreservesTheExistingSession() = runTest {
        val store = FakeSessionStore()
        val refreshClient = HttpClient(MockEngine { error("network unavailable") })
        val builder = HttpClientBuilder(
            json = Json,
            tokenStore = store,
            refreshClientFactory = { refreshClient },
        )

        assertNull(builder.refreshSession(store))
        assertEquals("access", store.accessToken.first())
        assertEquals("refresh", store.refreshToken.first())
    }

    @Test
    fun transientServerFailurePreservesTheExistingSession() = runTest {
        val store = FakeSessionStore()
        val refreshClient = HttpClient(
            MockEngine {
                respond(content = "", status = HttpStatusCode.ServiceUnavailable)
            },
        ) {
            expectSuccess = false
            install(ContentNegotiation) { json(Json) }
        }
        val builder = HttpClientBuilder(
            json = Json,
            tokenStore = store,
            refreshClientFactory = { refreshClient },
        )

        assertNull(builder.refreshSession(store))
        assertEquals("access", store.accessToken.first())
        assertEquals("refresh", store.refreshToken.first())
    }

    @Test
    fun explicitRefreshRejectionClearsTheSession() = runTest {
        val store = FakeSessionStore()
        val refreshClient = HttpClient(
            MockEngine {
                respond(content = "", status = HttpStatusCode.Unauthorized)
            },
        ) {
            expectSuccess = false
            install(ContentNegotiation) { json(Json) }
        }
        val builder = HttpClientBuilder(
            json = Json,
            tokenStore = store,
            refreshClientFactory = { refreshClient },
        )

        assertNull(builder.refreshSession(store))
        assertNull(store.accessToken.first())
        assertNull(store.refreshToken.first())
    }

    @Test
    fun successfulResponseWithInvalidPayloadClearsTheSession() = runTest {
        val store = FakeSessionStore()
        val refreshClient = HttpClient(
            MockEngine {
                respond(content = "not-json", status = HttpStatusCode.OK)
            },
        ) {
            expectSuccess = false
            install(ContentNegotiation) { json(Json) }
        }
        val builder = HttpClientBuilder(
            json = Json,
            tokenStore = store,
            refreshClientFactory = { refreshClient },
        )

        assertNull(builder.refreshSession(store))
        assertNull(store.accessToken.first())
        assertNull(store.refreshToken.first())
    }

    private class FakeSessionStore : SessionTokenStore {
        private val access = MutableStateFlow<String?>("access")
        private val refresh = MutableStateFlow<String?>("refresh")

        override val accessToken: Flow<String?> = access
        override val refreshToken: Flow<String?> = refresh

        override suspend fun saveTokens(tokens: SessionTokens) {
            access.value = tokens.accessToken
            refresh.value = tokens.refreshToken
        }

        override suspend fun clearTokens() {
            access.value = null
            refresh.value = null
        }
    }
}
