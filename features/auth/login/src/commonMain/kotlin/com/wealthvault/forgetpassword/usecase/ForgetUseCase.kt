package com.wealthvault.forgetpassword.usecase

import com.wealthvault.`auth-api`.model.ForgetPasswordRequest
import com.wealthvault.core.FlowResult
import com.wealthvault.core.FlowUseCase
import com.wealthvault.forgetpassword.data.forget.ForgetRepositoryImpl
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow


class ForgetUsecase(
    private val forgetRepository: ForgetRepositoryImpl,
    // 1. รับ dispatcher เพิ่มเข้ามา
    dispatcher: CoroutineDispatcher,
): FlowUseCase<ForgetPasswordRequest, Boolean>(dispatcher) { // 2. ส่งต่อให้คลาสแม่

    override fun execute(parameters: ForgetPasswordRequest): Flow<FlowResult<Boolean>> = flow {
        println("🚀 [ForgetUseCase] Send OTP to : ${parameters.email}")

        val result = forgetRepository.forgetpassword(parameters)

        result.onSuccess {
            println("✅ [ForgetUseCase] Send OTP Success")

            emit(FlowResult.Continue(true))
        }.onFailure { exception ->
            println("❌[ForgetUseCase] Send OTP Failed: ${exception.message}")
            emit(FlowResult.Failure(exception))
        }
    }.catch { cause ->
        println("🚨[ForgetUseCase] Unexpected Error: ${cause.message}")
        emit(FlowResult.Failure(cause))
    }
}
