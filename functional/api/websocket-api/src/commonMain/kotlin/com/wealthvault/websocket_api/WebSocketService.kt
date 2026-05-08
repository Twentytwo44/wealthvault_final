package com.wealthvault.websocket_api

import io.ktor.client.HttpClient
import io.ktor.client.plugins.websocket.webSocketSession
import io.ktor.websocket.Frame
import io.ktor.websocket.WebSocketSession
import io.ktor.websocket.close
import io.ktor.websocket.readText
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.isActive

interface WebSocketService {
    suspend fun connect(url: String): Flow<String>
    suspend fun send(message: String)
    suspend fun close()
    
    // Type-safe versions if needed
    // suspend fun <T> observeMessages(): Flow<T>
}

class WebSocketServiceImpl(
    private val client: HttpClient
) : WebSocketService {
    private var session: WebSocketSession? = null

    override suspend fun connect(url: String): Flow<String> = flow {
        try {
            session = client.webSocketSession(url)
            while (session?.isActive == true) {
                val frame = session?.incoming?.receive()
                if (frame is Frame.Text) {
                    emit(frame.readText())
                }
            }
        } catch (e: Exception) {
            println("WebSocket connection error: ${e.message}")
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
