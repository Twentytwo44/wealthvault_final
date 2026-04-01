package com.wealthvault.land_api.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import com.wealthvault.core.model.HasImageUrl // 🌟 นำเข้า Interface รูปภาพ

@Serializable
data class LandIdResponse(
    @SerialName("status") val status: String? = null,
    @SerialName("data") val data: LandIdData? = null,
    @SerialName("error") val error: String? = null
)

@Serializable
data class LandIdData(
    @SerialName("id") val id: String,
    @SerialName("user_id") val userId: String,
    @SerialName("name") val name: String,
    @SerialName("deed_num") val deedNum: String,
    @SerialName("area") val area: Double,   // 🌟 เปลี่ยนเป็น Double
    @SerialName("amount") val amount: Double, // 🌟 เปลี่ยนเป็น Double
    @SerialName("description") val description: String,

    // 🌟 ใส่ ? (Nullable) เผื่อบางทีหลังบ้านไม่ได้ส่งมาจะได้ไม่แครช
    @SerialName("location") val location: LocationById? = null,

    // 🌟 เติม files เผื่อมีรูปถ่ายโฉนดที่ดิน
    @SerialName("files") val files: List<FileDataLand>? = emptyList(),

    @SerialName("created_at") val createdAt: String? = null,
    @SerialName("updated_at") val updatedAt: String? = null
)

@Serializable
data class LocationById(
    @SerialName("location_id") val locationId: String = "",
    @SerialName("address") val address: String = "",
    @SerialName("sub_district") val subDistrict: String = "",
    @SerialName("district") val district: String = "",
    @SerialName("province") val province: String = "",
    @SerialName("postal_code") val postalCode: String = "",
    @SerialName("created_at") val createdAt: String? = null,
    @SerialName("updated_at") val updatedAt: String? = null
)

// 🌟 สร้าง FileData สำหรับ Land
@Serializable
data class FileDataLand(
    @SerialName("id") val id: String = "",
    @SerialName("url") override val url: String = "", // 🌟 สืบทอด HasImageUrl
    @SerialName("file_type") val fileType: String = ""
) : HasImageUrl