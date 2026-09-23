package com.wealthvault.land_api

import com.wealthvault.core.model.Money
import com.wealthvault.land_api.getland.GetLandApiImpl
import com.wealthvault.land_api.getlandbyid.GetLandByIdApiImpl
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

class LandApiImplTest {
    @Test
    fun mapsLandListToDomainValues() = runTest {
        val client = mockClient(
            """
            {
              "data": [{
                "id": "land-1",
                "user_id": "user-1",
                "name": "Home plot",
                "deed_num": "D-100",
                "area": 12,
                "amount": 1250000,
                "location": {
                  "location_id": "location-1",
                  "address": "1 Main Road",
                  "sub_district": "A",
                  "district": "B",
                  "province": "C",
                  "postal_code": "10100"
                }
              }]
            }
            """.trimIndent(),
        )

        val result = GetLandApiImpl(client).getLand().single()

        assertEquals("land-1", result.id)
        assertEquals(Money.fromDecimal("1250000"), result.amount)
        assertEquals("location-1", result.location?.locationId)
        assertEquals("10100", result.location?.postalCode)
        client.close()
    }

    @Test
    fun mapsLandDetailFilesAndReferencesToDomain() = runTest {
        val client = mockClient(
            """
            {
              "data": {
                "id": "land-2",
                "user_id": "user-2",
                "area": 42.5,
                "amount": 987654.25,
                "files": [{"id": "file-1", "url": "https://cdn/deed.pdf", "file_type": "application/pdf"}],
                "ref": [{"ref_id": "ref-1", "ref_name": "North boundary"}]
              }
            }
            """.trimIndent(),
        )

        val result = GetLandByIdApiImpl(client).getLandById("land-2")

        assertEquals("land-2", result?.id)
        assertEquals(Money.fromDecimal("987654.25"), result?.amount)
        assertEquals("file-1", result?.files?.single()?.id)
        assertEquals("ref-1", result?.ref?.single()?.refId)
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
