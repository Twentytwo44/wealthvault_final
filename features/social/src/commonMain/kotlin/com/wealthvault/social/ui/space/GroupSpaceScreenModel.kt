package com.wealthvault.social.ui.space

// --- CRITICAL IMPORTS ADDED BELOW ---
import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import com.wealthvault.core.observability.AppLogger
import com.wealthvault.core.observability.platformLogger
import com.wealthvault.domain.auth.SessionManager
import com.wealthvault.domain.social.GroupChatAction
import com.wealthvault.domain.social.GroupChatGateway
import com.wealthvault.domain.social.GroupChatEvent
import com.wealthvault.domain.social.GroupMessage
import com.wealthvault.domain.social.SocialRepository
import com.wealthvault.core.architecture.AppError
import com.wealthvault.core.architecture.UiAction
import com.wealthvault.core.architecture.UiEffect
import com.wealthvault.core.architecture.UiState
import com.wealthvault.core.architecture.toAppError
import kotlinx.coroutines.delay
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.Job
import kotlinx.coroutines.NonCancellable
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.coroutines.withTimeoutOrNull

data class GroupSpaceUiData(val messages: List<GroupMessage> = emptyList())

sealed interface GroupSpaceUiAction : UiAction {
    data class Refresh(val groupId: String) : GroupSpaceUiAction
}

sealed interface GroupSpaceUiEffect : UiEffect {
    data class ShowError(val error: AppError) : GroupSpaceUiEffect
}

// ------------------------------------

class GroupSpaceScreenModel(
    private val repository: SocialRepository,
    private val groupChatGateway: GroupChatGateway,
    private val sessionManager: SessionManager,
    private val logger: AppLogger = platformLogger(),
) : ScreenModel {

    private var currentGroupId: String? = null
    private var fetchJob: Job? = null
    private var chatJob: Job? = null
    private var joinJob: Job? = null
    private var grantJob: Job? = null

    private val _messages = MutableStateFlow<List<GroupMessage>>(emptyList())
    val messages: StateFlow<List<GroupMessage>> = _messages.asStateFlow()

    private val _isLoading = MutableStateFlow(true)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _uiState = MutableStateFlow(UiState(data = GroupSpaceUiData(), isLoading = true))
    val uiState: StateFlow<UiState<GroupSpaceUiData>> = _uiState.asStateFlow()
    private val _effects = MutableSharedFlow<GroupSpaceUiEffect>(extraBufferCapacity = 1)
    val effects = _effects.asSharedFlow()

    fun onAction(action: GroupSpaceUiAction) {
        when (action) {
            is GroupSpaceUiAction.Refresh -> fetchMessages(action.groupId)
        }
    }

    val accessToken: StateFlow<String?> = sessionManager.accessToken
        .stateIn(
            scope = screenModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = null
        )

    fun fetchMessages(groupId: String) {
        if (fetchJob?.isActive == true && currentGroupId == groupId) return
        fetchJob?.cancel()
        currentGroupId = groupId
        val job = screenModelScope.launch {
            _isLoading.value = true
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)
            try {
                repository.getGroupMessages(groupId)
                    .onSuccess { data ->
                        _messages.value = data
                        _uiState.value = UiState(data = GroupSpaceUiData(data))
                    }
                    .onFailure { error ->
                        _messages.value = emptyList()
                        val appError = error.toAppError()
                        _uiState.value = UiState(data = GroupSpaceUiData(), error = appError)
                        _effects.tryEmit(GroupSpaceUiEffect.ShowError(appError))
                    }
            } catch (error: CancellationException) {
                throw error
            } catch (error: Throwable) {
                val appError = error.toAppError()
                _messages.value = emptyList()
                _uiState.value = UiState(data = GroupSpaceUiData(), error = appError)
                _effects.tryEmit(GroupSpaceUiEffect.ShowError(appError))
            } finally {
                _isLoading.value = false
            }
        }
        fetchJob = job
        job.invokeOnCompletion {
            if (fetchJob === job) fetchJob = null
        }
    }

    fun connectToChat(groupId: String, token: String) {
        if (chatJob?.isActive == true && currentGroupId == groupId) return
        chatJob?.cancel()
        joinJob?.cancel()
        currentGroupId = groupId
        val connectionJob = screenModelScope.launch {
            try {
                // Keep endpoint configuration and credentials inside the
                // data gateway; presentation only requests an authenticated
                // chat connection.
                groupChatGateway.connectToChat(token).collect { event ->
                    try {
                        logger.info("WebSocket connection established")
                        when (event) {
                            is GroupChatEvent.DataUpdated -> {
                                if (event.groupId == groupId) {
                                    // ✅ ใช้การ Fetch แบบ "เงียบๆ" ไม่เปิด Loading
                                    silentFetchMessages(groupId)
                                }
                            }
                            is GroupChatEvent.Message -> {
                                val newMessage = event.value
                                // ป้องกันการแอดข้อความซ้ำ
                                if (_messages.value.none { it.createdAt == newMessage.createdAt }) {
                                    // เนื่องจากใช้ reverseLayout = true ข้อความใหม่ต้องอยู่ index 0
                                    _messages.value = listOf(newMessage) + _messages.value
                                }
                            }
                        }
                    } catch (e: CancellationException) {
                        throw e
                    } catch (e: Throwable) {
                        logger.warn("Group chat message decode failed", e)
                        // Consider if you really want to re-fetch on every decode error
                    }
                }
            } catch (e: CancellationException) {
                throw e
            } catch (e: Throwable) {
                if (e is CancellationException) throw e
                logger.warn("Group chat WebSocket failed", e)
            }
        }
        chatJob = connectionJob
        connectionJob.invokeOnCompletion {
            if (chatJob === connectionJob) chatJob = null
        }


        // Action: JOIN
        val join = screenModelScope.launch {
                delay(500)
            try {
                val joinAction = GroupChatAction(action = "JOIN", groupId = groupId)
                groupChatGateway.send(joinAction)
                logger.debug("WebSocket JOIN sent")
            } catch (e: CancellationException) {
                throw e
            } catch (e: Throwable) {
                if (e is CancellationException) throw e
                logger.warn("WebSocket JOIN failed", e)
            }
        }
        joinJob = join
        join.invokeOnCompletion {
            if (joinJob === join) joinJob = null
        }
    }

    private fun silentFetchMessages(groupId: String) {
        screenModelScope.launch {
            try {
                // ไม่ต้องสั่ง _isLoading.value = true
                repository.getGroupMessages(groupId).onSuccess { data ->
                    _messages.value = data // แทนที่ List ทั้งหมดเมื่อโหลดเสร็จ
                }.onFailure { error ->
                    logger.warn("Silent group message refresh failed", error)
                }
            } catch (error: CancellationException) {
                throw error
            } catch (error: Throwable) {
                logger.warn("Silent group message refresh failed unexpectedly", error)
            }
        }
    }

    override fun onDispose() {
        fetchJob?.cancel()
        chatJob?.cancel()
        joinJob?.cancel()
        grantJob?.cancel()
        // The screen scope is cancelled by Voyager immediately after this
        // callback. Keep only the short socket cleanup non-cancellable so a
        // stale connection cannot survive navigation, while the LEAVE frame
        // remains best-effort and bounded.
        screenModelScope.launch(NonCancellable) {
            try {
                withContext(NonCancellable) {
                        withTimeoutOrNull(1_000) {
                            currentGroupId?.let { groupId ->
                                val leaveAction = GroupChatAction(action = "LEAVE", groupId = groupId)
                            groupChatGateway.send(leaveAction)
                        }
                    }
                }
            } finally {
                groupChatGateway.close()
            }
        }
        super.onDispose()
    }

    fun grantAccess(groupId: String, targetId: String, itemIds: List<String>) {
        if (grantJob?.isActive == true) return
        _uiState.value = _uiState.value.copy(isLoading = true, error = null)
        val job = screenModelScope.launch {
            try {
                repository.grantAccess(groupId, targetId, itemIds).onSuccess {
                    fetchMessages(groupId)
                }.onFailure { error ->
                    val appError = error.toAppError()
                    _uiState.value = _uiState.value.copy(error = appError)
                    _effects.tryEmit(GroupSpaceUiEffect.ShowError(appError))
                }
            } catch (error: CancellationException) {
                throw error
            } catch (error: Throwable) {
                val appError = error.toAppError()
                _uiState.value = _uiState.value.copy(error = appError)
                _effects.tryEmit(GroupSpaceUiEffect.ShowError(appError))
            } finally {
                _uiState.value = _uiState.value.copy(isLoading = false)
            }
        }
        grantJob = job
        job.invokeOnCompletion { if (grantJob === job) grantJob = null }
    }
}
