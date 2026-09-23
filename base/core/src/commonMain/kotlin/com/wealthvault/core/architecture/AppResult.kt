package com.wealthvault.core.architecture

sealed interface AppResult<out T> {
    data class Success<T>(val value: T) : AppResult<T>
    data class Failure(val error: AppError) : AppResult<Nothing>

    /**
     * Small compatibility surface for screens that are being migrated from
     * Kotlin Result.  Keeping these operations on the contract means a
     * feature can switch to AppResult without reintroducing a second result
     * type through presentation helpers.
     */
    val isSuccess: Boolean
        get() = this is Success<*>

    val isFailure: Boolean
        get() = this is Failure

    fun getOrNull(): T? = when (this) {
        is Success -> value
        is Failure -> null
    }

    fun exceptionOrNull(): Throwable? = when (this) {
        is Success -> null
        is Failure -> error.toThrowable()
    }

    fun getOrThrow(): T = when (this) {
        is Success -> value
        is Failure -> throw error.toThrowable()
    }

    fun onSuccess(action: (T) -> Unit): AppResult<T> {
        if (this is Success) action(value)
        return this
    }

    fun onFailure(action: (Throwable) -> Unit): AppResult<T> {
        if (this is Failure) action(error.toThrowable())
        return this
    }
}

/** Compatibility-friendly imports for incremental migrations from Kotlin Result. */
@Suppress("EXTENSION_SHADOWED_BY_MEMBER")
inline fun <T> AppResult<T>.onSuccess(action: (T) -> Unit): AppResult<T> {
    if (this is AppResult.Success) action(value)
    return this
}

@Suppress("EXTENSION_SHADOWED_BY_MEMBER")
inline fun <T> AppResult<T>.onFailure(action: (Throwable) -> Unit): AppResult<T> {
    if (this is AppResult.Failure) action(error.toThrowable())
    return this
}

@Suppress("EXTENSION_SHADOWED_BY_MEMBER")
fun <T> AppResult<T>.getOrNull(): T? = when (this) {
    is AppResult.Success -> value
    is AppResult.Failure -> null
}

inline fun <T, R> AppResult<T>.map(transform: (T) -> R): AppResult<R> = when (this) {
    is AppResult.Success -> AppResult.Success(transform(value))
    is AppResult.Failure -> this
}

inline fun <T> runAppCatching(block: () -> T): AppResult<T> = try {
    AppResult.Success(block())
} catch (error: Throwable) {
    AppResult.Failure(error.toAppError())
}

/** Suspending equivalent used by repositories at the data/domain boundary. */
suspend inline fun <T> runSuspendAppCatching(crossinline block: suspend () -> T): AppResult<T> = try {
    AppResult.Success(block())
} catch (error: Throwable) {
    // Cancellation is lifecycle control, not an application failure. Let the
    // caller's coroutine unwind so a disposed screen cannot render an error
    // or leave a loading state behind after its work has been cancelled.
    if (error is kotlinx.coroutines.CancellationException) throw error
    AppResult.Failure(error.toAppError())
}

fun <T> Result<T>.toAppResult(): AppResult<T> = fold(
    onSuccess = { AppResult.Success(it) },
    onFailure = { error -> AppResult.Failure(error.toAppError()) },
)
