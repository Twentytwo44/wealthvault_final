package com.wealthvault.data.portfolio.repository

import com.wealthvault.data.portfolio.account.transport.updateaccount.UpdateAccountApi
import com.wealthvault.data.portfolio.building.transport.updatebuilding.UpdateBuildingApi
import com.wealthvault.data.portfolio.cash.transport.updatecash.UpdateCashApi
import com.wealthvault.core.architecture.AppResult
import com.wealthvault.core.cache.FeatureCache
import com.wealthvault.core.cache.FeatureCacheEntry
import com.wealthvault.core.model.Money
import com.wealthvault.domain.portfolio.BankAccountData
import com.wealthvault.domain.portfolio.BankAccountRequest
import com.wealthvault.domain.portfolio.BuildingData
import com.wealthvault.domain.portfolio.BuildingRequest
import com.wealthvault.domain.portfolio.CashData
import com.wealthvault.domain.portfolio.CashRequest
import com.wealthvault.domain.portfolio.InsuranceData
import com.wealthvault.domain.portfolio.InsuranceRequest
import com.wealthvault.domain.portfolio.InvestmentData
import com.wealthvault.domain.portfolio.InvestmentRequest
import com.wealthvault.domain.portfolio.LandData
import com.wealthvault.domain.portfolio.LandRequest
import com.wealthvault.domain.portfolio.LiabilityData
import com.wealthvault.domain.portfolio.LiabilityRequest
import com.wealthvault.data.portfolio.repository.account.BankAccountNetworkDataSource
import com.wealthvault.data.portfolio.repository.account.BankAccountRepositoryImpl
import com.wealthvault.data.portfolio.repository.building.BuildingNetworkDataSource
import com.wealthvault.data.portfolio.repository.building.BuildingRepositoryImpl
import com.wealthvault.data.portfolio.repository.cash.CashNetworkDataSource
import com.wealthvault.data.portfolio.repository.cash.CashRepositoryImpl
import com.wealthvault.data.portfolio.repository.debt.LiabilityNetworkDataSource
import com.wealthvault.data.portfolio.repository.debt.LiabilityRepositoryImpl
import com.wealthvault.data.portfolio.repository.insurance.InsuranceNetworkDataSource
import com.wealthvault.data.portfolio.repository.insurance.InsuranceRepositoryImpl
import com.wealthvault.data.portfolio.repository.investment.AssetNetworkDataSource
import com.wealthvault.data.portfolio.repository.investment.AssetRepositoryImpl
import com.wealthvault.data.portfolio.repository.land.LandNetworkDataSource
import com.wealthvault.data.portfolio.repository.land.LandRepositoryImpl
import com.wealthvault.data.portfolio.insurance.transport.updateinsurance.UpdateInsuranceApi
import com.wealthvault.data.portfolio.investment.transport.updateinvestment.UpdateInvestmentApi
import com.wealthvault.data.portfolio.land.transport.updateland.UpdateLandApi
import com.wealthvault.data.portfolio.liability.transport.updateliability.UpdateLiabilityApi
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs

class PortfolioMutationRepositoriesTest {
    @Test
    fun allUpdateRepositoriesDelegateAndInvalidateBothCaches() = runTest {
        val cache = RecordingCache()
        val accountApi = FakeAccountApi()
        val buildingApi = FakeBuildingApi()
        val cashApi = FakeCashApi()
        val insuranceApi = FakeInsuranceApi()
        val investmentApi = FakeInvestmentApi()
        val landApi = FakeLandApi()
        val liabilityApi = FakeLiabilityApi()

        assertEquals("a-1", assertIs<AppResult.Success<BankAccountData>>(
            BankAccountRepositoryImpl(BankAccountNetworkDataSource(accountApi), cache)
                .updateAccount("a-1", BankAccountRequest("name", "bank", "123", "saving", Money(100), "desc")),
        ).value.id)
        assertEquals("b-1", assertIs<AppResult.Success<BuildingData>>(
            BuildingRepositoryImpl(BuildingNetworkDataSource(buildingApi), cache)
                .updateBuilding("b-1", BuildingRequest(name = "home")),
        ).value.id)
        assertEquals("c-1", assertIs<AppResult.Success<CashData>>(
            CashRepositoryImpl(CashNetworkDataSource(cashApi), cache)
                .updateCash("c-1", CashRequest(name = "cash")),
        ).value.id)
        assertEquals("i-1", assertIs<AppResult.Success<InsuranceData>>(
            InsuranceRepositoryImpl(InsuranceNetworkDataSource(insuranceApi), cache)
                .updateInsurance("i-1", InsuranceRequest(name = "cover")),
        ).value.id)
        assertEquals("v-1", assertIs<AppResult.Success<InvestmentData>>(
            AssetRepositoryImpl(AssetNetworkDataSource(investmentApi), cache)
                .updateInvestment("v-1", InvestmentRequest(name = "fund")),
        ).value.id)
        assertEquals("l-1", assertIs<AppResult.Success<LandData>>(
            LandRepositoryImpl(LandNetworkDataSource(landApi), cache)
                .updateLand("l-1", LandRequest(name = "land")),
        ).value.id)
        assertEquals("d-1", assertIs<AppResult.Success<LiabilityData>>(
            LiabilityRepositoryImpl(LiabilityNetworkDataSource(liabilityApi), cache)
                .updateLiability("d-1", LiabilityRequest(name = "debt")),
        ).value.id)

        assertEquals(14, cache.clearedNamespaces.size)
        assertEquals(List(7) { listOf("portfolio", "reference-lists") }.flatten(), cache.clearedNamespaces)

        accountApi.fail = true
        val failed = BankAccountRepositoryImpl(BankAccountNetworkDataSource(accountApi), cache)
            .updateAccount("a-1", BankAccountRequest("name", "bank", "123", "saving", Money(100), "desc"))
        assertIs<AppResult.Failure>(failed)
        assertEquals(14, cache.clearedNamespaces.size)
    }

    private class RecordingCache : FeatureCache {
        val clearedNamespaces = mutableListOf<String>()

        override suspend fun read(namespace: String, key: String): FeatureCacheEntry? = null
        override suspend fun write(namespace: String, key: String, payload: String, updatedAtEpochMillis: Long) = Unit
        override suspend fun clear(namespace: String, key: String) = Unit
        override suspend fun clearNamespace(namespace: String) {
            clearedNamespaces += namespace
        }
    }

    private class FakeAccountApi : UpdateAccountApi {
        var fail = false
        override suspend fun updateAccount(id: String, request: BankAccountRequest): BankAccountData {
            if (fail) error("account unavailable")
            return BankAccountData(id, "u-1")
        }
    }

    private class FakeBuildingApi : UpdateBuildingApi {
        override suspend fun updateBuilding(id: String, request: BuildingRequest) = BuildingData(id = id)
    }

    private class FakeCashApi : UpdateCashApi {
        override suspend fun updateCash(id: String, request: CashRequest) = CashData(id = id)
    }

    private class FakeInsuranceApi : UpdateInsuranceApi {
        override suspend fun updateInsurance(id: String, request: InsuranceRequest) = InsuranceData(id = id)
    }

    private class FakeInvestmentApi : UpdateInvestmentApi {
        override suspend fun updateInvestment(id: String, request: InvestmentRequest) = InvestmentData(id = id)
    }

    private class FakeLandApi : UpdateLandApi {
        override suspend fun updateLand(id: String, request: LandRequest) = LandData(id = id)
    }

    private class FakeLiabilityApi : UpdateLiabilityApi {
        override suspend fun updateLiability(id: String, request: LiabilityRequest) = LiabilityData(id = id)
    }
}
