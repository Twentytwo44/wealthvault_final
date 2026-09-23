package com.wealthvault.core.model

import kotlinx.serialization.Serializable

/** Domain-facing dashboard models; API DTOs are mapped before crossing the data boundary. */
@Serializable
data class DashboardData(
    val assets: List<DashboardItem> = emptyList(),
    val friendCount: Int = 0,
    val liabilities: List<DashboardItem> = emptyList(),
    val netWorth: DashboardNetWorth? = null,
    val uniqueSharedItemCount: Int = 0,
)

@Serializable
data class DashboardItem(
    val id: String = "",
    val type: String = "",
    val name: String = "",
    val value: Money? = null,
    val createdAt: String? = null,
)

@Serializable
data class DashboardNetWorth(
    val count: Int = 0,
    val totalAssets: Money = Money(0),
    val totalLiabilities: Money = Money(0),
    val value: Money = Money(0),
)
