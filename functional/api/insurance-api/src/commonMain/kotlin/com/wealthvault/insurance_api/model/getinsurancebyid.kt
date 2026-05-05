package com.wealthvault.insurance_api.model

import com.wealthvault.core.model.FileDataModel
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class InsuranceIdResponse(
    @SerialName("status") val status: String? = null,
    @SerialName("data") val data: InsuranceIdData? = null,
    @SerialName("error") val error: String? = null
)

@Serializable
data class InsuranceIdData(
    @SerialName("id") val id: String,
    @SerialName("user_id") val userId: String,
    @SerialName("name") val name: String? = null,
    @SerialName("policy_number") val policyNumber: String? = null,
    @SerialName("type") val type: String? = null,
    @SerialName("company_name") val companyName: String? = null,
    @SerialName("coverage_period") val coveragePeriod: Int? = null,
    @SerialName("coverage_amount") val coverageAmount: Double? = null,
    @SerialName("con_date") val conDate: String? = null,
    @SerialName("exp_date") val expDate: String? = null,
    @SerialName("description") val description: String? = null,
    @SerialName("files") val files: List<FileDataModel>? = emptyList(),
    @SerialName("created_at") val createdAt: String? = null,
    @SerialName("updated_at") val updatedAt: String? = null
)

//@Serializable
//data class FileDataInsurance(
//    @SerialName("id") val id: String = "",
//    @SerialName("url") override val url: String = "", // 🌟 สืบทอด HasImageUrl
//    @SerialName("file_type") override val fileType: String = ""
//) : HasImageUrl
