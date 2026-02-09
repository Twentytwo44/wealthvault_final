package com.wealthvault.wealthvault_final

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform
