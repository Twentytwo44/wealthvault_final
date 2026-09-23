package com.wealthvault.api

import com.wealthvault.data.portfolio.account.transport.getaccount.GetAccountApiImpl
import com.wealthvault.data.portfolio.account.transport.getaccountbyid.GetAccountByIdApiImpl
import com.wealthvault.data.portfolio.building.transport.getbuilding.GetBuildingApiImpl
import com.wealthvault.data.portfolio.building.transport.getbuildingbyid.GetBuildingByIdApiImpl
import com.wealthvault.data.portfolio.cash.transport.getcash.GetCashApiImpl
import com.wealthvault.data.portfolio.cash.transport.getcashtbyid.GetCashByIdApiImpl
import com.wealthvault.data.portfolio.insurance.transport.getinsurance.GetInsuranceApiImpl
import com.wealthvault.data.portfolio.insurance.transport.getinsurancetbyid.GetInsuranceByIdApiImpl
import com.wealthvault.data.portfolio.investment.transport.getinvestment.GetInvestmentApiImpl
import com.wealthvault.data.portfolio.investment.transport.getinvestmentbyid.GetInvestmentByIdApiImpl
import com.wealthvault.data.portfolio.land.transport.getland.GetLandApiImpl
import com.wealthvault.data.portfolio.land.transport.getlandbyid.GetLandByIdApiImpl
import com.wealthvault.data.portfolio.liability.transport.getliability.GetLiabilityApiImpl
import com.wealthvault.data.portfolio.liability.transport.getliabilitybyid.GetLiabilityByIdApiImpl
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
import kotlin.test.assertFailsWith

class GetApiErrorBoundaryTest {
    @Test
    fun allListAndDetailAdaptersRejectBackendErrors() = runTest {
        assertFailsWith<IllegalStateException> { GetAccountApiImpl(mockClient(ERROR)).getAccount() }
        assertFailsWith<IllegalStateException> { GetAccountByIdApiImpl(mockClient(ERROR)).getAccountById("a") }
        assertFailsWith<IllegalStateException> { GetCashApiImpl(mockClient(ERROR)).getCash() }
        assertFailsWith<IllegalStateException> { GetCashByIdApiImpl(mockClient(ERROR)).getCashById("c") }
        assertFailsWith<IllegalStateException> { GetBuildingApiImpl(mockClient(ERROR)).getBuilding() }
        assertFailsWith<IllegalStateException> { GetBuildingByIdApiImpl(mockClient(ERROR)).getBuildingById("b") }
        assertFailsWith<IllegalStateException> { GetInsuranceApiImpl(mockClient(ERROR)).getInsurance() }
        assertFailsWith<IllegalStateException> { GetInsuranceByIdApiImpl(mockClient(ERROR)).getInsuranceById("i") }
        assertFailsWith<IllegalStateException> { GetInvestmentApiImpl(mockClient(ERROR)).getInvestment() }
        assertFailsWith<IllegalStateException> { GetInvestmentByIdApiImpl(mockClient(ERROR)).getInvestmentById("v") }
        assertFailsWith<IllegalStateException> { GetLandApiImpl(mockClient(ERROR)).getLand() }
        assertFailsWith<IllegalStateException> { GetLandByIdApiImpl(mockClient(ERROR)).getLandById("l") }
        assertFailsWith<IllegalStateException> { GetLiabilityApiImpl(mockClient(ERROR)).getLiability() }
        assertFailsWith<IllegalStateException> { GetLiabilityByIdApiImpl(mockClient(ERROR)).getLiabilityById("d") }
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
        const val ERROR = "{\"error\":\"backend unavailable\"}"
    }
}
