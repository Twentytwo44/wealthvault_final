package com.wealthvault.data.portfolio.repository

import com.wealthvault.core.architecture.AppResult
import com.wealthvault.core.architecture.CacheFreshness
import com.wealthvault.core.cache.FeatureCache
import com.wealthvault.core.cache.FeatureCacheEntry
import com.wealthvault.core.model.FixedDecimal
import com.wealthvault.core.model.Money
import com.wealthvault.domain.portfolio.AccountData
import com.wealthvault.domain.portfolio.AssetFile
import com.wealthvault.domain.portfolio.BankAccountData
import com.wealthvault.domain.portfolio.BuildingInsurance
import com.wealthvault.domain.portfolio.BuildingIdData
import com.wealthvault.domain.portfolio.BuildingLocation
import com.wealthvault.domain.portfolio.BuildingReference
import com.wealthvault.domain.portfolio.CashIdData
import com.wealthvault.domain.portfolio.GetBuildingData
import com.wealthvault.domain.portfolio.GetCashData
import com.wealthvault.domain.portfolio.GetInsuranceData
import com.wealthvault.domain.portfolio.GetInvestmentData
import com.wealthvault.domain.portfolio.GetLandData
import com.wealthvault.domain.portfolio.GetLiabilityData
import com.wealthvault.domain.portfolio.InsuranceIdData
import com.wealthvault.domain.portfolio.InvestmentIdData
import com.wealthvault.domain.portfolio.LandIdData
import com.wealthvault.domain.portfolio.LandLocation
import com.wealthvault.domain.portfolio.LandReference
import com.wealthvault.domain.portfolio.LiabilityIdData
import com.wealthvault.data.portfolio.repository.FinanciallistRemoteDataSource
import com.wealthvault.data.portfolio.repository.FinanciallistRepositoryImpl
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import kotlinx.serialization.json.Json
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs

class FinanciallistRepositoryTest {
    @Test
    fun listAndDetailReadsMapThroughTheCacheBoundary() = runTest {
        val source = FakeRemoteDataSource()
        val repository = FinanciallistRepositoryImpl(source, MemoryCache(), Json)

        assertEquals(listOf(source.account), assertIs<AppResult.Success<List<AccountData>>>(repository.getAccounts()).value)
        assertEquals(listOf(source.cash), assertIs<AppResult.Success<List<GetCashData>>>(repository.getCashes()).value)
        assertEquals(listOf(source.investment), assertIs<AppResult.Success<List<GetInvestmentData>>>(repository.getInvestments()).value)
        assertEquals(listOf(source.insurance), assertIs<AppResult.Success<List<GetInsuranceData>>>(repository.getInsurances()).value)
        assertEquals(listOf(source.building), assertIs<AppResult.Success<List<GetBuildingData>>>(repository.getBuildings()).value)
        assertEquals(listOf(source.land), assertIs<AppResult.Success<List<GetLandData>>>(repository.getLands()).value)
        assertEquals(listOf(source.liability), assertIs<AppResult.Success<List<GetLiabilityData>>>(repository.getLiabilities()).value)

        assertEquals(source.accountById, assertIs<AppResult.Success<BankAccountData>>(repository.getAccountById("a-1")).value)
        assertEquals(source.buildingById, assertIs<AppResult.Success<BuildingIdData>>(repository.getBuildingById("b-1")).value)
        assertEquals(source.cashById, assertIs<AppResult.Success<CashIdData>>(repository.getCashById("c-1")).value)
        assertEquals(source.insuranceById, assertIs<AppResult.Success<InsuranceIdData>>(repository.getInsuranceById("i-1")).value)
        assertEquals(source.investmentById, assertIs<AppResult.Success<InvestmentIdData>>(repository.getInvestmentById("v-1")).value)
        assertEquals(source.landById, assertIs<AppResult.Success<LandIdData>>(repository.getLandById("l-1")).value)
        assertEquals(source.liabilityById, assertIs<AppResult.Success<LiabilityIdData>>(repository.getLiabilityById("d-1")).value)

        // Read the same values again so the JSON cache snapshots exercise
        // every nested mapper, not just the network-to-domain path.
        assertEquals(source.building, assertIs<AppResult.Success<List<GetBuildingData>>>(repository.getBuildings()).value.single())
        assertEquals(source.land, assertIs<AppResult.Success<List<GetLandData>>>(repository.getLands()).value.single())
        assertEquals(source.accountById, assertIs<AppResult.Success<BankAccountData>>(repository.getAccountById("a-1")).value)
        assertEquals(source.buildingById, assertIs<AppResult.Success<BuildingIdData>>(repository.getBuildingById("b-1")).value)
        assertEquals(source.cashById, assertIs<AppResult.Success<CashIdData>>(repository.getCashById("c-1")).value)
        assertEquals(source.insuranceById, assertIs<AppResult.Success<InsuranceIdData>>(repository.getInsuranceById("i-1")).value)
        assertEquals(source.investmentById, assertIs<AppResult.Success<InvestmentIdData>>(repository.getInvestmentById("v-1")).value)
        assertEquals(source.landById, assertIs<AppResult.Success<LandIdData>>(repository.getLandById("l-1")).value)
        assertEquals(source.liabilityById, assertIs<AppResult.Success<LiabilityIdData>>(repository.getLiabilityById("d-1")).value)

        val observed = repository.observeAccounts().first()
        assertEquals(CacheFreshness.Fresh, observed.freshness)
        assertEquals(1, observed.value.size)
    }

    @Test
    fun freshReadsAreSingleFlightAndForceRefreshBypassesTheSnapshot() = runTest {
        val source = FakeRemoteDataSource()
        val repository = FinanciallistRepositoryImpl(source, MemoryCache(), Json)

        repository.getAccounts()
        repository.getAccounts()
        assertEquals(1, source.accountCalls)

        repository.getAccounts(force = true)
        assertEquals(2, source.accountCalls)
    }

    @Test
    fun staleCacheIsServedOfflineAndRefreshAggregatesFailures() = runTest {
        val cache = MemoryCache()
        val source = FakeRemoteDataSource()
        val repository = FinanciallistRepositoryImpl(source, cache, Json)

        repository.getAccounts()
        val key = "portfolio:accounts"
        val entry = cache.values.getValue(key)
        cache.values[key] = entry.copy(updatedAtEpochMillis = 1L)
        source.failAccounts = true

        val stale = assertIs<AppResult.Success<List<AccountData>>>(repository.getAccounts()).value
        assertEquals(source.account, stale.single())
        assertEquals(CacheFreshness.Offline, repository.observeAccounts().first().freshness)

        cache.values.remove("portfolio:accounts")
        val failedRefresh = repository.refresh(force = true)
        assertIs<AppResult.Failure>(failedRefresh)
    }

    @Test
    fun successfulDeleteInvalidatesBothPortfolioNamespaces() = runTest {
        val cache = MemoryCache()
        val repository = FinanciallistRepositoryImpl(FakeRemoteDataSource(), cache, Json)
        repository.getAccounts()
        repository.getCashes()

        val result = repository.deleteAsset("a-1", "account")

        assertEquals(AppResult.Success(true), result)
        assertEquals(emptySet(), cache.values.keys)
        assertEquals(setOf("portfolio", "reference-lists"), cache.clearedNamespaces.toSet())
    }

    private class MemoryCache : FeatureCache {
        val values = mutableMapOf<String, FeatureCacheEntry>()
        val clearedNamespaces = mutableListOf<String>()

        override suspend fun read(namespace: String, key: String): FeatureCacheEntry? = values["$namespace:$key"]

        override suspend fun write(namespace: String, key: String, payload: String, updatedAtEpochMillis: Long) {
            values["$namespace:$key"] = FeatureCacheEntry(payload, updatedAtEpochMillis)
        }

        override suspend fun clear(namespace: String, key: String) {
            values.remove("$namespace:$key")
        }

        override suspend fun clearNamespace(namespace: String) {
            clearedNamespaces += namespace
            values.keys.removeAll { it.startsWith("$namespace:") }
        }
    }

    private class FakeRemoteDataSource : FinanciallistRemoteDataSource {
        private val file = AssetFile("file-1", "https://cdn/file", "image/png")
        private val buildingLocation = BuildingLocation("loc-b", "1 Main", "Sub", "District", "Bangkok", "10100", "created", "updated")
        private val landLocation = LandLocation("loc-l", "2 Main", "Sub", "District", "Bangkok", "10200", "created", "updated")
        val account = AccountData("a-1", "u-1", name = "Account", amount = Money(100))
        val cash = GetCashData("c-1", "u-1", name = "Cash", ammount = Money(200))
        val investment = GetInvestmentData("v-1", "u-1", name = "Fund", quantity = FixedDecimal(1234, 2), costPerPrice = Money(301), amount = Money(300))
        val insurance = GetInsuranceData("i-1", "u-1", name = "Cover", coverageAmount = Money(400))
        val building = GetBuildingData(
            "b-1", "u-1", name = "Home", amount = Money(500), location = buildingLocation,
            ins = listOf(BuildingInsurance("ins-1", "Home cover")), referenceIds = listOf("ref-1"), files = listOf(file),
        )
        val land = GetLandData("l-1", "u-1", name = "Land", amount = Money(600), location = landLocation)
        val liability = GetLiabilityData("d-1", "u-1", name = "Debt", principal = Money(700), interestRate = FixedDecimal(250, 2))

        val accountById = BankAccountData("a-1", "u-1", name = "Account", amount = Money(100), files = listOf(file), createdAt = "created", updatedAt = "updated")
        val buildingById = BuildingIdData(
            "b-1", "u-1", name = "Home", amount = Money(500), location = buildingLocation,
            ins = listOf(BuildingInsurance("ins-1", "Home cover")), referenceIds = listOf(BuildingReference("ref-1", "Road")),
            files = listOf(file), createdAt = "created", updatedAt = "updated",
        )
        val cashById = CashIdData("c-1", "u-1", name = "Cash", amount = Money(200), files = listOf(file), createdAt = "created", updatedAt = "updated")
        val insuranceById = InsuranceIdData("i-1", "u-1", name = "Cover", coverageAmount = Money(400), files = listOf(file), createdAt = "created", updatedAt = "updated")
        val investmentById = InvestmentIdData("v-1", "u-1", name = "Fund", quantity = FixedDecimal(1234, 2), costPerPrice = Money(301), amount = Money(300), files = listOf(file), createdAt = "created", updatedAt = "updated")
        val landById = LandIdData("l-1", "u-1", name = "Land", amount = Money(600), location = landLocation, files = listOf(file), ref = listOf(LandReference("ref-2", "Deed")), createdAt = "created", updatedAt = "updated")
        val liabilityById = LiabilityIdData("d-1", "u-1", name = "Debt", principal = Money(700), interestRate = FixedDecimal(250, 2), files = listOf(file), createdAt = "created", updatedAt = "updated")

        var accountCalls = 0
        var failAccounts = false

        override suspend fun getAccount(): List<AccountData> {
            accountCalls += 1
            if (failAccounts) error("offline")
            return listOf(account)
        }

        override suspend fun getCash() = listOf(cash)
        override suspend fun getInvestment() = listOf(investment)
        override suspend fun getInsurance() = listOf(insurance)
        override suspend fun getBuilding() = listOf(building)
        override suspend fun getLand() = listOf(land)
        override suspend fun getLiability() = listOf(liability)
        override suspend fun getAccountById(id: String) = accountById
        override suspend fun getBuildingById(id: String) = buildingById
        override suspend fun getCashById(id: String) = cashById
        override suspend fun getInsuranceById(id: String) = insuranceById
        override suspend fun getInvestmentById(id: String) = investmentById
        override suspend fun getLandById(id: String) = landById
        override suspend fun getLiabilityById(id: String) = liabilityById
        override suspend fun deleteAsset(id: String, type: String) = true
    }
}
