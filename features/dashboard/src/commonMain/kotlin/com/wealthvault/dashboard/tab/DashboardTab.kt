package com.wealthvault.dashboard.tab

import androidx.compose.runtime.Composable
import cafe.adriel.voyager.core.registry.screenModule
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.Navigator
import cafe.adriel.voyager.navigator.currentOrThrow
import cafe.adriel.voyager.navigator.tab.LocalTabNavigator
import cafe.adriel.voyager.navigator.tab.Tab
import cafe.adriel.voyager.navigator.tab.TabOptions
import com.wealthvault.core.generated.resources.Res
import com.wealthvault.core.generated.resources.ic_nav_dashboard
import com.wealthvault.dashboard.ui.DashboardScreen
import com.wealthvault.main.SharedScreen
import org.jetbrains.compose.resources.painterResource

object DashboardTab : Tab {
    override val options: TabOptions
        @Composable
        get() = TabOptions(
            index = 0u,
            title = "หน้าหลัก",
            icon = painterResource(Res.drawable.ic_nav_dashboard)
        )

    @Composable
    override fun Content() {
        // ดึง Navigator ทั้ง 2 ตัวมาเตรียมไว้
        val rootNavigator = LocalNavigator.currentOrThrow.parent ?: LocalNavigator.currentOrThrow
        val tabNavigator = LocalTabNavigator.current

        Navigator(
            DashboardScreen(

            )
        )
    }
}

val dashboardTabModule = screenModule {
    register<SharedScreen.DashboardTab> { DashboardTab }
}
