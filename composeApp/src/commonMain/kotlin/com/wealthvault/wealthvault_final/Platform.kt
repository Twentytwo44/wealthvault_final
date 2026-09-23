package com.wealthvault.app

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform
