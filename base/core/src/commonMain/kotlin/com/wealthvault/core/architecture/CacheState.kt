package com.wealthvault.core.architecture

enum class CacheFreshness {
    Fresh,
    Stale,
    Offline,
}

data class CachedValue<out T>(
    val value: T,
    val freshness: CacheFreshness,
    val updatedAtEpochMillis: Long,
)
