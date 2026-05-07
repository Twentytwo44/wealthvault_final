package com.wealthvault.config
object Config {
    const val localhost_android = "http://10.0.2.2:8080/api/"
    const val localhost_ios = "http://localhost:8080/api/"

    const val ws_android = "ws://10.0.2.2:8080/api/"
    const val ws_ios = "ws://localhost:8080/api/"

    val webSocketUrl: String
        get() = if (getPlatformName().contains("Android", ignoreCase = true)) ws_android else ws_ios
}

// Simple platform check helper if not already available in this scope
expect fun getPlatformName(): String
