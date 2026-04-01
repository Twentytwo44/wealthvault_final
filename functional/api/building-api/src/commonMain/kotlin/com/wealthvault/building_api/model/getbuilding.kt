package com.wealthvault.building_api.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class GetBuildingResponse(
    @SerialName("status") val status: String? = null,
    @SerialName("data") val data: List<GetBuildingData>? = null,
    @SerialName("error") val error: String? = null
)

@Serializable
data class GetBuildingData(
    @SerialName("id") val id: String,
    @SerialName("user_id") val userId: String,
    @SerialName("type") val type: String,
    @SerialName("name") val name: String,
    @SerialName("area") val area: Double, // 🌟 เปลี่ยนเป็น Double
    @SerialName("amount") val amount: Double, // 🌟 เปลี่ยนเป็น Double
    @SerialName("description") val description: String,
    @SerialName("location") val location: LocationDataAll? = null,
    @SerialName("ins") val ins: List<InsDataAll>? = emptyList(),
    @SerialName("reference_ids") val referenceIds: List<String>? = emptyList(), // 🌟 เผื่อมี
    @SerialName("files") val files: List<FileDataAll>? = emptyList(), // 🌟 เผื่อมีรูปโชว์หน้ารวม
    @SerialName("created_at") val createdAt: String? = null,
    @SerialName("updated_at") val updatedAt: String? = null
)

@Serializable
data class LocationDataAll(
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
data class InsDataAll(
    @SerialName("ins_id") val insId: String = "",
    @SerialName("ins_name") val insName: String = ""
)

// 🌟 เพิ่ม FileData ไว้สำหรับหน้านี้โดยเฉพาะ
@Serializable
data class FileDataAll(
    @SerialName("id") val id: String = "",
    @SerialName("url") val url: String = "",
    @SerialName("file_type") val fileType: String = ""
)