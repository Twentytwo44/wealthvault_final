package com.wealthvault.navigation.tabs

import androidx.compose.runtime.Composable
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.Navigator
import cafe.adriel.voyager.navigator.currentOrThrow
import cafe.adriel.voyager.navigator.tab.Tab
import cafe.adriel.voyager.navigator.tab.TabOptions
import com.wealthvault.core.generated.resources.Res
import com.wealthvault.core.generated.resources.ic_nav_dashboard
import com.wealthvault.dashboard.ui.DashboardScreen
import org.jetbrains.compose.resources.painterResource

// 🌟 Import หน้า Notification (อย่าลืมเช็ค Package ให้ตรงกับของจริงนะครับ)
import com.wealthvault.notification.ui.NotificationScreen

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
        // 🌟 ดึง Root Navigator ออกมาตรงๆ เพื่อให้หน้าแจ้งเตือนทับ Navbar มิด
        val rootNavigator = LocalNavigator.currentOrThrow.parent ?: LocalNavigator.currentOrThrow

        Navigator(DashboardScreenDestination(rootNavigator))
    }
}

// ==========================================
// 🚀 โซนสร้าง Screen (เส้นทาง)
// ==========================================

class DashboardScreenDestination(private val rootNavigator: Navigator) : Screen {
    @Composable
    override fun Content() {
        DashboardScreen(
            onNotiClick = {
                // กดกระดิ่งปุ๊บ -> สไลด์หน้าแจ้งเตือนขึ้นมา!
                rootNavigator.push(NotificationDestination(rootNavigator))
            }
        )
    }
}

class NotificationDestination(private val rootNavigator: Navigator) : Screen {
    @Composable
    override fun Content() {
        NotificationScreen(
            onBackClick = { rootNavigator.pop() } // กดย้อนกลับ -> ปิดหน้าแจ้งเตือน
        )
    }
}