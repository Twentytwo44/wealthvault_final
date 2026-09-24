package com.wealthvault.data.social.repository.websocket

import com.wealthvault.core.observability.platformLogger
import io.ktor.client.HttpClient
import io.ktor.client.plugins.websocket.webSocketSession
import io.ktor.client.request.header
import io.ktor.http.HttpHeaders
import io.ktor.websocket.Frame
import io.ktor.websocket.WebSocketSession
import io.ktor.websocket.close
import io.ktor.websocket.readText
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.isActive

/**
 * Data-layer transport for group chat. The domain only sees GroupChatGateway;
 * Ktor and WebSocketSession stay inside the social bounded context.
 */
internal interface WebSocketTransport {
    suspend fun connect(url: String): Flow<String>

    suspend fun connect(url: String, accessToken: String?): Flow<String> = connect(url)

    suspend fun send(message: String)
    suspend fun close()
}

internal class KtorWebSocketTransport(
    private val client: HttpClient,
) : WebSocketTransport {
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
            require(url.startsWith("wss://")) {
                "Production WebSocket connections must use WSS"
            }
            session = client.webSocketSession(url) {
                accessToken?.takeIf { it.isNotBlank() }?.let {
                    header(HttpHeaders.Authorization, "Bearer $it")
                }
            }
            while (session?.isActive == true) {
                val frame = session?.incoming?.receive()
                if (frame is Frame.Text) emit(frame.readText())
            }
        } catch (error: CancellationException) {
            throw error
        } catch (error: Throwable) {
            logger.error("WebSocket connection failed", error)
            throw error
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
