package com.wealthvault.core.observability

/** Platform trace hooks used by Macrobenchmark/Instruments without leaking
 * platform APIs into domain or data contracts. */
internal expect fun beginPlatformTrace(name: String)
internal expect fun endPlatformTrace()

interface PerformanceTracer {
    suspend fun <T> trace(name: String, block: suspend () -> T): T
}

/**
 * Lightweight production tracer. It records duration through the logging
 * boundary so platform implementations can route the event to Logcat, NSLog,
 * or an external sink without coupling domain code to a metrics SDK.
 */
class LoggingPerformanceTracer(
    private val logger: AppLogger,
) : PerformanceTracer {
    override suspend fun <T> trace(name: String, block: suspend () -> T): T {
        val startedAt = kotlin.time.TimeSource.Monotonic.markNow()
        beginPlatformTrace(name)
        return try {
            block()
        } finally {
            endPlatformTrace()
            logger.debug("performance_trace name=$name duration_ms=${startedAt.elapsedNow().inWholeMilliseconds}")
        }
    }
}

object NoOpPerformanceTracer : PerformanceTracer {
    override suspend fun <T> trace(name: String, block: suspend () -> T): T = block()
}
