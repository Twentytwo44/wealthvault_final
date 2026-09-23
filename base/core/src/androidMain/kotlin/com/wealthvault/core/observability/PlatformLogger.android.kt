package com.wealthvault.core.observability

import android.util.Log

private const val TAG = "WealthVault"

actual fun platformLogger(): AppLogger = object : AppLogger {
    override fun debug(message: String) {
        Log.d(TAG, redactLogMessage(message))
    }
    override fun info(message: String) {
        Log.i(TAG, redactLogMessage(message))
    }
    override fun warn(message: String, cause: Throwable?) {
        Log.w(TAG, format(message, cause))
    }
    override fun error(message: String, cause: Throwable?) {
        Log.e(TAG, format(message, cause))
    }

    // Passing the original Throwable to Logcat would print its unredacted
    // message and could leak an Authorization header or token. Keep the
    // useful failure text while dropping the unsafe throwable chain.
    private fun format(message: String, cause: Throwable?): String = buildString {
        append(redactLogMessage(message))
        cause?.message?.takeIf { it.isNotBlank() }?.let {
            append(": ")
            append(redactLogMessage(it))
        }
    }
}
