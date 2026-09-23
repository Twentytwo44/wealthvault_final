package com.wealthvault.core

import com.wealthvault.core.architecture.AppResult
import com.wealthvault.core.cache.FeatureCache
import com.wealthvault.core.cache.FeatureCacheEntry
import com.wealthvault.core.cache.ReferenceListCache
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.Serializable
import kotlinx.serialization.KSerializer
import kotlinx.serialization.SerializationException
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class ReferenceListCacheTest {
    @Test
    fun freshSnapshotAvoidsNetwork() = runBlocking {
        val cache = MemoryCache()
        val json = Json
        cache.write(
            namespace = "reference-lists",
            key = "land",
            payload = json.encodeToString(listOf(ReferenceValue("cached"))),
            updatedAtEpochMillis = kotlin.time.Clock.System.now().toEpochMilliseconds(),
        )
        val referenceCache = ReferenceListCache(cache, json, "reference-lists", "land")
        var networkCalls = 0

        val result = referenceCache.get(ReferenceValue.serializer()) {
            networkCalls += 1
            AppResult.Success(listOf(ReferenceValue("network")))
        }

        assertEquals(AppResult.Success(listOf(ReferenceValue("cached"))), result)
        assertEquals(0, networkCalls)
    }

    @Test
    fun concurrentExpiredReadsRefreshOnce() = runBlocking {
        val referenceCache = ReferenceListCache(null, Json, "reference-lists", "land")
        var networkCalls = 0

        val results = (1..20).map {
            async {
                referenceCache.get(ReferenceValue.serializer()) {
                    networkCalls += 1
                    delay(10)
                    AppResult.Success(listOf(ReferenceValue("network")))
                }
            }
        }.awaitAll()

        assertEquals(1, networkCalls)
        assertEquals(listOf(AppResult.Success(listOf(ReferenceValue("network")))), results.distinct())
    }

    @Test
    fun failedRefreshServesExpiredSnapshot() = runBlocking {
        val cache = MemoryCache()
        val json = Json
        cache.write(
            namespace = "reference-lists",
            key = "land",
            payload = json.encodeToString(listOf(ReferenceValue("stale"))),
            updatedAtEpochMillis = kotlin.time.Clock.System.now().toEpochMilliseconds() - 16 * 60 * 1_000L,
        )
        val referenceCache = ReferenceListCache(cache, json, "reference-lists", "land")

        val result = referenceCache.get(ReferenceValue.serializer()) {
            AppResult.Failure(
                com.wealthvault.core.architecture.AppError.Network(IllegalStateException("offline")),
            )
        }

        assertEquals(AppResult.Success(listOf(ReferenceValue("stale"))), result)
    }

    @Test
    fun forceRefreshBypassesFreshSnapshot() = runBlocking {
        val cache = MemoryCache()
        val json = Json
        cache.write(
            "reference-lists",
            "land",
            json.encodeToString(listOf(ReferenceValue("cached"))),
            kotlin.time.Clock.System.now().toEpochMilliseconds(),
        )
        val referenceCache = ReferenceListCache(cache, json, "reference-lists", "land")
        var networkCalls = 0

        val result = referenceCache.get(ReferenceValue.serializer(), force = true) {
            networkCalls += 1
            AppResult.Success(listOf(ReferenceValue("network")))
        }

        assertEquals(AppResult.Success(listOf(ReferenceValue("network"))), result)
        assertEquals(1, networkCalls)
    }

    @Test
    fun failedRefreshWithoutCacheReturnsFailure() = runBlocking {
        val referenceCache = ReferenceListCache(null, Json, "reference-lists", "land")
        val failure: AppResult<List<ReferenceValue>> = AppResult.Failure(
            com.wealthvault.core.architecture.AppError.Network(IllegalStateException("offline")),
        )

        val result = referenceCache.get(ReferenceValue.serializer()) { failure }

        assertEquals(failure, result)
    }

    @Test
    fun malformedSnapshotAndCacheWriteFailureDoNotHideNetworkValue() = runBlocking {
        val cache = MemoryCache(failWrites = true).apply {
            values["reference-lists:land"] = FeatureCacheEntry("not-json", 1L)
        }
        val referenceCache = ReferenceListCache(cache, Json, "reference-lists", "land")

        val result = referenceCache.get(ReferenceValue.serializer()) {
            AppResult.Success(listOf(ReferenceValue("network")))
        }

        assertEquals(AppResult.Success(listOf(ReferenceValue("network"))), result)
    }

    @Test
    fun missingJsonStillReturnsSuccessfulNetworkResult() = runBlocking {
        val referenceCache = ReferenceListCache(MemoryCache(), null, "reference-lists", "land")

        val result = referenceCache.get(ReferenceValue.serializer()) {
            AppResult.Success(listOf(ReferenceValue("network")))
        }

        assertEquals(AppResult.Success(listOf(ReferenceValue("network"))), result)
    }

    @Test
    fun missingJsonWithExistingSnapshotStillRefreshes() = runBlocking {
        val cache = MemoryCache().apply {
            values["reference-lists:land"] = FeatureCacheEntry("ignored", 1L)
        }
        val referenceCache = ReferenceListCache(cache, null, "reference-lists", "land")

        val result = referenceCache.get(ReferenceValue.serializer()) {
            AppResult.Success(listOf(ReferenceValue("network")))
        }

        assertEquals(AppResult.Success(listOf(ReferenceValue("network"))), result)
    }

    @Test
    fun readFailureFallsBackToNetworkAndFutureSnapshotCannotSuppressRefresh() = runBlocking {
        val failingCache = MemoryCache(failReads = true)
        val failingReferenceCache = ReferenceListCache(failingCache, Json, "reference-lists", "land")
        val networkResult = failingReferenceCache.get(ReferenceValue.serializer()) {
            AppResult.Success(listOf(ReferenceValue("network")))
        }
        assertEquals(AppResult.Success(listOf(ReferenceValue("network"))), networkResult)

        val futureCache = MemoryCache().apply {
            values["reference-lists:land"] = FeatureCacheEntry(
                payload = Json.encodeToString(listOf(ReferenceValue("future"))),
                updatedAtEpochMillis = kotlin.time.Clock.System.now().toEpochMilliseconds() + 60 * 60 * 1_000L,
            )
        }
        val futureReferenceCache = ReferenceListCache(futureCache, Json, "reference-lists", "land")
        var networkCalls = 0
        val futureResult = futureReferenceCache.get(ReferenceValue.serializer()) {
            networkCalls += 1
            AppResult.Success(listOf(ReferenceValue("network")))
        }
        assertEquals(AppResult.Success(listOf(ReferenceValue("network"))), futureResult)
        assertEquals(1, networkCalls)
    }

    @Test
    fun corruptedFarPastTimestampCannotSuppressRefresh() = runBlocking {
        val cache = MemoryCache().apply {
            values["reference-lists:land"] = FeatureCacheEntry(
                payload = Json.encodeToString(listOf(ReferenceValue("corrupted"))),
                updatedAtEpochMillis = Long.MIN_VALUE,
            )
        }
        val referenceCache = ReferenceListCache(cache, Json, "reference-lists", "land")
        var networkCalls = 0

        val result = referenceCache.get(ReferenceValue.serializer()) {
            networkCalls += 1
            AppResult.Success(listOf(ReferenceValue("network")))
        }

        assertEquals(AppResult.Success(listOf(ReferenceValue("network"))), result)
        assertEquals(1, networkCalls)
    }

    @Test
    fun serializerFailureDoesNotHideNetworkValue() = runBlocking {
        val referenceCache = ReferenceListCache(MemoryCache(), Json, "reference-lists", "land")
        val result = referenceCache.get(ThrowingSerializer) {
            AppResult.Success(listOf(ReferenceValue("network")))
        }
        assertEquals(AppResult.Success(listOf(ReferenceValue("network"))), result)
    }

    @Test
    fun cancellationDuringReadOrWriteIsNeverSwallowed() = runBlocking {
        val readCancelled = ReferenceListCache(
            MemoryCache(failReadWithCancellation = true),
            Json,
            "reference-lists",
            "land",
        )
        assertFailsWith<CancellationException> {
            readCancelled.get(ReferenceValue.serializer()) {
                AppResult.Success(listOf(ReferenceValue("network")))
            }
        }

        val writeCancelled = ReferenceListCache(
            MemoryCache(failWriteWithCancellation = true),
            Json,
            "reference-lists",
            "land",
        )
        assertFailsWith<CancellationException> {
            writeCancelled.get(ReferenceValue.serializer()) {
                AppResult.Success(listOf(ReferenceValue("network")))
            }
        }
        Unit
    }

    @Serializable
    private data class ReferenceValue(val name: String)

    private object ThrowingSerializer : KSerializer<ReferenceValue> {
        override val descriptor: SerialDescriptor =
            PrimitiveSerialDescriptor("ReferenceValue", PrimitiveKind.STRING)

        override fun serialize(encoder: Encoder, value: ReferenceValue): Unit =
            throw SerializationException("serializer unavailable")

        override fun deserialize(decoder: Decoder): ReferenceValue =
            error("not used")
    }

    private class MemoryCache(
        private val failWrites: Boolean = false,
        private val failReads: Boolean = false,
        private val failReadWithCancellation: Boolean = false,
        private val failWriteWithCancellation: Boolean = false,
    ) : FeatureCache {
        val values = mutableMapOf<String, FeatureCacheEntry>()

        override suspend fun read(namespace: String, key: String): FeatureCacheEntry? {
            if (failReadWithCancellation) throw CancellationException("cancelled")
            if (failReads) error("cache unavailable")
            return values["$namespace:$key"]
        }

        override suspend fun write(namespace: String, key: String, payload: String, updatedAtEpochMillis: Long) {
            if (failWriteWithCancellation) throw CancellationException("cancelled")
            if (failWrites) error("cache unavailable")
            values["$namespace:$key"] = FeatureCacheEntry(payload, updatedAtEpochMillis)
        }

        override suspend fun clear(namespace: String, key: String) {
            values.remove("$namespace:$key")
        }

        override suspend fun clearNamespace(namespace: String) {
            values.keys.removeAll { it.startsWith("$namespace:") }
        }
    }
}
