package com.example.core

sealed class FlowResult<out T> {
    data object Start: FlowResult<Nothing>()
    data class Continue<out T>(val data: T): FlowResult<T>()
    data class Failure(val cause: Throwable?): FlowResult<Nothing>()
    data class Ended(val cause: Throwable?): FlowResult<Nothing>()
}