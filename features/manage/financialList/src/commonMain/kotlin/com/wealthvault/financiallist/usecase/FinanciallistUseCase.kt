package com.wealthvault.financiallistusecase



//class DashboardUseCase(
//    private val registerRepository: RegisterRepositoryImpl,
//    // 1. รับ dispatcher เพิ่มเข้ามา
//    dispatcher: CoroutineDispatcher,
//): FlowUseCase<RegisterRequest, Boolean>(dispatcher) { // 2. ส่งต่อให้คลาสแม่
//
//    override fun execute(parameters: RegisterRequest): Flow<FlowResult<Boolean>> = flow {
//        println("🚀 [RegisterUseCase] Starting Register Action for: ${parameters.email}")
//
//        val result = registerRepository.register(parameters)
//
//        result.onSuccess {
//            println("✅ [RegisterUseCase] Register Success")
//
//            emit(FlowResult.Continue(true))
//        }.onFailure { exception ->
//            println("❌ [RegisterUseCase] Register Failed: ${exception.message}")
//            emit(FlowResult.Failure(exception))
//        }
//    }.catch { cause ->
//        println("🚨 [RegisterUseCase] Unexpected Error: ${cause.message}")
//        emit(FlowResult.Failure(cause))
//    }
//}
