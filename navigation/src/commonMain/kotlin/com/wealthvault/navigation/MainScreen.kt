package com.wealthvault.navigation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.registry.screenModule
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.tab.CurrentTab
import cafe.adriel.voyager.navigator.tab.TabNavigator
import com.wealthvault.core.theme.LightBg
import com.wealthvault.core.theme.WealthVaultTheme
import com.wealthvault.navigation.components.BottomBarItem
import com.wealthvault.navigation.tabs.AssetTab
import com.wealthvault.navigation.tabs.DashboardTab
import com.wealthvault.navigation.tabs.DebtTab
import com.wealthvault.navigation.tabs.ProfileTab
import com.wealthvault.navigation.tabs.SocialTab

// ==========================================
// 🌟 1. สร้างคลาสหน้าจอหลัก เพื่อให้ Voyager ตัวนอกสุดเรียกใช้ได้
// ==========================================
class MainAppDestination : Screen {
    @Composable
    override fun Content() {
        // เรียกใช้ฟังก์ชัน MainScreen ที่มี Navbar ของคุณ Champ
        MainScreen()
    }
}

// ==========================================
// 🌟 2. โค้ด UI หน้าหลัก (ของเดิมของคุณ Champ เลยครับ)
// ==========================================
//@Composable
//fun MainScreen() {
//    // 🌟 2. สร้าง State สำหรับเก็บสถานะว่า "ตอนนี้เปิดหรือปิดอยู่" (เริ่มต้นเป็น true คือเปิด)
//    val bottomBarState = remember { mutableStateOf(true) }
//
//    // 🌟 3. ครอบระบบ Tab ด้วย Provider เพื่อแชร์ State นี้ลงไปให้หน้าย่อยต่างๆ
//    CompositionLocalProvider(LocalBottomBarState provides bottomBarState) {
//        TabNavigator(DashboardTab) { navigator ->
//            Scaffold(
//                containerColor = LightBg,
//                bottomBar = {
//                    // 🌟 4. เอา if มาครอบ ถ้า state เป็น true ค่อยวาด Menu Bar
//                    if (bottomBarState.value) {
//                        Surface(
//                            shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp),
//                            color = Color.White,
//                            shadowElevation = 8.dp,
//                            modifier = Modifier.fillMaxWidth()
//                        ) {
//                            Row(
//                                modifier = Modifier
//                                    .fillMaxWidth()
//                                    .padding(vertical = 12.dp, horizontal = 8.dp),
//                                horizontalArrangement = Arrangement.SpaceAround,
//                                verticalAlignment = Alignment.CenterVertically
//                            ) {
//                                BottomBarItem(DebtTab, navigator)
//                                BottomBarItem(AssetTab, navigator)
//                                BottomBarItem(DashboardTab, navigator)
//                                BottomBarItem(SocialTab, navigator)
//                                BottomBarItem(ProfileTab, navigator)
//                            }
//                        }
//                    }
//                }
//            ) { padding ->
//                // 💡 จุดเด่นของท่านี้: ถ้า bottomBarState เป็น false ตัว Scaffold จะรู้ทันที
//                // และปรับค่า padding.calculateBottomPadding() ให้เป็น 0 อัตโนมัติ ทำให้จอเต็ม!
//                Box(
//                    Modifier
//                        .fillMaxSize()
//                        .padding(bottom = padding.calculateBottomPadding())
//                ) {
//                    WealthVaultTheme {
//                        CurrentTab()
//                    }
//                }
//            }
//        }
//    }
//}


class MainScreen : Screen {

    @Composable
    override fun Content() {
        // 🌟 สร้าง TabNavigator ไว้ข้างใน Screen นี้
        TabNavigator(DashboardTab) { navigator ->
            Scaffold(
                containerColor = LightBg,
                bottomBar = {
                    // วาด BottomBar ตามปกติ ไม่ต้องมี if เช็คสถานะแล้ว
                    Surface(
                        shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp),
                        color = Color.White,
                        shadowElevation = 8.dp,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 12.dp, horizontal = 8.dp),
                            horizontalArrangement = Arrangement.SpaceAround,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            BottomBarItem(DebtTab, navigator)
                            BottomBarItem(AssetTab, navigator)
                            BottomBarItem(DashboardTab, navigator)
                            BottomBarItem(SocialTab, navigator)
                            BottomBarItem(ProfileTab, navigator)
                        }
                    }
                }
            ) { padding ->
                Box(
                    Modifier
                        .fillMaxSize()
                        .padding(bottom = padding.calculateBottomPadding())
                ) {
                    WealthVaultTheme {
                        CurrentTab()
                    }
                }
            }
        }
    }
}


val mainScreenModule = screenModule {
    register<SharedScreen.Main> {
        MainScreen()
    }
}
