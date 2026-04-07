package com.wealthvault.building_api.deletebuilding

import com.wealthvault.config.Config
import com.wealthvault.core.model.DeleteBaseResponse
import de.jensklingenberg.ktorfit.Ktorfit
import io.ktor.client.call.body
import io.ktor.client.request.delete


class DeleteBuildingApiImpl(private val ktorfit: Ktorfit) : DeleteBuildingApi {

    override suspend fun deleteBuilding(id: String): DeleteBaseResponse {
        val client = ktorfit.httpClient

        return client.delete("${Config.localhost_android}asset/building/$id/") {
            // ปกติ DELETE ไม่ต้องส่ง Body แต่ต้องแนบ Token
            // ซึ่ง HttpClient ตัวนี้มี Auth Plugin ที่เราเซ็ตไว้ใน ApiModule แล้ว
        }.body()
    }
}
