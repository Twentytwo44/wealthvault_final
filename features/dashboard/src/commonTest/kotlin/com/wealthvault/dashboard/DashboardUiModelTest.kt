package com.wealthvault.dashboard

import com.wealthvault.core.model.DashboardData
import com.wealthvault.core.model.DashboardItem
import com.wealthvault.core.model.Money
import com.wealthvault.dashboard.ui.DashboardTab
import com.wealthvault.dashboard.ui.buildDashboardListRows
import com.wealthvault.dashboard.ui.getCategoryGroupName
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class DashboardUiModelTest {
    @Test
    fun mapsAssetRowsOutsideCompositionWithStableFallbacks() {
        val rows = buildDashboardListRows(
            DashboardData(
                assets = listOf(
                    DashboardItem(
                        type = "Investment",
                        name = "",
                        value = Money(125_050),
                        createdAt = "2026-01-02",
                    ),
                    DashboardItem(
                        id = "cash-1",
                        type = "cash",
                        name = "Wallet",
                        value = Money(0),
                    ),
                ),
            ),
            DashboardTab.ASSET,
        )

        assertEquals("Investment::2026-01-02", rows[0].id)
        assertEquals("ไม่ระบุชื่อ", rows[0].title)
        assertEquals("ลงทุน หุ้น กองทุน", rows[0].categoryName)
        assertEquals("มูลค่ารวม", rows[0].amountLabel)
        assertEquals("1,250.5 บาท", rows[0].amountValue)
        assertEquals("cash-1", rows[1].id)
    }

    @Test
    fun mapsLiabilityRowsAndHandlesNullDashboard() {
        assertTrue(buildDashboardListRows(null, DashboardTab.DEBT).isEmpty())

        val rows = buildDashboardListRows(
            DashboardData(
                liabilities = listOf(
                    DashboardItem(
                        type = "home-loan",
                        name = "Mortgage",
                        value = Money(300_000),
                    ),
                ),
            ),
            DashboardTab.DEBT,
        )

        assertEquals("หนี้สิน", rows.single().categoryName)
        assertEquals("ยอดหนี้", rows.single().amountLabel)
        assertEquals("3,000 บาท", rows.single().amountValue)
    }

    @Test
    fun categoryMatchingIsCaseInsensitiveAndHasSafeFallback() {
        assertEquals("บัญชีเงินฝาก", getCategoryGroupName("BANK_ACCOUNT", isAsset = true))
        assertEquals("หนี้สินอื่นๆ", getCategoryGroupName("unknown", isAsset = false))
    }
}
