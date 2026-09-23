package com.wealthvault.core.architecture

import com.wealthvault.core.model.Attachment
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow

interface UiAction

interface UiEffect

/** Common actions used by form ScreenModels during the migration to UDF. */
sealed interface FormAction<out T> : UiAction {
    data class Changed<T>(val value: T) : FormAction<T>
    data class AttachmentsChanged(
        val added: List<Attachment>,
        val deleted: List<Attachment>,
    ) : FormAction<Nothing>
    data class Submit(val id: String) : FormAction<Nothing>
}

/** Common one-time effects emitted after a form mutation. */
sealed interface FormEffect : UiEffect {
    data object Saved : FormEffect
    data class Failed(val error: AppError) : FormEffect
}

/** Small state/effect holder that keeps form ScreenModels consistent. */
class UiStateHolder<T>(initial: T) {
    private var currentData: T = initial
    private val _state = MutableStateFlow(UiState(data = initial))
    val state: StateFlow<UiState<T>> = _state.asStateFlow()

    private val _effects = MutableSharedFlow<UiEffect>(extraBufferCapacity = 1)
    val effects: SharedFlow<UiEffect> = _effects.asSharedFlow()

    fun set(
        data: T = currentData,
        isLoading: Boolean = _state.value.isLoading,
        error: AppError? = _state.value.error,
    ) {
        currentData = data
        _state.value = UiState(data = data, isLoading = isLoading, error = error)
    }

    fun loading() {
        _state.value = _state.value.copy(isLoading = true, error = null)
    }

    fun success(data: T = currentData) {
        currentData = data
        _state.value = UiState(data = data, isLoading = false)
    }

    fun failure(error: AppError) {
        _state.value = _state.value.copy(isLoading = false, error = error)
        _effects.tryEmit(FormEffect.Failed(error))
    }

    fun emit(effect: UiEffect) {
        _effects.tryEmit(effect)
    }
}
