package com.wealthvault.websocket_api

import io.ktor.client.HttpClient
import io.ktor.client.plugins.websocket.webSocketSession
import io.ktor.client.request.header
import io.ktor.http.HttpHeaders
import io.ktor.websocket.Frame
import io.ktor.websocket.WebSocketSession
import io.ktor.websocket.close
import io.ktor.websocket.readText
import com.wealthvault.core.observability.platformLogger
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.isActive

interface WebSocketService {
    suspend fun connect(url: String): Flow<String>

    /** Connect with an Authorization header rather than a query-string token. */
    suspend fun connect(url: String, accessToken: String?): Flow<String> = connect(url)

    suspend fun send(message: String)
    suspend fun close()
    
    // Type-safe versions if needed
    // suspend fun <T> observeMessages(): Flow<T>
}

class WebSocketServiceImpl(
    private val client: HttpClient
) : WebSocketService {
    private val logger = platformLogger()
    private var session: WebSocketSession? = null

    override suspend fun connect(url: String): Flow<String> = flow {
        connectInternal(url, accessToken = null).collect { emit(it) }
    }

    override suspend fun connect(url: String, accessToken: String?): Flow<String> = flow {
        connectInternal(url, accessToken).collect { emit(it) }
    }

    private suspend fun connectInternal(url: String, accessToken: String?): Flow<String> = flow {
        try {
            session = client.webSocketSession(url) {
                accessToken?.takeIf { it.isNotBlank() }?.let {
                    header(HttpHeaders.Authorization, "Bearer $it")
                }
            }
            while (session?.isActive == true) {
                val frame = session?.incoming?.receive()
                if (frame is Frame.Text) {
                    emit(frame.readText())
                }
            }
        } catch (e: Exception) {
            logger.error("WebSocket connection failed", e)
            throw e
        }
    }

    override suspend fun send(message: String) {
        session?.send(Frame.Text(message))
    }

    override suspend fun close() {
        session?.close()
        session = null
    }
}
