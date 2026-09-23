package com.wealthvault.network


import com.wealthvault.config.Config
import com.wealthvault.core.observability.AppLogger
import com.wealthvault.core.observability.NoOpAppLogger
import com.wealthvault.domain.auth.SessionTokenStore
import com.wealthvault.domain.auth.SessionTokens
import com.wealthvault.network.platformHttpClient
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.plugins.DefaultRequest
import io.ktor.client.plugins.HttpSend
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.plugins.HttpRequestRetry
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.plugin
import io.ktor.client.request.header
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import io.ktor.http.HttpMethod
import io.ktor.http.contentType
import io.ktor.serialization.kotlinx.json.json
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.first
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json


class HttpClientBuilder(
    private val json: Json,
    private val tokenStore: SessionTokenStore? = null,
    private val logger: AppLogger = NoOpAppLogger,
    private val refreshClientFactory: (() -> HttpClient)? = null,
) {
    private val refreshCoordinator = tokenStore?.let { store ->
        SessionRefreshCoordinator(store) { refreshSession(store) }
    }

    fun build(withAuth: Boolean = true): HttpClient {
        Config.requireSecureTransport()
        val client = platformHttpClient {
            install(ContentNegotiation) {
                // Reuse the composition-root serializer instead of creating a
                // new Json instance for every client singleton.
                json(json, contentType = ContentType.Any)
            }

            install(DefaultRequest) {
                header(HttpHeaders.ContentType, ContentType.Application.Json)
            }

            install(HttpTimeout) {
                requestTimeoutMillis = 30_000
                connectTimeoutMillis = 10_000
                socketTimeoutMillis = 30_000
            }

            // Only retry idempotent requests. Mutations (POST/PATCH/DELETE) must
            // be retried by their caller with an explicit idempotency policy.
            install(HttpRequestRetry) {
                retryIf(maxRetries = 2) { request, response ->
                    request.method.isIdempotent() &&
                        (response.status == HttpStatusCode.TooManyRequests ||
                            response.status.value in 500..599)
                }
                retryOnExceptionIf(maxRetries = 2) { request, _ ->
                    request.method.isIdempotent()
                }
                exponentialDelay()
            }

            // 🌟 ลบ install(Auth) ทิ้ง แล้วใช้ install(HttpSend) แทน
            if (withAuth && tokenStore != null) {
                install(HttpSend) {
                    // กำหนดให้ลองยิงใหม่ได้สูงสุด 2 รอบ (กันลูปอินฟินิตี้)
                    maxSendCount = 2
                }
            }
        }

        // 🌟 เขียน Logic ดักจับ Request / Response ด้วยตัวเอง
        if (withAuth && tokenStore != null) {
            client.plugin(HttpSend).intercept { request ->

                // 1. เช็กว่าไม่ใช่เส้น Auth/Login ค่อยแปะ Token
                val isAuthRoute = request.url.pathSegments.contains("auth") ||
                        request.url.pathSegments.contains("login")

                if (!isAuthRoute) {
                    val accessToken = tokenStore.accessToken.first()
                    if (!accessToken.isNullOrBlank()) {
                        request.header(HttpHeaders.Authorization, "Bearer $accessToken")
                    }
                }

                // 2. 🚀 ปล่อย Request วิ่งออกไปหา Backend
                var originalCall = execute(request)

                // 3. 🚨 ถ้า Backend ตอบ 401 กลับมา (โดยไม่ต้องสน Header WWW-Authenticate!)
                if (originalCall.response.status == HttpStatusCode.Unauthorized && !isAuthRoute) {
                    logger.debug("Authenticated request received 401; refreshing session")

                    val failedAccessToken = tokenStore.accessToken.first()
                    val newAccessToken = refreshCoordinator?.refreshAfterUnauthorized(failedAccessToken)

                    // A 401 may refresh the session for subsequent calls, but
                    // only idempotent requests are safe to replay here. POST,
                    // PATCH and DELETE can have already reached the backend.
                    if (!newAccessToken.isNullOrBlank() && request.method.isIdempotent()) {
                        request.headers.remove(HttpHeaders.Authorization)
                        request.header(HttpHeaders.Authorization, "Bearer $newAccessToken")
                        originalCall = execute(request)
                    }
                }

                // คืนค่าผลลัพธ์กลับไปให้ UI
                originalCall
            }
        }

        return client
    }

    /** Internal for deterministic boundary tests; production callers use the interceptor. */
    internal suspend fun refreshSession(tokenStore: SessionTokenStore): String? {
        val currentRefreshToken = tokenStore.refreshToken.first()
        if (currentRefreshToken.isNullOrBlank()) {
            logger.warn("Refresh skipped because no refresh token is available")
            tokenStore.clearTokens()
            return null
        }

        // Refresh is deliberately sent through a client without the auth
        // interceptor. This prevents a failed refresh from recursively
        // triggering another refresh attempt.
        val refreshClient = refreshClientFactory?.invoke() ?: platformHttpClient {
            // Inspect 401/403 as ordinary responses so the explicit rejection
            // branch below can clear the session deliberately. Transport and
            // decoding failures remain transient and preserve the session.
            expectSuccess = false
            install(ContentNegotiation) {
                json(json, contentType = ContentType.Any)
            }
            install(HttpTimeout) {
                requestTimeoutMillis = 30_000
                connectTimeoutMillis = 10_000
                socketTimeoutMillis = 30_000
            }
        }

        return try {
            val response = refreshClient.post("${Config.apiBaseUrl}auth/refresh/") {
                setBody(RefreshRequestPayload(currentRefreshToken))
                contentType(ContentType.Application.Json)
            }

            // A temporary outage must not log the user out. Only an explicit
            // authentication rejection means the refresh token is no longer
            // valid; callers can retry the request after a later network
            // recovery without losing the local session.
            if (response.status.value !in 200..299) {
                if (response.status.value == 401 || response.status.value == 403) {
                    tokenStore.clearTokens()
                }
                logger.warn("Session refresh returned HTTP ${response.status.value}")
                return null
            }

            val payload: RefreshResponsePayload = try {
                response.body()
            } catch (error: CancellationException) {
                throw error
            } catch (error: Throwable) {
                // A successful response with an unreadable token payload is
                // not a transient transport outage; the session cannot be
                // trusted, so clear it instead of keeping corrupt state.
                logger.warn("Session refresh returned an invalid token payload", error)
                tokenStore.clearTokens()
                return null
            }

            val newAccess = payload.data?.accessToken
            val newRefresh = payload.data?.refreshToken
            if (!newAccess.isNullOrBlank() && !newRefresh.isNullOrBlank()) {
                tokenStore.saveTokens(SessionTokens(newAccess, newRefresh))
                newAccess
            } else {
                tokenStore.clearTokens()
                null
            }
        } catch (error: CancellationException) {
            throw error
        } catch (error: Throwable) {
            logger.warn("Session refresh failed", error)
            // Transport failures are transient. Keep the current secure
            // session so a later request can retry refresh instead of forcing
            // the user through login while the backend/network recovers.
            null
        } finally {
            refreshClient.close()
        }
    }
}

/**
 * Refresh transport stays private to the authenticated client.  Keeping this
 * payload here removes the setup layer's dependency on the legacy auth API
 * module while preserving the backend JSON contract exactly.
 */
@Serializable
private data class RefreshRequestPayload(
    @SerialName("refreshtoken") val refreshToken: String,
)

@Serializable
private data class RefreshResponsePayload(
    val data: RefreshDataPayload? = null,
)

@Serializable
private data class RefreshDataPayload(
    @SerialName("access_token") val accessToken: String? = null,
    @SerialName("refresh_token") val refreshToken: String? = null,
)

internal fun HttpMethod.isIdempotent(): Boolean = this == HttpMethod.Get ||
    this == HttpMethod.Head ||
    this == HttpMethod.Options ||
    this == HttpMethod.Put
