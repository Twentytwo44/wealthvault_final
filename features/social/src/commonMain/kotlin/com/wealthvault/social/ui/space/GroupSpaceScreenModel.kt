package com.wealthvault.social.ui.space

// --- CRITICAL IMPORTS ADDED BELOW ---
import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import com.wealthvault.config.Config
import com.wealthvault.data_store.TokenStore
import com.wealthvault.group_api.model.ChatAction
import com.wealthvault.group_api.model.GroupMsgData
import com.wealthvault.group_api.model.WsUpdateResponse
import com.wealthvault.social.data.SocialRepositoryImpl
import com.wealthvault.websocket_api.WebSocketService
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive

// ------------------------------------

class GroupSpaceScreenModel(
    private val repository: SocialRepositoryImpl,
    private val webSocketService: WebSocketService,
    private val json: Json,
    private val tokenStore: TokenStore
) : ScreenModel {

    private var currentGroupId: String? = null

    private val _messages = MutableStateFlow<List<GroupMsgData>>(emptyList())
    val messages: StateFlow<List<GroupMsgData>> = _messages.asStateFlow()

    private val _isLoading = MutableStateFlow(true)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    val accessToken: StateFlow<String?> = tokenStore.accessToken
        .stateIn(
            scope = screenModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = null
        )

    fun fetchMessages(groupId: String) {
        currentGroupId = groupId
        screenModelScope.launch {
            _isLoading.value = true
            repository.getGroupMessages(groupId)
                .onSuccess { data -> _messages.value = data }
                .onFailure { _messages.value = emptyList() }
            _isLoading.value = false
        }
    }

    fun connectToChat(groupId: String, token: String) {
        screenModelScope.launch {
            try {
                // Use the Config object correctly
                val url = "${Config.webSocketUrl}ws?token=$token"

                webSocketService.connect(url).collect { message ->
                    try {
                        println("✅ [WebSocket] Connection Established Successfully!")
                        println("🌐 Connected to: $url")
                        val jsonElement = json.parseToJsonElement(message)
                        val type = jsonElement.jsonObject["type"]?.jsonPrimitive?.content

                        if (type == "DATA_UPDATE") {
                            val update = json.decodeFromString<WsUpdateResponse>(message)
                            if (update.payload?.groupId == groupId) {
                                // ✅ ใช้การ Fetch แบบ "เงียบๆ" ไม่เปิด Loading
                                silentFetchMessages(groupId)
                            }
                            return@collect
                        }

                        val newMessage = json.decodeFromString<GroupMsgData>(message)

                        // ป้องกันการแอดข้อความซ้ำ
                        if (_messages.value.none { it.createdAt == newMessage.createdAt  }) {
                            // เนื่องจากใช้ reverseLayout = true ข้อความใหม่ต้องอยู่ index 0
                            _messages.value = listOf(newMessage) + _messages.value
                        }
                    } catch (e: Exception) {
                        println("🚨 [GroupChat] Failed to decode message: ${e.message}")
                        // Consider if you really want to re-fetch on every decode error
                    }
                }
            } catch (e: Exception) {
                println("🚨 [GroupChat] WebSocket Error: ${e.message}")
            }
        }


        // Action: JOIN
        screenModelScope.launch {
            delay(500)
            try {
                val joinAction = ChatAction(action = "JOIN", groupId = groupId)
                webSocketService.send(json.encodeToString(joinAction))
                println("📤 [WebSocket] Sent JOIN for group: $groupId")
            } catch (e: Exception) {
                println("🚨 [GroupChat] Failed to send JOIN: ${e.message}")
            }
        }
    }

    private fun silentFetchMessages(groupId: String) {
        screenModelScope.launch {
            // ไม่ต้องสั่ง _isLoading.value = true
            repository.getGroupMessages(groupId).onSuccess { data ->
                _messages.value = data // แทนที่ List ทั้งหมดเมื่อโหลดเสร็จ
            }
        }
    }

    override fun onDispose() {
        // Important: Close connection first
        screenModelScope.launch {
            try {
                currentGroupId?.let { groupId ->
                    val leaveAction = ChatAction(action = "LEAVE", groupId = groupId)
                    webSocketService.send(json.encodeToString(leaveAction))
                }
            } finally {
                webSocketService.close()
            }
        }
        super.onDispose()
    }

    fun grantAccess(groupId: String, targetId: String, itemIds: List<String>) {
        screenModelScope.launch {
            repository.grantAccess(groupId, targetId, itemIds).onSuccess {
                fetchMessages(groupId)
            }
        }
    }
}
