package com.wealthvault.financiallist

import com.wealthvault.data.portfolio.account.transport.deleteaccount.DeleteAccountApi
import com.wealthvault.data.portfolio.account.transport.getaccount.GetAccountApi
import com.wealthvault.data.portfolio.account.transport.getaccountbyid.GetAccountByIdApi
import com.wealthvault.data.portfolio.building.transport.deletebuilding.DeleteBuildingApi
import com.wealthvault.data.portfolio.building.transport.getbuilding.GetBuildingApi
import com.wealthvault.data.portfolio.building.transport.getbuildingbyid.GetBuildingByIdApi
import com.wealthvault.data.portfolio.cash.transport.deletecash.DeleteCashApi
import com.wealthvault.data.portfolio.cash.transport.getcash.GetCashApi
import com.wealthvault.data.portfolio.cash.transport.getcashtbyid.GetCashByIdApi
import com.wealthvault.core.model.Money
import com.wealthvault.domain.portfolio.AccountData
import com.wealthvault.domain.portfolio.BankAccountData
import com.wealthvault.domain.portfolio.BuildingIdData
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
import com.wealthvault.domain.portfolio.LiabilityIdData
import com.wealthvault.data.portfolio.repository.FinanciallistDataSource
import com.wealthvault.data.portfolio.insurance.transport.deleteinsurance.DeleteInsuranceApi
import com.wealthvault.data.portfolio.insurance.transport.getinsurance.GetInsuranceApi
import com.wealthvault.data.portfolio.insurance.transport.getinsurancetbyid.GetInsuranceByIdApi
import com.wealthvault.data.portfolio.investment.transport.deleteinvestment.DeleteInvestmentApi
import com.wealthvault.data.portfolio.investment.transport.getinvestment.GetInvestmentApi
import com.wealthvault.data.portfolio.investment.transport.getinvestmentbyid.GetInvestmentByIdApi
import com.wealthvault.data.portfolio.land.transport.deleteland.DeleteLandApi
import com.wealthvault.data.portfolio.land.transport.getland.GetLandApi
import com.wealthvault.data.portfolio.land.transport.getlandbyid.GetLandByIdApi
import com.wealthvault.data.portfolio.liability.transport.deleteliability.DeleteLiabilityApi
import com.wealthvault.data.portfolio.liability.transport.getliability.GetLiabilityApi
import com.wealthvault.data.portfolio.liability.transport.getliabilitybyid.GetLiabilityByIdApi
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class FinanciallistDataSourceTest {
    @Test
    fun delegatesListsDetailsAndAllDeleteTypeAliases() = kotlinx.coroutines.test.runTest {
        val api = FakeApis()
        val source = FinanciallistDataSource(
            api, api, api, api, api, api, api,
            api, api, api, api, api, api, api,
            api, api, api, api, api, api, api,
        )

        assertEquals(listOf(api.account), source.getAccount())
        assertEquals(listOf(api.cash), source.getCash())
        assertEquals(listOf(api.investment), source.getInvestment())
        assertEquals(listOf(api.insurance), source.getInsurance())
        assertEquals(listOf(api.building), source.getBuilding())
        assertEquals(listOf(api.land), source.getLand())
        assertEquals(listOf(api.liability), source.getLiability())
        assertEquals(api.accountById, source.getAccountById("a"))
        assertEquals(api.buildingById, source.getBuildingById("b"))
        assertEquals(api.cashById, source.getCashById("c"))
        assertEquals(api.insuranceById, source.getInsuranceById("i"))
        assertEquals(api.investmentById, source.getInvestmentById("v"))
        assertEquals(api.landById, source.getLandById("l"))
        assertEquals(api.liabilityById, source.getLiabilityById("d"))

        listOf("account", "cash", "investment", "insurance", "building", "land", "liability", "expense")
            .forEach { type -> assertEquals(true, source.deleteAsset("asset-1", type)) }
        assertFailsWith<IllegalArgumentException> { source.deleteAsset("asset-1", "unknown") }
    }

    private class FakeApis :
        GetAccountApi, GetCashApi, GetInvestmentApi, GetInsuranceApi, GetBuildingApi, GetLandApi, GetLiabilityApi,
        GetAccountByIdApi, GetCashByIdApi, GetBuildingByIdApi, GetInsuranceByIdApi, GetInvestmentByIdApi,
        GetLandByIdApi, GetLiabilityByIdApi, DeleteAccountApi, DeleteCashApi, DeleteInvestmentApi,
        DeleteInsuranceApi, DeleteBuildingApi, DeleteLandApi, DeleteLiabilityApi {
        val account = AccountData("a", "u", amount = Money(1))
        val cash = GetCashData("c", "u", ammount = Money(2))
        val investment = GetInvestmentData("v", "u", amount = Money(3))
        val insurance = GetInsuranceData("i", "u", coverageAmount = Money(4))
        val building = GetBuildingData("b", "u", amount = Money(5))
        val land = GetLandData("l", "u", amount = Money(6))
        val liability = GetLiabilityData("d", "u", principal = Money(7))
        val accountById = BankAccountData("a", "u")
        val buildingById = BuildingIdData("b", "u")
        val cashById = CashIdData("c", "u")
        val insuranceById = InsuranceIdData("i", "u")
        val investmentById = InvestmentIdData("v", "u")
        val landById = LandIdData("l", "u")
        val liabilityById = LiabilityIdData("d", "u")

        override suspend fun getAccount() = listOf(account)
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
        override suspend fun deleteAccount(id: String) = Unit
        override suspend fun deleteCash(id: String) = Unit
        override suspend fun deleteInvestment(id: String) = Unit
        override suspend fun deleteInsurance(id: String) = Unit
        override suspend fun deleteBuilding(id: String) = Unit
        override suspend fun deleteLand(id: String) = Unit
        override suspend fun deleteLiability(id: String) = Unit
    }
}
