package com.wealthvault.building_api.model

import com.wealthvault.core.model.FileDataModel
import com.wealthvault.core.model.HasImageUrl
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class BuildingIdResponse(
    @SerialName("status") val status: String? = null,
    @SerialName("data") val data: BuildingIdData? = null,
    @SerialName("error") val error: String? = null
)

@Serializable
data class BuildingIdData(
    @SerialName("id") val id: String? = null,
    @SerialName("user_id") val userId: String? = null,
    @SerialName("type") val type: String? = null,
    @SerialName("name") val name: String? = null,
    @SerialName("area") val area: Double? = null, // 🌟 เปลี่ยนเป็น Double
    @SerialName("amount") val amount: Double? = null, // 🌟 เปลี่ยนเป็น Double
    @SerialName("description") val description: String? = null,
    @SerialName("location") val location: LocationDataById? = null,
    @SerialName("ins") val ins: List<InsDataById>? = emptyList(),
    @SerialName("ref") val referenceIds: List<RefDataById>? = emptyList(), // 🌟 ตาม Postman

    // 🌟 พระเอกของเรา เพิ่มตรงนี้ให้โหลดรูปได้
    @SerialName("files") val files: List<FileDataModel>? = emptyList(),

    @SerialName("created_at") val createdAt: String? = null,
    @SerialName("updated_at") val updatedAt: String? = null
)

@Serializable
data class LocationDataById(
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
data class InsDataById(
    @SerialName("ins_id") val insId: String = "",
    @SerialName("ins_name") val insName: String = ""
)

// 🌟 เพิ่ม FileData สำหรับหน้านี้โดยเฉพาะ
@Serializable
data class FileDataById(
    @SerialName("id") val id: String = "",
    @SerialName("url") override val url: String = "", // 🌟 ใส่ override หน้า val
    @SerialName("file_type") override val fileType: String = ""
) : HasImageUrl // 🌟 ตบเข้า Interface

@Serializable
data class RefDataById(
    @SerialName("ref_id") val refId: String = "",
    @SerialName("ref_name") val refName: String = ""
)
