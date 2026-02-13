package com.wealthvault.login.usecase

import com.wealthvault.core.FlowResult
import com.wealthvault.core.FlowUseCase
import com.wealthvault.login.data.AuthRepositoryImpl
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map

internal class LoginUseCase(
    private val authRepository: AuthRepositoryImpl
): FlowUseCase<Unit, Boolean>() {

    override fun execute(parameters: Unit): Flow<FlowResult<Boolean>> {
        return authRepository.observeAuthState()
            .map<Boolean, FlowResult<Boolean>> { isAuthenticated ->
                // ระบุชัดเจนว่านี่คือ FlowResult<Boolean>
                FlowResult.Continue(isAuthenticated)
            }
            .catch { cause ->
                // ตอนนี้ Failure จะถูกยอมรับในฐานะ FlowResult<Boolean> ครับ
                emit(FlowResult.Failure(cause))
            }
    }
}
