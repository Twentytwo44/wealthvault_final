package com.wealthvault.social.ui.main_social.add_friend

import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import com.wealthvault.domain.social.SocialRepository
import com.wealthvault.core.observability.AppLogger
import com.wealthvault.core.observability.platformLogger
import com.wealthvault.domain.profile.FriendData
import com.wealthvault.domain.social.AcceptFriendRequest
import com.wealthvault.domain.social.PendingFriend
import com.wealthvault.core.architecture.AppError
import com.wealthvault.core.architecture.UiAction
import com.wealthvault.core.architecture.UiEffect
import com.wealthvault.core.architecture.UiState
import com.wealthvault.core.architecture.toAppError
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.Job
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.launch

data class AddFriendUiData(
    val isSearching: Boolean = false,
    val hasSearched: Boolean = false,
    val searchResult: FriendData? = null,
    val addFriendSuccess: Boolean = false,
    val pendingFriends: List<PendingFriend> = emptyList(),
    val popupMessage: String? = null,
)

sealed interface AddFriendUiAction : UiAction {
    data class Search(val email: String) : AddFriendUiAction
    data class Add(val targetId: String) : AddFriendUiAction
    data object FetchPending : AddFriendUiAction
    data class Respond(val requesterId: String, val accept: Boolean) : AddFriendUiAction
}

sealed interface AddFriendUiEffect : UiEffect {
    data object Added : AddFriendUiEffect
    data class ShowError(val error: AppError) : AddFriendUiEffect
}

class AddFriendScreenModel(
    private val repository: SocialRepository,
    private val logger: AppLogger = platformLogger(),
) : ScreenModel {

    // สถานะกำลังค้นหา (เอาไปใช้หมุนๆ ปุ่ม)
    private val _isSearching = MutableStateFlow(false)
    val isSearching: StateFlow<Boolean> = _isSearching.asStateFlow()

    // สถานะว่าได้เคยกดค้นหาไปแล้วหรือยัง (เอาไปโชว์ผลลัพธ์)
    private val _hasSearched = MutableStateFlow(false)
    val hasSearched: StateFlow<Boolean> = _hasSearched.asStateFlow()

    // ผลลัพธ์การค้นหา
    private val _searchResult = MutableStateFlow<FriendData?>(null)
    val searchResult: StateFlow<FriendData?> = _searchResult.asStateFlow()

    // สถานะการเพิ่มเพื่อนสำเร็จ (เอาไปเด้งกลับหน้าเดิม)
    private val _addFriendSuccess = MutableStateFlow(false)
    val addFriendSuccess: StateFlow<Boolean> = _addFriendSuccess.asStateFlow()

    private var searchJob: Job? = null
    private var addJob: Job? = null
    private var pendingJob: Job? = null
    private var respondJob: Job? = null

    // 🌟 ฟังก์ชันค้นหาผู้ใช้จาก Email

    fun resetAddFriendSuccess() {
        _addFriendSuccess.value = false
        syncUiState()
    }

    fun searchUser(email: String) {
        if (searchJob?.isActive == true) return
        _isSearching.value = true
        _hasSearched.value = true
        _searchResult.value = null // เคลียร์ผลลัพธ์เก่าทิ้งก่อน
        syncUiState(isLoading = true, error = null)
        val job = screenModelScope.launch {
            try {
                repository.searchUser(email).onSuccess { user ->
                    _searchResult.value = user
                    syncUiState(isLoading = false, error = null)
                }.onFailure { error ->
                    _searchResult.value = null // ถ้าไม่เจอหรือพัง ให้เป็น null
                    val appError = error.toAppError()
                    syncUiState(isLoading = false, error = appError)
                    _effects.tryEmit(AddFriendUiEffect.ShowError(appError))
                }
            } catch (error: CancellationException) {
                throw error
            } catch (error: Throwable) {
                val appError = error.toAppError()
                _searchResult.value = null
                syncUiState(isLoading = false, error = appError)
                _effects.tryEmit(AddFriendUiEffect.ShowError(appError))
            } finally {
                _isSearching.value = false
                syncUiState(isLoading = false)
            }
        }
        searchJob = job
        job.invokeOnCompletion {
            if (searchJob === job) searchJob = null
        }
    }

    // 🌟 ฟังก์ชันส่งคำขอเพิ่มเพื่อน
    fun addFriend(targetId: String) {
        if (addJob?.isActive == true) return
        syncUiState(isLoading = true, error = null)
        val job = screenModelScope.launch {
            try {
                repository.addFriend(targetId).onSuccess {
                    _addFriendSuccess.value = true
                    syncUiState(isLoading = false, error = null)
                    _effects.tryEmit(AddFriendUiEffect.Added)
                }.onFailure { error ->
                    logger.warn("Add friend failed", error)
                    val appError = error.toAppError()
                    syncUiState(isLoading = false, error = appError)
                    _effects.tryEmit(AddFriendUiEffect.ShowError(appError))
                }
            } catch (error: CancellationException) {
                throw error
            } catch (error: Throwable) {
                logger.warn("Add friend failed unexpectedly", error)
                val appError = error.toAppError()
                syncUiState(isLoading = false, error = appError)
                _effects.tryEmit(AddFriendUiEffect.ShowError(appError))
            } finally {
                syncUiState(isLoading = false)
            }
        }
        addJob = job
        job.invokeOnCompletion {
            if (addJob === job) addJob = null
        }
    }
    private val _pendingFriends = MutableStateFlow<List<PendingFriend>>(emptyList())
    val pendingFriends: StateFlow<List<PendingFriend>> = _pendingFriends.asStateFlow()

    // 🌟 ฟังก์ชันดึงคำขอเป็นเพื่อน (เอาไปเรียกตอนเปิดหน้าจอ)
    fun fetchPendingFriends() {
        if (pendingJob?.isActive == true) return
        syncUiState(isLoading = true, error = null)
        val job = screenModelScope.launch {
            try {
                repository.getPendingFriends(force = false).onSuccess { friends ->
                    _pendingFriends.value = friends
                    syncUiState(isLoading = false, error = null)
                }.onFailure { error ->
                    val appError = error.toAppError()
                    syncUiState(isLoading = false, error = appError)
                    _effects.tryEmit(AddFriendUiEffect.ShowError(appError))
                }
            } catch (error: CancellationException) {
                throw error
            } catch (error: Throwable) {
                val appError = error.toAppError()
                syncUiState(isLoading = false, error = appError)
                _effects.tryEmit(AddFriendUiEffect.ShowError(appError))
            } finally {
                syncUiState(isLoading = false)
            }
        }
        pendingJob = job
        job.invokeOnCompletion {
            if (pendingJob === job) pendingJob = null
        }
    }

    // 🌟 ฟังก์ชันจัดการคำขอเป็นเพื่อน (รับ/ปฏิเสธ)
    private val _popupMessage = MutableStateFlow<String?>(null)
    val popupMessage: StateFlow<String?> = _popupMessage.asStateFlow()

    private val _uiState = MutableStateFlow(UiState(data = AddFriendUiData()))
    val uiState: StateFlow<UiState<AddFriendUiData>> = _uiState.asStateFlow()
    private val _effects = MutableSharedFlow<AddFriendUiEffect>(extraBufferCapacity = 1)
    val effects = _effects.asSharedFlow()

    fun onAction(action: AddFriendUiAction) {
        when (action) {
            is AddFriendUiAction.Search -> searchUser(action.email)
            is AddFriendUiAction.Add -> addFriend(action.targetId)
            AddFriendUiAction.FetchPending -> fetchPendingFriends()
            is AddFriendUiAction.Respond -> respondToFriendRequest(action.requesterId, action.accept)
        }
    }

    private fun syncUiState(
        isLoading: Boolean = _uiState.value.isLoading,
        error: AppError? = _uiState.value.error,
    ) {
        _uiState.value = UiState(
            data = AddFriendUiData(
                isSearching = _isSearching.value,
                hasSearched = _hasSearched.value,
                searchResult = _searchResult.value,
                addFriendSuccess = _addFriendSuccess.value,
                pendingFriends = _pendingFriends.value,
                popupMessage = _popupMessage.value,
            ),
            isLoading = isLoading,
            error = error,
        )
    }

    // 🌟 2. ฟังก์ชันสำหรับปิด Popup
    fun clearPopupMessage() {
        _popupMessage.value = null
        syncUiState()
    }

    // 🌟 3. อัปเดตฟังก์ชันจัดการคำขอ ให้แจ้งเตือนเมื่อเสร็จสิ้น
    fun respondToFriendRequest(requesterId: String, isAccept: Boolean) {
        if (respondJob?.isActive == true) return
        syncUiState(isLoading = true, error = null)
        val job = screenModelScope.launch {
            val actionStr = if (isAccept) "ACCEPT" else "DECLINE"
            val request = AcceptFriendRequest(requesterId = requesterId, action = actionStr)

            try {
                repository.acceptFriend(request)
                    .onSuccess {
                        // ลบคนนั้นออกจากหน้าจอ
                        _pendingFriends.value = _pendingFriends.value.filterNot { it.id == requesterId }
                        // 🌟 ตั้งค่าข้อความ Popup
                        _popupMessage.value = if (isAccept) "เพิ่มเป็นเพื่อนสำเร็จแล้ว!" else "ลบคำขอเป็นเพื่อนเรียบร้อย"
                        syncUiState(isLoading = false, error = null)
                    }
                    .onFailure { error ->
                        // 🌟 แจ้งเตือนกรณี Error
                        _popupMessage.value = "เกิดข้อผิดพลาด: ${error.message}"
                        val appError = error.toAppError()
                        syncUiState(isLoading = false, error = appError)
                        _effects.tryEmit(AddFriendUiEffect.ShowError(appError))
                    }
            } catch (error: CancellationException) {
                throw error
            } catch (error: Throwable) {
                val appError = error.toAppError()
                _popupMessage.value = "เกิดข้อผิดพลาด: ${error.message}"
                syncUiState(isLoading = false, error = appError)
                _effects.tryEmit(AddFriendUiEffect.ShowError(appError))
            } finally {
                syncUiState(isLoading = false)
            }
        }
        respondJob = job
        job.invokeOnCompletion {
            if (respondJob === job) respondJob = null
        }
    }

}
