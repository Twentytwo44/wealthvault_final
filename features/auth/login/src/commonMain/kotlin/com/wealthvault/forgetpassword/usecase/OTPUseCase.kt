package com.wealthvault.forgetpassword.usecase

import com.wealthvault.`auth-api`.model.OTPRequest
import com.wealthvault.core.FlowResult
import com.wealthvault.core.FlowUseCase
import com.wealthvault.forgetpassword.data.OTPRepositoryImpl
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow


class OTPUseCase(
    private val otpRepository: OTPRepositoryImpl,
    // 1. รับ dispatcher เพิ่มเข้ามา
    dispatcher: CoroutineDispatcher,
): FlowUseCase<OTPRequest, Boolean>(dispatcher) { // 2. ส่งต่อให้คลาสแม่

    override fun execute(parameters: OTPRequest): Flow<FlowResult<Boolean>> = flow {
        println("🚀 [OTPUseCase] Confirm OTP to : ${parameters.email}")

        val result = otpRepository.otp(parameters)

        result.onSuccess {
            println("✅ [OTPseCase] Confirm OTP Success")

            emit(FlowResult.Continue(true))
        }.onFailure { exception ->
            println("❌[OTPUseCase] Confirm OTP Failed: ${exception.message}")
            emit(FlowResult.Failure(exception))
        }
    }.catch { cause ->
        println("🚨[OTPUseCase] Unexpected Error: ${cause.message}")
        emit(FlowResult.Failure(cause))
    }
}
