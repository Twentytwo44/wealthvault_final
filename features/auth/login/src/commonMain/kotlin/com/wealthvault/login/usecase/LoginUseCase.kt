package com.wealthvault.login.usecase

import com.wealthvault.`auth-api`.model.LoginRequest
import com.wealthvault.core.FlowResult
import com.wealthvault.core.FlowUseCase
import com.wealthvault.data_store.TokenStore
import com.wealthvault.login.data.AuthRepositoryImpl
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow

class LoginUseCase(
    private val authRepository: AuthRepositoryImpl,

    // 1. รับ dispatcher เพิ่มเข้ามา
    dispatcher: CoroutineDispatcher,
    private val tokenStore: TokenStore
): FlowUseCase<LoginRequest, Boolean>(dispatcher) { // 2. ส่งต่อให้คลาสแม่

    override fun execute(parameters: LoginRequest): Flow<FlowResult<Boolean>> = flow {
        println("🚀 [LoginUseCase] Starting Login Action for: ${parameters.email}")

        val result = authRepository.login(parameters)

        result.onSuccess {
            println("✅ [LoginUseCase] Login Success")
            println("TokenStore:, ${tokenStore.accessToken.first()}")
            println("RefreshStore:, ${tokenStore.refreshToken.first()}")


            emit(FlowResult.Continue(true))
        }.onFailure { exception ->
            println("❌ [LoginUseCase] Login Failed: ${exception.message}")
            emit(FlowResult.Failure(exception))
        }
    }.catch { cause ->
        println("🚨 [LoginUseCase] Unexpected Error: ${cause.message}")

        // 🌟 เปลี่ยนมาเช็คแค่ message ง่ายๆ พอครับ
        val errorMsg = cause.message?.lowercase() ?: ""
        if (errorMsg.contains("refused") || errorMsg.contains("failed to connect") || errorMsg.contains("timeout")) {

            println("============================================================")
            println("🕵️‍♂️ จับตาดู! Ktor พยายามยิง API ขัดข้อง!")
            // พิมพ์แค่ StackTrace พอ จะได้ไม่ติดเรื่อง io.ktor...
            cause.printStackTrace()
            println("============================================================")
        }

        emit(FlowResult.Failure(cause))
    }
}
