package com.wealthvault.api

import com.wealthvault.data.portfolio.cash.transport.createcash.CreateCashApiImpl
import com.wealthvault.data.portfolio.cash.transport.deletecash.DeleteCashApiImpl
import com.wealthvault.data.portfolio.cash.transport.updatecash.UpdateCashApiImpl
import com.wealthvault.data.portfolio.building.transport.deletebuilding.DeleteBuildingApiImpl
import com.wealthvault.data.portfolio.building.transport.updatebuilding.UpdateBuildingApiImpl
import com.wealthvault.data.portfolio.investment.transport.createcash.CreateBuildingApiImpl
import com.wealthvault.data.portfolio.insurance.transport.createcash.CreateInsuranceApiImpl
import com.wealthvault.data.portfolio.insurance.transport.deleteinsurance.DeleteInsuranceApiImpl
import com.wealthvault.data.portfolio.insurance.transport.updateinsurance.UpdateInsuranceApiImpl
import com.wealthvault.data.portfolio.investment.transport.createinvestment.CreateInvestmentApiImpl
import com.wealthvault.data.portfolio.investment.transport.deleteinvestment.DeleteInvestmentApiImpl
import com.wealthvault.data.portfolio.investment.transport.updateinvestment.UpdateInvestmentApiImpl
import com.wealthvault.data.portfolio.investment.transport.createcash.CreateLandApiImpl
import com.wealthvault.data.portfolio.land.transport.deleteland.DeleteLandApiImpl
import com.wealthvault.data.portfolio.land.transport.updateland.UpdateLandApiImpl
import com.wealthvault.data.portfolio.investment.transport.createcash.CreateLiabilityApiImpl
import com.wealthvault.data.portfolio.liability.transport.deleteliability.DeleteLiabilityApiImpl
import com.wealthvault.data.portfolio.liability.transport.updateliability.UpdateLiabilityApiImpl
import com.wealthvault.core.model.Money
import com.wealthvault.core.model.FixedDecimal
import com.wealthvault.domain.portfolio.BuildingFileUploadData
import com.wealthvault.domain.portfolio.BuildingRequest
import com.wealthvault.domain.portfolio.BuildingReferenceData
import com.wealthvault.domain.portfolio.CashFileUploadData
import com.wealthvault.domain.portfolio.CashRequest
import com.wealthvault.domain.portfolio.FileUploadData
import com.wealthvault.domain.portfolio.InsReferenceData
import com.wealthvault.domain.portfolio.InsuranceFileUploadData
import com.wealthvault.domain.portfolio.InsuranceRequest
import com.wealthvault.domain.portfolio.InvestmentRequest
import com.wealthvault.domain.portfolio.LandFileUploadData
import com.wealthvault.domain.portfolio.LandReferenceData
import com.wealthvault.domain.portfolio.LandRequest
import com.wealthvault.domain.portfolio.LiabilityRequest
import com.wealthvault.domain.portfolio.LiabilityUploadData
import io.ktor.client.HttpClient
import io.ktor.client.engine.mock.MockEngine
import io.ktor.client.engine.mock.respond
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.headersOf
import io.ktor.serialization.kotlinx.json.json
import kotlinx.coroutines.test.runTest
import kotlinx.serialization.json.Json
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class PortfolioCrudApiBoundaryTest {
    @Test
    fun cashBuildingInsuranceInvestmentLandAndLiabilityWritePathsMapResponses() = runTest {
        val cashRequest = CashRequest(
            name = "Cash", amount = Money(100), description = "wallet",
            files = listOf(CashFileUploadData(byteArrayOf(1), "image/png", "cash.png")),
            deleteListId = listOf("cash-old"),
        )
        assertEquals("cash-1", CreateCashApiImpl(mockClient("""{"data":{"id":"cash-1","user_id":"u-1","amount":1.00}}""")).create(cashRequest).id)
        assertEquals("cash-1", UpdateCashApiImpl(mockClient("""{"data":{"id":"cash-1","user_id":"u-1","amount":2.00}}""")).updateCash("cash-1", cashRequest).id)
        DeleteCashApiImpl(mockClient(DELETE_RESPONSE)).deleteCash("cash-1")

        val buildingRequest = BuildingRequest(
            type = "HOUSE", name = "Home", area = 10.0, amount = Money(1000), description = "home",
            locationAddress = "1 Main", locationSubDistrict = "Sub", locationDistrict = "District",
            locationProvince = "Bangkok", locationPostalCode = "10100",
            insIds = listOf(InsReferenceData("Cover", "ins-1")),
            files = listOf(BuildingFileUploadData(byteArrayOf(2), "image/png", "home.png")),
            referenceIds = listOf(BuildingReferenceData("Road", "ref-1")),
            deleteListId = listOf("building-old"),
            deleteRefListId = listOf(BuildingReferenceData("Old road", "ref-old")),
            deleteInsListId = listOf(InsReferenceData("Old cover", "ins-old")),
        )
        assertEquals("building-1", CreateBuildingApiImpl(mockClient("""{"data":{"id":"building-1","user_id":"u-1","amount":10.00}}""")).create(buildingRequest).id)
        assertEquals("building-1", UpdateBuildingApiImpl(mockClient("""{"data":{"id":"building-1","user_id":"u-1","amount":11.00}}""")).updateBuilding("building-1", buildingRequest).id)
        DeleteBuildingApiImpl(mockClient(DELETE_RESPONSE)).deleteBuilding("building-1")

        val insuranceRequest = InsuranceRequest(
            name = "Cover", policyNumber = "P-1", type = "life", companyName = "Insurer",
            coveragePeriod = "12", coverageAmount = Money(2000), conDate = "2025-01-01", expDate = "2026-01-01",
            description = "policy", files = listOf(InsuranceFileUploadData(byteArrayOf(3), "image/png", "policy.png")),
            deleteListId = listOf("insurance-old"),
        )
        assertEquals("insurance-1", CreateInsuranceApiImpl(mockClient("""{"data":{"id":"insurance-1","user_id":"u-1","coverage_amount":20.00}}""")).create(insuranceRequest).id)
        assertEquals("insurance-1", UpdateInsuranceApiImpl(mockClient("""{"data":{"id":"insurance-1","user_id":"u-1","coverage_amount":21.00}}""")).updateInsurance("insurance-1", insuranceRequest).id)
        DeleteInsuranceApiImpl(mockClient(DELETE_RESPONSE)).deleteInsurance("insurance-1")

        val investmentRequest = InvestmentRequest(
            name = "Fund", symbol = "FND", type = "stock", brokerName = "Broker",
            quantity = FixedDecimal.fromDecimal("10.5", scale = 4),
            costPerPrice = Money.fromDecimal("20.25"), description = "fund",
            files = listOf(FileUploadData(byteArrayOf(4), "image/png", "fund.png")),
            deleteListId = listOf("investment-old"),
        )
        assertEquals("investment-1", CreateInvestmentApiImpl(mockClient("""{"data":{"id":"investment-1","user_id":"u-1","quantity":10.5,"cost_per_price":20.25}}""")).create(investmentRequest).id)
        assertEquals("investment-1", UpdateInvestmentApiImpl(mockClient("""{"data":{"id":"investment-1","user_id":"u-1","quantity":11.5,"cost_per_price":21.25}}""")).updateInvestment("investment-1", investmentRequest).id)
        DeleteInvestmentApiImpl(mockClient(DELETE_RESPONSE)).deleteInvestment("investment-1")

        val landRequest = LandRequest(
            name = "Land", deedNum = "D-1", area = 20.0, amount = Money(3000), description = "plot",
            locationAddress = "2 Main", locationSubDistrict = "Sub", locationDistrict = "District",
            locationProvince = "Bangkok", locationPostalCode = "10200",
            files = listOf(LandFileUploadData(byteArrayOf(5), "image/png", "land.png")),
            referenceIds = listOf(LandReferenceData("Deed", "ref-1")), deleteListId = listOf("land-old"),
            deleteRefListId = listOf(LandReferenceData("Old", "ref-old")),
        )
        assertEquals("land-1", CreateLandApiImpl(mockClient("""{"data":{"id":"land-1","user_id":"u-1","amount":30}}""")).create(landRequest).id)
        assertEquals("land-1", UpdateLandApiImpl(mockClient("""{"data":{"id":"land-1","user_id":"u-1","amount":31}}""")).updateLand("land-1", landRequest).id)
        DeleteLandApiImpl(mockClient(DELETE_RESPONSE)).deleteLand("land-1")

        val liabilityRequest = LiabilityRequest(
            type = "loan", name = "Loan", creditor = "Bank", principal = Money(4000),
            interestRate = FixedDecimal.fromDecimal("5.25", scale = 4),
            description = "debt", startedAt = "2025-01-01", endedAt = "2030-01-01",
            files = listOf(LiabilityUploadData(byteArrayOf(6), "image/png", "loan.png")), deleteListId = listOf("debt-old"),
        )
        assertEquals("liability-1", CreateLiabilityApiImpl(mockClient("""{"data":{"id":"liability-1","user_id":"u-1","principal":40.00,"interest_rate":5.25}}""")).create(liabilityRequest).id)
        assertEquals("liability-1", UpdateLiabilityApiImpl(mockClient("""{"data":{"id":"liability-1","user_id":"u-1","principal":41.00,"interest_rate":5.25}}""")).updateLiability("liability-1", liabilityRequest).id)
        DeleteLiabilityApiImpl(mockClient(DELETE_RESPONSE)).deleteLiability("liability-1")
    }

    @Test
    fun writeResponsesWithBackendErrorsNeverBecomeSuccessfulDomainValues() = runTest {
        val error = "{\"error\":\"backend rejected request\"}"
        assertFailsWith<IllegalStateException> {
            CreateCashApiImpl(mockClient(error)).create(CashRequest(name = "Cash"))
        }
        assertFailsWith<IllegalStateException> {
            CreateBuildingApiImpl(mockClient(error)).create(BuildingRequest(name = "Home"))
        }
        assertFailsWith<IllegalStateException> {
            CreateInsuranceApiImpl(mockClient(error)).create(InsuranceRequest(name = "Cover"))
        }
        assertFailsWith<IllegalStateException> {
            CreateInvestmentApiImpl(mockClient(error)).create(InvestmentRequest(name = "Fund"))
        }
        assertFailsWith<IllegalStateException> {
            CreateLandApiImpl(mockClient(error)).create(LandRequest(name = "Land"))
        }
        assertFailsWith<IllegalStateException> {
            CreateLiabilityApiImpl(mockClient(error)).create(LiabilityRequest(name = "Debt"))
        }
        assertFailsWith<IllegalStateException> {
            DeleteCashApiImpl(mockClient(error)).deleteCash("cash-1")
        }
        assertFailsWith<IllegalStateException> {
            DeleteBuildingApiImpl(mockClient(error)).deleteBuilding("building-1")
        }
        assertFailsWith<IllegalStateException> {
            DeleteInsuranceApiImpl(mockClient(error)).deleteInsurance("insurance-1")
        }
        assertFailsWith<IllegalStateException> {
            DeleteInvestmentApiImpl(mockClient(error)).deleteInvestment("investment-1")
        }
        assertFailsWith<IllegalStateException> {
            DeleteLandApiImpl(mockClient(error)).deleteLand("land-1")
        }
        assertFailsWith<IllegalStateException> {
            DeleteLiabilityApiImpl(mockClient(error)).deleteLiability("liability-1")
        }
    }

    private fun mockClient(payload: String) = HttpClient(MockEngine) {
        install(ContentNegotiation) { json(Json { ignoreUnknownKeys = true }) }
        engine {
            addHandler {
                respond(
                    content = payload,
                    headers = headersOf(HttpHeaders.ContentType, ContentType.Application.Json.toString()),
                )
            }
        }
    }

    private companion object {
        const val DELETE_RESPONSE = "{\"status\":\"success\",\"data\":{\"success\":\"true\"}}"
    }
}
