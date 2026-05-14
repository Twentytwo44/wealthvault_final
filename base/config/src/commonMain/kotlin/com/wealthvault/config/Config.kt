package com.wealthvault.config
object Config {
    const val localhost_android = "https://wealth.narutchai.com/api/"
    // const val localhost_android = "http://localhost:8080/api/"
    const val localhost_ios = "https://wealth.narutchai.com/api/"

    const val ws_android = "ws://wealth.narutchai.com/api/"
    const val ws_ios = "ws://wealth.narutchai.com/api/"

    val webSocketUrl: String
        get() = if (getPlatformName().contains("Android", ignoreCase = true)) ws_android else ws_ios
}

// Simple platform check helper if not already available in this scope
expect fun getPlatformName(): String