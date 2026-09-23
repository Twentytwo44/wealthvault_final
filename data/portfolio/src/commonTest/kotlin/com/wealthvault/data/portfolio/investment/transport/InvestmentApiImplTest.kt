package com.wealthvault.data.portfolio.investment.transport

import com.wealthvault.core.model.FixedDecimal
import com.wealthvault.core.model.Money
import com.wealthvault.data.portfolio.investment.transport.getinvestment.GetInvestmentApiImpl
import com.wealthvault.data.portfolio.investment.transport.getinvestmentbyid.GetInvestmentByIdApiImpl
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

class InvestmentApiImplTest {
    @Test
    fun mapsInvestmentListToFixedPointDomainValues() = runTest {
        val client = mockClient(
            """
            {
              "data": [{
                "id": "investment-1",
                "user_id": "user-1",
                "name": "Fund",
                "quantity": 12.3456,
                "cost_per_price": 100.25,
                "amount": 1237.50
              }]
            }
            """.trimIndent(),
        )

        val result = GetInvestmentApiImpl(client).getInvestment().single()

        assertEquals("investment-1", result.id)
        assertEquals(FixedDecimal.fromDecimal("12.3456", scale = 4), result.quantity)
        assertEquals(Money.fromDecimal("100.25"), result.costPerPrice)
        assertEquals(Money.fromDecimal("1237.50"), result.amount)
        client.close()
    }

    @Test
    fun mapsInvestmentDetailAndFilesToDomain() = runTest {
        val client = mockClient(
            """
            {
              "data": {
                "id": "investment-2",
                "user_id": "user-2",
                "quantity": 3.5,
                "cost_per_price": 250.50,
                "amount": 876.75,
                "files": [{"id": "file-1", "url": "https://cdn/statement.pdf", "file_type": "application/pdf"}]
              }
            }
            """.trimIndent(),
        )

        val result = GetInvestmentByIdApiImpl(client).getInvestmentById("investment-2")

        assertEquals("investment-2", result?.id)
        assertEquals(FixedDecimal.fromDecimal("3.5", scale = 4), result?.quantity)
        assertEquals(Money.fromDecimal("250.50"), result?.costPerPrice)
        assertEquals(Money.fromDecimal("876.75"), result?.amount)
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
