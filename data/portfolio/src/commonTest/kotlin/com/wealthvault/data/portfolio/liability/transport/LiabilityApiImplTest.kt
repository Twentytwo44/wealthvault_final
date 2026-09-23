package com.wealthvault.data.portfolio.liability.transport

import com.wealthvault.data.portfolio.liability.transport.getliability.GetLiabilityApiImpl
import com.wealthvault.data.portfolio.liability.transport.getliabilitybyid.GetLiabilityByIdApiImpl
import com.wealthvault.core.model.FixedDecimal
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

class LiabilityApiImplTest {
    @Test
    fun mapsLiabilityListToFixedPointDomainValues() = runTest {
        val client = mockClient(
            """
            {
              "data": [{
                "id": "liability-1",
                "user_id": "user-1",
                "name": "Mortgage",
                "principal": 100000.25,
                "interest_rate": 3.125
              }]
            }
            """.trimIndent(),
        )

        val result = GetLiabilityApiImpl(client).getLiability().single()

        assertEquals("liability-1", result.id)
        assertEquals(Money.fromDecimal("100000.25"), result.principal)
        assertEquals(FixedDecimal.fromDecimal("3.125", scale = 4), result.interestRate)
        client.close()
    }

    @Test
    fun mapsLiabilityDetailAndFilesToDomain() = runTest {
        val client = mockClient(
            """
            {
              "data": {
                "id": "liability-2",
                "user_id": "user-2",
                "principal": 2500.50,
                "interest_rate": 4.75,
                "files": [{"id": "file-1", "url": "https://cdn/loan.pdf", "file_type": "application/pdf"}]
              }
            }
            """.trimIndent(),
        )

        val result = GetLiabilityByIdApiImpl(client).getLiabilityById("liability-2")

        assertEquals("liability-2", result?.id)
        assertEquals(Money.fromDecimal("2500.50"), result?.principal)
        assertEquals(FixedDecimal.fromDecimal("4.75", scale = 4), result?.interestRate)
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
