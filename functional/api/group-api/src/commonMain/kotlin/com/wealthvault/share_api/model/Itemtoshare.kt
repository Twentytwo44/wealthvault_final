package com.wealthvault.share_api.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ItemToShareResponse(
    @SerialName("items")
    val items: List<ItemToShareData>? = null,

    // 🌟 เผื่อ Backend มีการพ่น status หรือ error ออกมาด้วย
    @SerialName("status")
    val status: String? = null,

    @SerialName("error")
    val error: String? = null
)

@Serializable
data class ItemToShareData(
    @SerialName("id")
    val id: String? = null,

    @SerialName("type")
    val type: String? = null,

    @SerialName("name")
    val name: String? = null,

    // 🌟 ใช้ Double เพื่อรองรับทั้งจำนวนเต็มและทศนิยม
    @SerialName("value")
    val value: Double? = null,

    @SerialName("image")
    val image: String? = null,

    // 🌟 เอาไว้เช็คว่าอันไหนติ๊กแชร์ไปแล้วบ้าง
    @SerialName("is_shared")
    val isShared: Boolean? = null
)