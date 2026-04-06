package com.wealthvault.insurance_api.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable


@Serializable
data class GetInsuranceResponse(
    @SerialName("status")
    val status: String? = null,

    @SerialName("data")
    val data: List<GetInsuranceData>? = null,

    @SerialName("error")
    val error: String? = null
)

@Serializable
data class GetInsuranceData(

    @SerialName("id")
    val id: String? = null,

    @SerialName("user_id")
    val userId: String? = null,

    @SerialName("name")
    val name: String? = null,

    @SerialName("policy_number")
    val policyNumber: String? = null,

    @SerialName("type")
    val type: String? = null,

    @SerialName("company_name")
    val companyName: String? = null,

    @SerialName("coverage_period")
    val coveragePeriod: Int? = null,

    @SerialName("coverage_amount")
    val coverageAmount: Int? = null,

    @SerialName("con_date")
    val conDate: String? = null,

    @SerialName("exp_date")
    val expDate: String? = null,

    @SerialName("description")
    val description: String? = null,

    @SerialName("created_at")
    val createdAt: String? = null,

    @SerialName("updated_at")
    val updatedAt: String? = null,


)

