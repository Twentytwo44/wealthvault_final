package com.wealthvault.data.portfolio.insurance.transport

import com.wealthvault.data.portfolio.insurance.transport.getinsurance.GetInsuranceApiImpl
import com.wealthvault.data.portfolio.insurance.transport.getinsurancetbyid.GetInsuranceByIdApiImpl
import com.wealthvault.core.model.Money
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

class InsuranceApiImplTest {
    @Test
    fun mapsInsuranceListToDomainMoney() = runTest {
        val client = mockClient(
            """
            {
              "data": [{
                "id": "insurance-1",
                "user_id": "user-1",
                "name": "Health",
                "policy_number": "P-1",
                "coverage_period": 12,
                "coverage_amount": 50000.25
              }]
            }
            """.trimIndent(),
        )

        val result = GetInsuranceApiImpl(client).getInsurance().single()

        assertEquals("insurance-1", result.id)
        assertEquals(Money.fromDecimal("50000.25"), result.coverageAmount)
        assertEquals(12, result.coveragePeriod)
        client.close()
    }

    @Test
    fun mapsInsuranceDetailAndFilesToDomain() = runTest {
        val client = mockClient(
            """
            {
              "data": {
                "id": "insurance-2",
                "user_id": "user-2",
                "coverage_amount": 2500.50,
                "files": [{"id": "file-1", "url": "https://cdn/policy.pdf", "file_type": "application/pdf"}]
              }
            }
            """.trimIndent(),
        )

        val result = GetInsuranceByIdApiImpl(client).getInsuranceById("insurance-2")

        assertEquals("insurance-2", result?.id)
        assertEquals(Money.fromDecimal("2500.50"), result?.coverageAmount)
        assertEquals("file-1", result?.files?.single()?.id)
        client.close()
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
}
