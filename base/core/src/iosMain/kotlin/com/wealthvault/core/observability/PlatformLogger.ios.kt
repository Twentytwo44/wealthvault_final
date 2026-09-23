package com.wealthvault.core.observability

import platform.Foundation.NSLog

actual fun platformLogger(): AppLogger = object : AppLogger {
    override fun debug(message: String) = NSLog("[DEBUG] ${redactLogMessage(message)}")
    override fun info(message: String) = NSLog("[INFO] ${redactLogMessage(message)}")
    override fun warn(message: String, cause: Throwable?) {
        NSLog("[WARN] ${redactLogMessage(message)}${cause?.message?.let { ": ${redactLogMessage(it)}" } ?: ""}")
    }
    override fun error(message: String, cause: Throwable?) {
        NSLog("[ERROR] ${redactLogMessage(message)}${cause?.message?.let { ": ${redactLogMessage(it)}" } ?: ""}")
    }
}
