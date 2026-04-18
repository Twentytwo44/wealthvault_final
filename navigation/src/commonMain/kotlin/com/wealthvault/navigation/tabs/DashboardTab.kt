package com.wealthvault.navigation.tabs

import androidx.compose.runtime.Composable
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.Navigator
import cafe.adriel.voyager.navigator.currentOrThrow
import cafe.adriel.voyager.navigator.tab.LocalTabNavigator
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
        // ดึง Navigator ทั้ง 2 ตัวมาเตรียมไว้
        val rootNavigator = LocalNavigator.currentOrThrow.parent ?: LocalNavigator.currentOrThrow
        val tabNavigator = LocalTabNavigator.current

        Navigator(
            DashboardScreen(
                onNotiClick = {
                    // 🌟 เอาคำสั่งเด้งหน้า Noti เดิมกลับมาใส่ตรงนี้ครับ!
                    rootNavigator.push(NotificationDestination(rootNavigator))
                },
            )
        )
    }
}

// ==========================================
// 🚀 โซนสร้าง Screen (เส้นทาง)
// ==========================================

// 🗑️ ลบ DashboardScreenDestination ทิ้งไปได้เลยครับ เพราะ DashboardScreen ทำหน้าที่แทนแล้ว

class NotificationDestination(private val rootNavigator: Navigator) : Screen {
    @Composable
    override fun Content() {
        NotificationScreen(
            onBackClick = { rootNavigator.pop() } // กดย้อนกลับ -> ปิดหน้าแจ้งเตือน
        )
    }
}