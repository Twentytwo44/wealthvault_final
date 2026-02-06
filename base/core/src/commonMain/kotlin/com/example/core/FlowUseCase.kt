package com.example.core

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.flow.onStart

abstract class FlowUseCase<IN, OUT> {

    protected abstract fun execute(parameters: IN): Flow<FlowResult<OUT>>

    fun invoke(
        parameters: IN,
        coroutineDispatcher: CoroutineDispatcher,
    ): Flow<FlowResult<OUT>> {
        return execute(parameters)
            .onStart { emit(FlowResult.Start) }
            .onCompletion { emit(FlowResult.Ended(it)) }
            .flowOn(coroutineDispatcher)
    }
}