package com.wealthvault.data.portfolio.investment.transport

import com.wealthvault.config.Config
import com.wealthvault.data.portfolio.investment.transport.deleteinvestment.DeleteInvestmentApiImpl
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

class DeleteInvestmentApiImplTest {
    @Test
    fun usesTheSharedApiBaseWithoutAnExtraSlash() = runTest {
        var requestedUrl = ""
        val client = HttpClient(MockEngine) {
            install(ContentNegotiation) { json(Json { ignoreUnknownKeys = true }) }
            engine {
                addHandler { request ->
                    requestedUrl = request.url.toString()
                    respond(
                        content = "{\"status\":\"success\"}",
                        headers = headersOf(
                            HttpHeaders.ContentType,
                            ContentType.Application.Json.toString(),
                        ),
                    )
                }
            }
        }

        DeleteInvestmentApiImpl(client).deleteInvestment("investment-1")

        assertEquals("${Config.apiBaseUrl}asset/invest/investment-1/", requestedUrl)
        client.close()
    }
}
