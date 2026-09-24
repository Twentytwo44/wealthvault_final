package com.wealthvault.data.portfolio.form.asset

import com.wealthvault.data.portfolio.form.asset.data.building.GetBuildingNetworkDataSource
import com.wealthvault.data.portfolio.form.asset.data.building.GetBuildingRepositoryImpl
import com.wealthvault.data.portfolio.form.asset.data.insurance.GetInsuranceNetworkDataSource
import com.wealthvault.data.portfolio.form.asset.data.insurance.GetInsuranceRepositoryImpl
import com.wealthvault.data.portfolio.form.asset.data.land.GetLandNetworkDataSource
import com.wealthvault.data.portfolio.form.asset.data.land.GetLandRepositoryImpl
import com.wealthvault.data.portfolio.building.transport.getbuilding.GetBuildingApi
import com.wealthvault.core.architecture.AppResult
import com.wealthvault.core.cache.FeatureCache
import com.wealthvault.core.cache.FeatureCacheEntry
import com.wealthvault.core.model.Money
import com.wealthvault.domain.portfolio.AssetFile
import com.wealthvault.domain.portfolio.BuildingInsurance
import com.wealthvault.domain.portfolio.BuildingLocation
import com.wealthvault.domain.portfolio.GetBuildingData
import com.wealthvault.domain.portfolio.GetInsuranceData
import com.wealthvault.domain.portfolio.GetLandData
import com.wealthvault.domain.portfolio.LandLocation
import com.wealthvault.data.portfolio.insurance.transport.getinsurance.GetInsuranceApi
import com.wealthvault.data.portfolio.land.transport.getland.GetLandApi
import kotlinx.coroutines.test.runTest
import kotlinx.serialization.json.Json
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs

class ReferenceListRepositoriesTest {
    @Test
    fun buildingReferenceRepositoryMapsNestedCacheValuesAndReusesFreshSnapshot() = runTest {
        val value = GetBuildingData(
            id = "building-1",
            userId = "user-1",
            name = "Home",
            amount = Money(1000),
            location = BuildingLocation(locationId = "loc-1", address = "street"),
            ins = listOf(BuildingInsurance("ins-1", "Cover")),
            files = listOf(AssetFile("file-1", "https://cdn", "image")),
        )
        val api = FakeBuildingApi(listOf(value))
        val repository = GetBuildingRepositoryImpl(GetBuildingNetworkDataSource(api), MemoryCache(), Json)

        assertEquals(AppResult.Success(listOf(value)), repository.getBuilding())
        assertEquals(AppResult.Success(listOf(value)), repository.getBuilding())
        assertEquals(1, api.calls)
    }

    @Test
    fun landReferenceRepositoryPreservesLocationAndOfflineSnapshot() = runTest {
        val value = GetLandData(
            id = "land-1",
            userId = "user-1",
            name = "Land",
            amount = Money(2000),
            location = LandLocation(locationId = "loc-2", province = "Bangkok"),
        )
        val cache = MemoryCache()
        val api = FakeLandApi(listOf(value))
        val repository = GetLandRepositoryImpl(GetLandNetworkDataSource(api), cache, Json)

        assertEquals(AppResult.Success(listOf(value)), repository.getLand())
        val entry = cache.values.getValue("reference-lists:land")
        cache.values["reference-lists:land"] = entry.copy(updatedAtEpochMillis = 1L)
        api.failure = true

        assertEquals(AppResult.Success(listOf(value)), repository.getLand())
    }

    @Test
    fun insuranceReferenceRepositoryMapsAllCoverageFields() = runTest {
        val value = GetInsuranceData(
            id = "insurance-1",
            userId = "user-1",
            name = "Policy",
            policyNumber = "P-1",
            companyName = "Company",
            coveragePeriod = 12,
            coverageAmount = Money(3000),
            conDate = "2024-01-01",
            expDate = "2025-01-01",
        )
        val api = FakeInsuranceApi(listOf(value))
        val repository = GetInsuranceRepositoryImpl(GetInsuranceNetworkDataSource(api), MemoryCache(), Json)

        val result = assertIs<AppResult.Success<List<GetInsuranceData>>>(repository.getInsurance(force = true))
        assertEquals(value, result.value.single())
        assertEquals(1, api.calls)
    }

    private class MemoryCache : FeatureCache {
        val values = mutableMapOf<String, FeatureCacheEntry>()

        override suspend fun read(namespace: String, key: String): FeatureCacheEntry? = values["$namespace:$key"]
        override suspend fun write(namespace: String, key: String, payload: String, updatedAtEpochMillis: Long) {
            values["$namespace:$key"] = FeatureCacheEntry(payload, updatedAtEpochMillis)
        }
        override suspend fun clear(namespace: String, key: String) = Unit
        override suspend fun clearNamespace(namespace: String) = Unit
    }

    private class FakeBuildingApi(private val values: List<GetBuildingData>) : GetBuildingApi {
        var calls = 0
        override suspend fun getBuilding(): List<GetBuildingData> {
            calls += 1
            return values
        }
    }

    private class FakeLandApi(private val values: List<GetLandData>) : GetLandApi {
        var calls = 0
        var failure = false
        override suspend fun getLand(): List<GetLandData> {
            calls += 1
            if (failure) error("offline")
            return values
        }
    }

    private class FakeInsuranceApi(private val values: List<GetInsuranceData>) : GetInsuranceApi {
        var calls = 0
        override suspend fun getInsurance(): List<GetInsuranceData> {
            calls += 1
            return values
        }
    }
}
