package com.wealthvault.core.architecture

/** A small, platform-neutral error vocabulary for boundaries between layers. */
sealed interface AppError {
    data class Network(val cause: Throwable) : AppError
    data object Unauthorized : AppError
    data object NotFound : AppError
    data class Unknown(val cause: Throwable) : AppError
}

fun Throwable.toAppError(): AppError = when (this) {
    is kotlinx.coroutines.CancellationException -> throw this
    else -> AppError.Unknown(this)
}
