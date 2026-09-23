package com.wealthvault.dashboard.ui

import androidx.compose.runtime.Immutable
import com.wealthvault.core.model.DashboardData
import com.wealthvault.core.model.Money
import com.wealthvault.core.utils.formatAmount

/**
 * Immutable row model consumed by the dashboard list. Keeping the mapping
 * outside the composable makes the presentation contract deterministic and
 * unit-testable while preserving the existing labels and formatting.
 */
@Immutable
internal data class DashboardListRow(
    val id: String,
    val title: String,
    val categoryName: String,
    val amountLabel: String,
    val amountValue: String,
)

internal fun buildDashboardListRows(
    dashboardState: DashboardData?,
    selectedTab: DashboardTab,
): List<DashboardListRow> {
    if (dashboardState == null) return emptyList()

    val isAsset = selectedTab == DashboardTab.ASSET
    val currentList = if (isAsset) dashboardState.assets else dashboardState.liabilities
    return currentList.map { item ->
        val categoryName = getCategoryGroupName(item.type, isAsset)
        DashboardListRow(
            id = item.id.ifBlank { "${item.type}:${item.name}:${item.createdAt}" },
            title = item.name.ifEmpty { "ไม่ระบุชื่อ" },
            categoryName = categoryName,
            amountLabel = amountLabelFor(categoryName),
            amountValue = "${formatAmount(item.value ?: Money(0))} บาท",
        )
    }
}

private fun amountLabelFor(categoryName: String): String = when (categoryName) {
    "บัญชีเงินฝาก" -> "ยอดเงิน"
    "เงินสด ทองคำ" -> "มูลค่า"
    "ลงทุน หุ้น กองทุน" -> "มูลค่ารวม"
    "ประกัน" -> "วงเงินคุ้มครอง"
    "บ้าน ตึก อาคาร", "ที่ดิน" -> "มูลค่าประเมิน"
    "หนี้สิน", "รายจ่ายระยะยาว" -> "ยอดหนี้"
    else -> "มูลค่า"
}

fun getCategoryGroupName(type: String, isAsset: Boolean): String {
    val normalizedType = type.lowercase()
    return if (isAsset) {
        when {
            normalizedType.contains("account") -> "บัญชีเงินฝาก"
            normalizedType.contains("cash") -> "เงินสด ทองคำ"
            normalizedType.contains("investment") -> "ลงทุน หุ้น กองทุน"
            normalizedType.contains("insurance") -> "ประกัน"
            normalizedType.contains("building") -> "บ้าน ตึก อาคาร"
            normalizedType.contains("land") -> "ที่ดิน"
            else -> "ทรัพย์สินอื่นๆ"
        }
    } else {
        when {
            normalizedType.contains("liability") ||
                normalizedType.contains("loan") ||
                normalizedType.contains("expense") -> "หนี้สิน"
            else -> "หนี้สินอื่นๆ"
        }
    }
}
