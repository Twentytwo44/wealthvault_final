package com.wealthvault.introduction.usecase

//
//class IntroUseCase(
//    private val authRepository: AuthRepositoryImpl,
//    // 1. รับ dispatcher เพิ่มเข้ามา
//    dispatcher: CoroutineDispatcher,
//    private val tokenStore: TokenStore
//): FlowUseCase<LoginRequest, Boolean>(dispatcher) { // 2. ส่งต่อให้คลาสแม่
//
//    override fun execute(parameters: LoginRequest): Flow<FlowResult<Boolean>> = flow {
//        println("🚀 [LoginUseCase] Starting Login Action for: ${parameters.email}")
//
//        val result = authRepository.login(parameters)
//
//        result.onSuccess {
//            println("✅ [LoginUseCase] Login Success")
//            println("TokenStore:, ${tokenStore.authToken.first()}")
//            emit(FlowResult.Continue(true))
//        }.onFailure { exception ->
//            println("❌ [LoginUseCase] Login Failed: ${exception.message}")
//            emit(FlowResult.Failure(exception))
//        }
//    }.catch { cause ->
//        println("🚨 [LoginUseCase] Unexpected Error: ${cause.message}")
//        emit(FlowResult.Failure(cause))
//    }
//}
