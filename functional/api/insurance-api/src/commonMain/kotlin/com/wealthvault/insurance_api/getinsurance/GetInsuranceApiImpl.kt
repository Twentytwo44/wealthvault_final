package com.wealthvault.insurance_api.getinsurance

import com.wealthvault.config.Config
import com.wealthvault.insurance_api.requireDomainData
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get

class GetInsuranceApiImpl(private val client: HttpClient) : GetInsuranceApi {
    override suspend fun getInsurance() = client
        .get("${Config.localhost_android}asset/insurance/")
        .body<com.wealthvault.insurance_api.model.GetInsuranceResponse>()
        .requireDomainData()
}
