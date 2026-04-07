package com.wealthvault.navigation

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.screen.Screen // 🌟 อย่าลืม Import ตัวนี้นะครับ
import cafe.adriel.voyager.navigator.tab.CurrentTab
import cafe.adriel.voyager.navigator.tab.TabNavigator
import com.wealthvault.core.theme.LightBg
import com.wealthvault.core.theme.WealthVaultTheme
import com.wealthvault.navigation.components.BottomBarItem
import com.wealthvault.navigation.tabs.*

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
@Composable
fun MainScreen() {
    TabNavigator(DashboardTab) { navigator ->
        Scaffold(
            containerColor = LightBg,
            bottomBar = {
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
            // 🌟 แก้ตรงนี้: เราจะใช้แค่ padding.calculateBottomPadding()
            // เพื่อไม่ให้เนื้อหาโดน BottomBar บัง แต่ปล่อยด้านบน (Top) ให้ทะลุไปถึง StatusBar
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