package com.wealthvault.building_api

import com.wealthvault.building_api.getbuilding.GetBuildingApiImpl
import com.wealthvault.building_api.getbuildingbyid.GetBuildingByIdApiImpl
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

class BuildingApiImplTest {
    @Test
    fun mapsBuildingListToDomainValues() = runTest {
        val client = mockClient(
            """
            {
              "data": [{
                "id": "building-1",
                "user_id": "user-1",
                "type": "HOUSE",
                "name": "Home",
                "area": 120.5,
                "amount": 2500000.75,
                "location": {
                  "location_id": "location-1",
                  "address": "1 Main Road",
                  "sub_district": "A",
                  "district": "B",
                  "province": "C",
                  "postal_code": "10100"
                },
                "ins": [{"ins_id": "ins-1", "ins_name": "Home cover"}],
                "reference_ids": ["land-1"],
                "files": [{"id": "file-1", "url": "https://cdn/home.jpg", "file_type": "image/jpeg"}]
              }]
            }
            """.trimIndent(),
        )

        val result = GetBuildingApiImpl(client).getBuilding().single()

        assertEquals("building-1", result.id)
        assertEquals(Money.fromDecimal("2500000.75"), result.amount)
        assertEquals("location-1", result.location?.locationId)
        assertEquals("ins-1", result.ins?.single()?.insId)
        assertEquals("land-1", result.referenceIds?.single())
        assertEquals("file-1", result.files?.single()?.id)
        client.close()
    }

    @Test
    fun mapsBuildingDetailToDomainValues() = runTest {
        val client = mockClient(
            """
            {
              "data": {
                "id": "building-2",
                "user_id": "user-2",
                "area": 80.25,
                "amount": 987654.25,
                "ins": [{"ins_id": "ins-2", "ins_name": "Fire cover"}],
                "ref": [{"ref_id": "ref-1", "ref_name": "Plot A"}],
                "files": [{"id": "file-2", "url": "https://cdn/building.pdf", "file_type": "application/pdf"}]
              }
            }
            """.trimIndent(),
        )

        val result = GetBuildingByIdApiImpl(client).getBuildingById("building-2")

        assertEquals("building-2", result?.id)
        assertEquals(Money.fromDecimal("987654.25"), result?.amount)
        assertEquals("ins-2", result?.ins?.single()?.insId)
        assertEquals("ref-1", result?.referenceIds?.single()?.refId)
        assertEquals("file-2", result?.files?.single()?.id)
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
