package com.example.insurance_api.model

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
    val id: String,

    @SerialName("user_id")
    val userId: String,

    @SerialName("name")
    val name: String,

    @SerialName("policy_number")
    val policyNumber: String,

    @SerialName("type")
    val type: String,

    @SerialName("company_name")
    val companyName: String,

    @SerialName("coverage_period")
    val coveragePeriod: Int,

    @SerialName("coverage_amount")
    val coverageAmount: Int,

    @SerialName("con_date")
    val conDate: String,

    @SerialName("exp_date")
    val expDate: String,

    @SerialName("description")
    val description: String,

    @SerialName("created_at")
    val createdAt: String,

    @SerialName("updated_at")
    val updatedAt: String,


)

