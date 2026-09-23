package com.wealthvault.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.wealthvault.core.model.DashboardData
import com.wealthvault.core.model.DashboardItem
import com.wealthvault.core.model.DashboardNetWorth
import com.wealthvault.core.model.Money
import com.wealthvault.dashboard.ui.DashboardContent
import com.wealthvault.dashboard.ui.DashboardTab

/**
 * Deterministic, production-like dashboard content for frame/jank tests.
 *
 * This activity exists only in the benchmark variant. Keeping the data local
 * makes the scroll measurement independent from authentication, network
 * availability, and backend data shape while still exercising the real
 * dashboard composables and lazy list keys.
 */
class BenchmarkDashboardActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            DashboardContent(
                onNotiClick = {},
                onAddClick = {},
                dashboardState = benchmarkDashboardData,
                isLoading = false,
                selectedTab = DashboardTab.ASSET,
                onTabChange = {},
                hasUnreadNoti = false,
            )
        }
    }

    private companion object {
        val benchmarkDashboardData = DashboardData(
            assets = List(240) { index ->
                DashboardItem(
                    id = "benchmark-asset-$index",
                    type = "cash",
                    name = "Benchmark asset $index",
                    value = Money(minorUnits = 100_000L + index),
                    createdAt = "2026-01-01",
                )
            },
            netWorth = DashboardNetWorth(
                count = 240,
                totalAssets = Money(minorUnits = 24_000_000L),
                totalLiabilities = Money(0),
                value = Money(minorUnits = 24_000_000L),
            ),
        )
    }
}
