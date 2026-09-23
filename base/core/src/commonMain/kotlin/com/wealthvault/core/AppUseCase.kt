package com.wealthvault.core

import com.wealthvault.core.architecture.AppResult
import com.wealthvault.core.architecture.toAppError
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext

/**
 * Standard suspend use-case boundary.
 *
 * Use cases return the same [AppResult] contract as repositories.  Keeping
 * dispatcher ownership here prevents ad-hoc scopes and keeps presentation on
 * the same result contract as the data layer.
 */
abstract class AppUseCase<in P, out T>(
    private val dispatcher: CoroutineDispatcher,
) {
    suspend operator fun invoke(parameters: P): AppResult<T> = withContext(dispatcher) {
        try {
            execute(parameters)
        } catch (error: Throwable) {
            AppResult.Failure(error.toAppError())
        }
    }

    protected abstract suspend fun execute(parameters: P): AppResult<T>
}
