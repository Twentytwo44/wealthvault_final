package com.wealthvault.insurance_api.getinsurancetbyid

import com.wealthvault.config.Config
import com.wealthvault.insurance_api.toDomainData
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get

class GetInsuranceByIdApiImpl(private val client: HttpClient) : GetInsuranceByIdApi {
    override suspend fun getInsuranceById(id: String) = client
        .get("${Config.localhost_android}asset/insurance/$id/")
        .body<com.wealthvault.insurance_api.model.InsuranceIdResponse>()
        .toDomainData()
}
