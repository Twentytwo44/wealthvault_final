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
    else -> {
        val description = message.orEmpty()
        when {
            description.contains("401") -> AppError.Unauthorized
            description.contains("404") -> AppError.NotFound
            description.contains("timeout", ignoreCase = true) ||
                description.contains("connect", ignoreCase = true) ||
                description.contains("network", ignoreCase = true) ||
                this::class.simpleName.orEmpty().contains("Timeout", ignoreCase = true) ||
                this::class.simpleName.orEmpty().contains("Connect", ignoreCase = true) ||
                this::class.simpleName.orEmpty().contains("Network", ignoreCase = true) -> AppError.Network(this)
            else -> AppError.Unknown(this)
        }
    }
}

/** Converts a typed application error to a user-facing exception when needed. */
fun AppError.toThrowable(): Throwable = when (this) {
    is AppError.Network -> cause
    is AppError.Unknown -> cause
    AppError.Unauthorized -> IllegalStateException("Unauthorized")
    AppError.NotFound -> NoSuchElementException("Not found")
}
