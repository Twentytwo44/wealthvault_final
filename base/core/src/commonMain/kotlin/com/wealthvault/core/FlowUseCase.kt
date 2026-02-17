package com.wealthvault.core

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.flow.onStart

//abstract class FlowUseCase<IN, OUT> {
//
//    protected abstract fun execute(parameters: IN): Flow<com.wealthvault.core.FlowResult<OUT>>
//
//    fun invoke(
//        parameters: IN,
//        coroutineDispatcher: CoroutineDispatcher,
//    ): Flow<com.wealthvault.core.FlowResult<OUT>> {
//        return execute(parameters)
//            .onStart { emit(_root_ide_package_.com.wealthvault.core.FlowResult.Start) }
//            .onCompletion { emit(_root_ide_package_.com.wealthvault.core.FlowResult.Ended(it)) }
//            .flowOn(coroutineDispatcher)
//    }
//}
abstract class FlowUseCase<IN, OUT>(
    private val coroutineDispatcher: CoroutineDispatcher
) {

    protected abstract fun execute(parameters: IN): Flow<FlowResult<OUT>>

    // ✅ ใส่คำว่า operator เพื่อให้เรียก loginUseCase(parameters) ได้
    // ✅ นำ coroutineDispatcher ออกจาก parameter เพราะเราใช้จาก Constructor แล้ว
    operator fun invoke(
        parameters: IN
    ): Flow<FlowResult<OUT>> {
        return execute(parameters)
            .onStart { emit(FlowResult.Start) }
            .onCompletion { emit(FlowResult.Ended(it)) }
            .flowOn(coroutineDispatcher)
    }
}
