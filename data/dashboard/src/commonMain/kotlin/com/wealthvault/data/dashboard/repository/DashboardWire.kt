package com.wealthvault.data.dashboard.repository

import com.wealthvault.core.model.DashboardData
import com.wealthvault.core.model.DashboardItem
import com.wealthvault.core.model.DashboardNetWorth
import com.wealthvault.core.model.Money
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/** Transport-only dashboard payload. It must not cross the data boundary. */
@Serializable
internal data class DashboardWireResponse(
    @SerialName("assets") val assets: List<DashboardWireItem> = emptyList(),
    @SerialName("friend_count") val friendCount: Int = 0,
    @SerialName("liabilities") val liabilities: List<DashboardWireItem> = emptyList(),
    @SerialName("net_worth") val netWorth: DashboardWireNetWorth? = null,
    @SerialName("unique_shared_item_count") val uniqueSharedItemCount: Int = 0,
)

@Serializable
internal data class DashboardWireItem(
    @SerialName("id") val id: String = "",
    @SerialName("type") val type: String = "",
    @SerialName("name") val name: String = "",
    @SerialName("amount") val amount: Double? = null,
    @SerialName("created_at") val createdAt: String? = null,
)

@Serializable
internal data class DashboardWireNetWorth(
    @SerialName("count") val count: Int = 0,
    @SerialName("total_assets") val totalAssets: Double = 0.0,
    @SerialName("total_liabilities") val totalLiabilities: Double = 0.0,
    @SerialName("value") val value: Double = 0.0,
)

internal fun DashboardWireResponse.toDomain() = DashboardData(
    assets = assets.map(DashboardWireItem::toDomain),
    friendCount = friendCount,
    liabilities = liabilities.map(DashboardWireItem::toDomain),
    netWorth = netWorth?.let { value ->
        DashboardNetWorth(
            count = value.count,
            totalAssets = value.totalAssets.toMoney(),
            totalLiabilities = value.totalLiabilities.toMoney(),
            value = value.value.toMoney(),
        )
    },
    uniqueSharedItemCount = uniqueSharedItemCount,
)

private fun DashboardWireItem.toDomain() = DashboardItem(
    id = id,
    type = type,
    name = name,
    value = amount?.let(Money::fromDouble),
    createdAt = createdAt,
)

private fun Double.toMoney() = Money.fromDouble(this) ?: Money(0)
