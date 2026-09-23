package com.wealthvault.core.observability

/** Logging boundary. Implementations must redact credentials and personal data. */
interface AppLogger {
    fun debug(message: String)
    fun info(message: String)
    fun warn(message: String, cause: Throwable? = null)
    fun error(message: String, cause: Throwable? = null)
}

object NoOpAppLogger : AppLogger {
    override fun debug(message: String) = Unit
    override fun info(message: String) = Unit
    override fun warn(message: String, cause: Throwable?) = Unit
    override fun error(message: String, cause: Throwable?) = Unit
}
