package com.wealthvault.forgetpassword.usecase

import com.wealthvault.`auth-api`.model.OTPRequest
import com.wealthvault.core.FlowResult
import com.wealthvault.core.FlowUseCase
import com.wealthvault.forgetpassword.data.otp.OTPRepositoryImpl
// ลบ import io.ktor.http.parameters ทิ้งไปแล้ว!
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow

class OTPUseCase(
    private val otpRepository: OTPRepositoryImpl,
    dispatcher: CoroutineDispatcher,
): FlowUseCase<OTPRequest, String>(dispatcher) {

    override fun execute(parameters: OTPRequest): Flow<FlowResult<String>> = flow {
        println("🚀 [OTPUseCase] Confirm OTP to : ${parameters.email}")

        val result = otpRepository.otp(parameters)

        result.onSuccess { token -> // 🌟 ตอนนี้รับ Token ได้สำเร็จแล้ว!
            println("✅ [OTPUseCase] Confirm OTP Success. Token: $token")
            emit(FlowResult.Continue(token))

        }.onFailure { exception ->
            println("❌[OTPUseCase] Confirm OTP Failed: ${exception.message}")
            emit(FlowResult.Failure(exception))
        }

    }.catch { cause ->
        println("🚨[OTPUseCase] Unexpected Error: ${cause.message}")
        emit(FlowResult.Failure(cause))
    }
}