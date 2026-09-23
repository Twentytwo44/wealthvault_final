package com.wealthvault.data.portfolio.repository

import com.wealthvault.core.architecture.AppResult
import com.wealthvault.core.cache.FeatureCache

internal const val PORTFOLIO_CACHE_NAMESPACE = "portfolio"
internal const val REFERENCE_LIST_CACHE_NAMESPACE = "reference-lists"

/**
 * Successful portfolio mutations invalidate list/detail snapshots before the
 * next read. Cache failures are intentionally isolated from the mutation
 * result; a later request can repopulate the namespace.
 */
internal suspend fun <T> AppResult<T>.invalidatePortfolioCache(
    cache: FeatureCache?,
): AppResult<T> {
    if (this is AppResult.Success) {
        clearNamespacesBestEffort(
            cache,
            linkedSetOf(PORTFOLIO_CACHE_NAMESPACE, REFERENCE_LIST_CACHE_NAMESPACE),
        )
    }
    return this
}

private suspend fun clearNamespacesBestEffort(cache: FeatureCache?, namespaces: Set<String>) {
    try {
        cache?.clearNamespaces(namespaces)
    } catch (error: Throwable) {
        if (error is kotlinx.coroutines.CancellationException) throw error
        // Cache invalidation is best effort. The mutation result remains the
        // source of truth and a later refresh can repopulate the snapshots.
    }
}
