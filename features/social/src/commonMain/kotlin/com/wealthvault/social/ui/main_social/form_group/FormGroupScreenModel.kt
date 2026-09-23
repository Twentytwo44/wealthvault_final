package com.wealthvault.social.ui.main_social.form_group

import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import com.wealthvault.domain.social.SocialRepository
import com.wealthvault.core.observability.AppLogger
import com.wealthvault.core.observability.platformLogger
import com.wealthvault.domain.profile.FriendData
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

data class FormGroupUiData(
    val isSuccess: Boolean = false,
    val friends: List<FriendData> = emptyList(),
    val errorMessage: String? = null,
)

sealed interface FormGroupUiAction : UiAction {
    data object FetchFriends : FormGroupUiAction
    data class Create(val name: String, val memberIds: List<String>, val imageBytes: ByteArray?) : FormGroupUiAction
    data class Update(
        val groupId: String,
        val name: String,
        val imageBytes: ByteArray?,
        val initialMemberIds: List<String>,
        val currentMemberIds: List<String>,
    ) : FormGroupUiAction
    data class Delete(val groupId: String) : FormGroupUiAction
}

sealed interface FormGroupUiEffect : UiEffect {
    data object Saved : FormGroupUiEffect
    data class ShowError(val error: AppError) : FormGroupUiEffect
}

class FormGroupScreenModel(
    private val repository: SocialRepository,
    private val logger: AppLogger = platformLogger(),
) : ScreenModel {

    // ... State isLoading และ isSuccess (ใช้ของเดิมที่มีอยู่แล้ว)
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _isSuccess = MutableStateFlow(false)
    val isSuccess: StateFlow<Boolean> = _isSuccess.asStateFlow()

    // 🌟 1. เพิ่ม State เก็บรายชื่อเพื่อน
    private val _friends = MutableStateFlow<List<FriendData>>(emptyList())
    val friends: StateFlow<List<FriendData>> = _friends.asStateFlow()

    private val _friendsLoading = MutableStateFlow(false)
    val friendsLoading: StateFlow<Boolean> = _friendsLoading.asStateFlow()

    private val _friendsError = MutableStateFlow<AppError?>(null)
    val friendsError: StateFlow<AppError?> = _friendsError.asStateFlow()

    private var fetchJob: Job? = null
    private var mutationJob: Job? = null

    // 🌟 2. เพิ่มฟังก์ชันดึงเพื่อน
    fun fetchFriends() {
        if (fetchJob?.isActive == true) return
        _friendsLoading.value = true
        _friendsError.value = null
        _errorMessage.value = null
        val job = screenModelScope.launch {
            syncUiState(isLoading = _isLoading.value, error = null)
            try {
                repository.getAllFriends().onSuccess { data ->
                    _friends.value = data
                    _friendsError.value = null
                    syncUiState(isLoading = _isLoading.value, error = null)
                }.onFailure { error ->
                    val appError = error.toAppError()
                    _friendsError.value = appError
                    _errorMessage.value = error.message
                    syncUiState(isLoading = _isLoading.value, error = appError)
                    _effects.tryEmit(FormGroupUiEffect.ShowError(appError))
                }
            } catch (error: CancellationException) {
                throw error
            } catch (error: Throwable) {
                val appError = error.toAppError()
                _friendsError.value = appError
                _errorMessage.value = error.message
                syncUiState(isLoading = _isLoading.value, error = appError)
                _effects.tryEmit(FormGroupUiEffect.ShowError(appError))
            } finally {
                _friendsLoading.value = false
            }
        }
        fetchJob = job
        job.invokeOnCompletion {
            if (fetchJob === job) fetchJob = null
        }
    }

    // ฟังก์ชัน createGroup ของเดิม...
    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    private val _uiState = MutableStateFlow(UiState(data = FormGroupUiData()))
    val uiState: StateFlow<UiState<FormGroupUiData>> = _uiState.asStateFlow()
    private val _effects = MutableSharedFlow<FormGroupUiEffect>(extraBufferCapacity = 1)
    val effects = _effects.asSharedFlow()

    fun onAction(action: FormGroupUiAction) {
        when (action) {
            FormGroupUiAction.FetchFriends -> fetchFriends()
            is FormGroupUiAction.Create -> createGroup(action.name, action.memberIds, action.imageBytes)
            is FormGroupUiAction.Update -> updateExistingGroup(
                action.groupId,
                action.name,
                action.imageBytes,
                action.initialMemberIds,
                action.currentMemberIds,
            )
            is FormGroupUiAction.Delete -> deleteGroup(action.groupId)
        }
    }

    private fun syncUiState(
        isLoading: Boolean = _isLoading.value,
        error: AppError? = _uiState.value.error,
    ) {
        _uiState.value = UiState(
            data = FormGroupUiData(
                isSuccess = _isSuccess.value,
                friends = _friends.value,
                errorMessage = _errorMessage.value,
            ),
            isLoading = isLoading,
            error = error,
        )
    }

    fun resetSuccessState() {
        _isSuccess.value = false
    }

    // 🌟 ฟังก์ชันสำหรับเคลียร์ข้อความ Error
    // (ใช้ป้องกันไม่ให้ Snackbar เด้งโชว์ข้อความเดิมซ้ำๆ เวลาหน้าจอมีการ Recompose)
    fun clearErrorMessage() {
        _errorMessage.value = null
    }

    fun createGroup(groupName: String, memberIds: List<String>, imageBytes: ByteArray?) {
        if (_isLoading.value || mutationJob?.isActive == true) return
        _isLoading.value = true
        val job = screenModelScope.launch {
            _errorMessage.value = null // เคลียร์ Error เก่าทิ้งก่อนยิงใหม่
            syncUiState(isLoading = true, error = null)

            try {
                repository.createGroup(groupName, memberIds, imageBytes).onSuccess {
                    logger.info("Create group succeeded")
                    _isSuccess.value = true
                    syncUiState(isLoading = false, error = null)
                    _effects.tryEmit(FormGroupUiEffect.Saved)
                }.onFailure { error ->
                    logger.warn("Create group failed", error)
                    // 🌟 2. ดันข้อความ Error ไปให้ UI โชว์
                    _errorMessage.value = "สร้างกลุ่มไม่สำเร็จ: ${error.message}"
                    val appError = error.toAppError()
                    syncUiState(isLoading = false, error = appError)
                    _effects.tryEmit(FormGroupUiEffect.ShowError(appError))
                }
            } catch (error: CancellationException) {
                throw error
            } catch (error: Throwable) {
                logger.warn("Create group failed unexpectedly", error)
                _errorMessage.value = error.message ?: "สร้างกลุ่มไม่สำเร็จ"
                val appError = error.toAppError()
                syncUiState(isLoading = false, error = appError)
                _effects.tryEmit(FormGroupUiEffect.ShowError(appError))
            } finally {
                _isLoading.value = false
            }
        }
        mutationJob = job
        job.invokeOnCompletion { if (mutationJob === job) mutationJob = null }
    }

    fun updateExistingGroup(
        groupId: String,
        newName: String,
        imageBytes: ByteArray?,
        initialMemberIds: List<String>,
        currentMemberIds: List<String>
    ) {
        if (_isLoading.value || mutationJob?.isActive == true) return
        _isLoading.value = true
        val job = screenModelScope.launch {
            _errorMessage.value = null
            syncUiState(isLoading = true, error = null)

            try {
                // 🌟 1. อัปเดตข้อมูลพื้นฐานกลุ่ม (ชื่อและรูป)
                // เรายิง APIPATCH ไปที่เส้นอัปเดตกลุ่มที่คุณ Champ เตรียมไว้
                repository.updateGroup(groupId, newName, imageBytes).getOrThrow()

                // 🌟 2. ลอจิกจัดการสมาชิก (Diffing)
                // หาคนที่จะ "เพิ่ม" (มีในลิสต์ปัจจุบัน แต่ไม่มีในลิสต์ตั้งต้น)
                val toAdd = currentMemberIds.filterNot { initialMemberIds.contains(it) }
                toAdd.forEach { targetId ->
                    repository.addGroupMember(groupId, targetId).getOrThrow()
                }

                // หาคนที่จะ "ลบ" (มีในลิสต์ตั้งต้น แต่ไม่มีในลิสต์ปัจจุบัน)
                val toRemove = initialMemberIds.filterNot { currentMemberIds.contains(it) }
                toRemove.forEach { targetId ->
                    repository.removeGroupMember(groupId, targetId).getOrThrow()
                }

                // ถ้าทำงานครบทุกเส้นแล้ว ให้ถือว่าสำเร็จ
                _isSuccess.value = true
                syncUiState(isLoading = false, error = null)
                _effects.tryEmit(FormGroupUiEffect.Saved)

            } catch (e: CancellationException) {
                throw e
            } catch (e: Throwable) {
                // ถ้ามีเส้นใดเส้นหนึ่งพัง ให้พ่น Error ออกมาโชว์ที่ Snackbar
                _errorMessage.value = e.message ?: "เกิดข้อผิดพลาดในการอัปเดตกลุ่ม"
                logger.warn("Update group failed", e)
                val appError = e.toAppError()
                syncUiState(isLoading = false, error = appError)
                _effects.tryEmit(FormGroupUiEffect.ShowError(appError))
            } finally {
                _isLoading.value = false
            }
        }
        mutationJob = job
        job.invokeOnCompletion { if (mutationJob === job) mutationJob = null }
    }
    fun deleteGroup(groupId: String) {
        if (_isLoading.value || mutationJob?.isActive == true) return
        _isLoading.value = true
        val job = screenModelScope.launch {
            _errorMessage.value = null
            syncUiState(isLoading = true, error = null)

            try {
                // 💡 อย่าลืมไปเขียนฟังก์ชัน deleteGroup ใน Repository ให้เรียก API ที่เพิ่งสร้างด้วยนะครับ
                repository.deleteGroup(groupId).getOrThrow()

                logger.info("Delete group succeeded")
                _isSuccess.value = true // พอเปลี่ยนเป็น true, LaunchedEffect ใน UI จะสั่ง navigator.pop() อัตโนมัติ
                syncUiState(isLoading = false, error = null)
                _effects.tryEmit(FormGroupUiEffect.Saved)

            } catch (e: CancellationException) {
                throw e
            } catch (e: Throwable) {
                logger.warn("Delete group failed", e)
                _errorMessage.value = e.message ?: "ลบกลุ่มไม่สำเร็จ กรุณาลองใหม่อีกครั้ง"
                val appError = e.toAppError()
                syncUiState(isLoading = false, error = appError)
                _effects.tryEmit(FormGroupUiEffect.ShowError(appError))
            } finally {
                _isLoading.value = false
            }
        }
        mutationJob = job
        job.invokeOnCompletion { if (mutationJob === job) mutationJob = null }
    }
}
