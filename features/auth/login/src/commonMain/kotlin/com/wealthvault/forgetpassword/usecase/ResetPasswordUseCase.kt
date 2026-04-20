package com.wealthvault.forgetpassword.usecase

import com.wealthvault.`auth-api`.model.ResetPasswordRequest
import com.wealthvault.core.FlowResult
import com.wealthvault.core.FlowUseCase
import com.wealthvault.forgetpassword.data.reset.ResetRepositoryImpl
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow


class ResetPasswordUseCase(
    private val resetRepository: ResetRepositoryImpl,
    // 1. รับ dispatcher เพิ่มเข้ามา
    dispatcher: CoroutineDispatcher,
): FlowUseCase<ResetPasswordRequest, Boolean>(dispatcher) { // 2. ส่งต่อให้คลาสแม่

    override fun execute(parameters: ResetPasswordRequest): Flow<FlowResult<Boolean>> = flow {
        println("🚀 [ResetPasswordUseCase] Reset Password ")

        val result = resetRepository.reset(parameters)

        result.onSuccess {
            println("✅ [ResetPasswordUseCase] Reset Password succes")

            emit(FlowResult.Continue(true))
        }.onFailure { exception ->
            println("❌[[ResetPasswordUseCase] Reset Password Failed: ${exception.message}")
            emit(FlowResult.Failure(exception))
        }
    }.catch { cause ->
        println("🚨[ResetPasswordUseCase] Reset Password Unexpected Error: ${cause.message}")
        emit(FlowResult.Failure(cause))
    }
}
