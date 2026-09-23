package com.wealthvault.config
object Config {
    /**
     * Compatibility name retained for the legacy API modules. It resolves to
     * the current platform endpoint so shared code no longer silently uses the
     * Android URL when running on iOS.
     */
    val localhost_android: String
        get() = if (getPlatformName().contains("Android", ignoreCase = true)) {
            PRODUCTION_API_BASE_URL
        } else {
            localhost_ios
        }

    const val localhost_ios = "https://wealth.narutchai.com/api/"

    // The API is served over TLS; websocket traffic must use the secure transport as well.
    const val ws_android = "wss://wealth.narutchai.com/api/"
    const val ws_ios = "wss://wealth.narutchai.com/api/"

    /** Platform-neutral API base URL used by shared networking code. */
    val apiBaseUrl: String
        get() = localhost_android

    val webSocketUrl: String
        get() = if (getPlatformName().contains("Android", ignoreCase = true)) ws_android else ws_ios

    /** Fails fast if a production configuration ever regresses to cleartext. */
    fun requireSecureTransport() {
        require(apiBaseUrl.startsWith("https://")) {
            "Production API endpoints must use HTTPS"
        }
        require(webSocketUrl.startsWith("wss://")) {
            "Production WebSocket endpoints must use WSS"
        }
    }

    private const val PRODUCTION_API_BASE_URL = "https://wealth.narutchai.com/api/"
}

// Simple platform check helper if not already available in this scope
expect fun getPlatformName(): String
