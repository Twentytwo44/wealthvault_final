package com.wealthvault.`user-api`.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class DashboardDataResponse(
    @SerialName("assets")
    val assets: List<DashboardItem> = emptyList(),

    @SerialName("friend_count")
    val friendCount: Int = 0,

    @SerialName("liabilities")
    val liabilities: List<DashboardItem> = emptyList(),

    @SerialName("net_worth")
    val netWorth: NetWorthData? = null,

    @SerialName("unique_shared_item_count")
    val uniqueSharedItemCount: Int = 0
)

@Serializable
data class DashboardItem(
    @SerialName("id")
    val id: String = "",

    @SerialName("type")
    val type: String = "",

    @SerialName("name")
    val name: String = "",

    // 🌟 ตั้งเป็น Double? เพราะบางอัน (เช่นประกัน) ไม่มี value ส่งมา
    @SerialName("value")
    val value: Double? = null,

    @SerialName("created_at")
    val createdAt: CreatedAtData? = null
)

@Serializable
data class CreatedAtData(
    @SerialName("seconds")
    val seconds: Long = 0,

    @SerialName("nanos")
    val nanos: Long = 0
)

@Serializable
data class NetWorthData(
    @SerialName("count")
    val count: Int = 0,

    @SerialName("total_assets")
    val totalAssets: Double = 0.0,

    @SerialName("total_liabilities")
    val totalLiabilities: Double = 0.0,

    @SerialName("value")
    val value: Double = 0.0
)