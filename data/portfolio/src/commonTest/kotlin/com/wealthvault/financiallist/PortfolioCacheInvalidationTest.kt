package com.wealthvault.financiallist

import com.wealthvault.core.architecture.AppError
import com.wealthvault.core.architecture.AppResult
import com.wealthvault.core.cache.FeatureCache
import com.wealthvault.core.cache.FeatureCacheEntry
import com.wealthvault.data.portfolio.repository.invalidatePortfolioCache
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals

class PortfolioCacheInvalidationTest {
    @Test
    fun successfulMutationClearsPortfolioAndReferenceNamespaces() = runTest {
        val cache = RecordingCache()

        AppResult.Success(Unit).invalidatePortfolioCache(cache)

        assertEquals(listOf("portfolio", "reference-lists"), cache.clearedNamespaces)
        assertEquals(
            listOf<Set<String>>(linkedSetOf("portfolio", "reference-lists")),
            cache.clearNamespaceBatches,
        )
    }

    @Test
    fun failedMutationKeepsExistingCache() = runTest {
        val cache = RecordingCache()

        AppResult.Failure(AppError.Network(IllegalStateException("offline")))
            .invalidatePortfolioCache(cache)

        assertEquals(emptyList<String>(), cache.clearedNamespaces)
    }

    @Test
    fun failedBatchDoesNotPartiallyClearNamespaces() = runTest {
        val cache = RecordingCache(failingNamespace = "portfolio")

        AppResult.Success(Unit).invalidatePortfolioCache(cache)

        assertEquals(emptyList<String>(), cache.clearedNamespaces)
        assertEquals(
            listOf<Set<String>>(linkedSetOf("portfolio", "reference-lists")),
            cache.clearNamespaceBatches,
        )
    }

    private class RecordingCache(
        private val failingNamespace: String? = null,
    ) : FeatureCache {
        val clearedNamespaces = mutableListOf<String>()
        val clearNamespaceBatches = mutableListOf<Set<String>>()

        override suspend fun read(namespace: String, key: String): FeatureCacheEntry? = null
        override suspend fun write(namespace: String, key: String, payload: String, updatedAtEpochMillis: Long) = Unit
        override suspend fun clear(namespace: String, key: String) = Unit
        override suspend fun clearNamespace(namespace: String) {
            if (namespace == failingNamespace) error("cache unavailable")
            clearedNamespaces += namespace
        }

        override suspend fun clearNamespaces(namespaces: Set<String>) {
            clearNamespaceBatches += namespaces
            if (failingNamespace in namespaces) error("cache unavailable")
            clearedNamespaces += namespaces
        }
    }
}
