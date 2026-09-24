package com.wealthvault.social

import com.wealthvault.data.social.repository.websocket.WebSocketGroupChatGateway
import com.wealthvault.data.social.repository.websocket.WebSocketTransport
import com.wealthvault.domain.social.GroupChatAction
import com.wealthvault.domain.social.GroupChatEvent
import kotlinx.serialization.json.Json
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs

class WebSocketGroupChatGatewayTest {
    @Test
    fun forwardsChatLifecycleToTransportAdapter() = runTest {
        val service = FakeWebSocketService()
        val gateway = WebSocketGroupChatGateway(service, Json { ignoreUnknownKeys = true })

        val event = gateway.connect("wss://example.test/chat").first()
        assertIs<GroupChatEvent.Message>(event)
        assertEquals("hello", event.value.content)
        gateway.send(GroupChatAction("JOIN", "group-1"))
        gateway.close()

        assertEquals(listOf("wss://example.test/chat"), service.connectedUrls)
        assertEquals(listOf("{\"action\":\"JOIN\",\"group_id\":\"group-1\"}"), service.sentMessages)
        assertEquals(1, service.closeCalls)
    }

    @Test
    fun mapsDataUpdateToTypedDomainEvent() = runTest {
        val service = FakeWebSocketService(
            messages = listOf("{\"type\":\"DATA_UPDATE\",\"payload\":{\"group_id\":\"group-1\"}}"),
        )
        val gateway = WebSocketGroupChatGateway(service, Json { ignoreUnknownKeys = true })

        val event = gateway.connect("wss://example.test/chat").first()

        assertEquals(GroupChatEvent.DataUpdated("group-1"), event)
    }

    private class FakeWebSocketService(
        private val messages: List<String> = listOf(
            "{\"sender_id\":\"user-1\",\"msg_type\":\"text\",\"content\":\"hello\",\"created_at\":\"2026-01-01\"}",
        ),
    ) : WebSocketTransport {
        val connectedUrls = mutableListOf<String>()
        val sentMessages = mutableListOf<String>()
        var closeCalls = 0

        override suspend fun connect(url: String): Flow<String> {
            connectedUrls += url
            return flowOf(*messages.toTypedArray())
        }

        override suspend fun send(message: String) {
            sentMessages += message
        }

        override suspend fun close() {
            closeCalls += 1
        }
    }
}
