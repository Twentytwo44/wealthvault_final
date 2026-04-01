package com.wealthvault.building_api.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class BuildingRequest(
    @SerialName("type") val type: String,
    @SerialName("name") val name: String,
    @SerialName("area") val area: Double, // 🌟 เป็น Double
    @SerialName("amount") val amount: Double, // 🌟 เป็น Double
    @SerialName("description") val description: String,
    @SerialName("location.address") val locationAddress: String,
    @SerialName("location.sub_district") val locationSubDistrict: String,
    @SerialName("location.district") val locationDistrict: String,
    @SerialName("location.province") val locationProvince: String,
    @SerialName("location.postal_code") val locationPostalCode: String,
    @SerialName("ins_ids") val insIds: String,
)

@Serializable
data class BuildingResponse(
    @SerialName("status") val status: String? = null,
    @SerialName("data") val data: BuildingData? = null,
    @SerialName("error") val error: String? = null
)

@Serializable
data class BuildingData(
    @SerialName("id") val id: String,
    @SerialName("user_id") val userId: String,
    @SerialName("type") val type: String,
    @SerialName("name") val name: String,
    @SerialName("area") val area: Double,
    @SerialName("amount") val amount: Double,
    @SerialName("description") val description: String,
    @SerialName("location") val location: LocationData? = null,
    @SerialName("ins") val ins: List<InsData>? = emptyList(),
    @SerialName("reference_ids") val referenceIds: List<String>? = emptyList(),
    @SerialName("files") val files: List<FileData>? = emptyList(),
    @SerialName("created_at") val createdAt: String? = null,
    @SerialName("updated_at") val updatedAt: String? = null
)

@Serializable
data class LocationData(
    @SerialName("location_id") val locationId: String = "",
    @SerialName("address") val address: String = "",
    @SerialName("sub_district") val subDistrict: String = "",
    @SerialName("district") val district: String = "",
    @SerialName("province") val province: String = "",
    @SerialName("postal_code") val postalCode: String = "",
    @SerialName("created_at") val createdAt: String? = null,
    @SerialName("updated_at") val updatedAt: String? = null
)

@Serializable
data class InsData(
    @SerialName("ins_id") val insId: String = "",
    @SerialName("ins_name") val insName: String = ""
)

@Serializable
data class FileData(
    @SerialName("id") val id: String = "",
    @SerialName("url") val url: String = "",
    @SerialName("file_type") val fileType: String = ""
)