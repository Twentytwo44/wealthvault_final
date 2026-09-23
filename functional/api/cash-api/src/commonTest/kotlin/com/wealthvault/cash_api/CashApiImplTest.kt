package com.wealthvault.cash_api

import com.wealthvault.cash_api.getcash.GetCashApiImpl
import com.wealthvault.cash_api.getcashtbyid.GetCashByIdApiImpl
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

class CashApiImplTest {
    @Test
    fun mapsListResponseToDomainMoney() = runTest {
        val client = mockClient(
            """
            {
              "data": [{
                "id": "cash-1",
                "user_id": "user-1",
                "name": "Wallet",
                "amount": 125.50,
                "description": "daily cash"
              }]
            }
            """.trimIndent(),
        )

        val result = GetCashApiImpl(client).getCash().single()

        assertEquals("cash-1", result.id)
        assertEquals(Money.fromDecimal("125.50"), result.ammount)
        assertEquals("daily cash", result.description)
        client.close()
    }

    @Test
    fun mapsDetailResponseAndFilesToDomain() = runTest {
        val client = mockClient(
            """
            {
              "data": {
                "id": "cash-2",
                "user_id": "user-2",
                "amount": 42.25,
                "files": [{"id": "file-1", "url": "https://cdn/file.png", "file_type": "image/png"}]
              }
            }
            """.trimIndent(),
        )

        val result = GetCashByIdApiImpl(client).getCashById("cash-2")

        assertEquals("cash-2", result?.id)
        assertEquals(Money.fromDecimal("42.25"), result?.amount)
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
