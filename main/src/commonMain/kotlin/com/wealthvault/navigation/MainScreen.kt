package com.wealthvault.navigation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding // 🌟 1. อย่าลืม Import ตัวนี้เพิ่มนะครับ
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.registry.rememberScreen
import cafe.adriel.voyager.core.registry.screenModule
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.tab.CurrentTab
import cafe.adriel.voyager.navigator.tab.Tab
import cafe.adriel.voyager.navigator.tab.TabNavigator
import com.wealthvault.core.theme.LightBg
import com.wealthvault.core.theme.LightSoftWhite
import com.wealthvault.core.theme.WealthVaultTheme
import com.wealthvault.main.SharedScreen
import com.wealthvault.navigation.components.BottomBarItem

class MainScreen : Screen {

    @Composable
    override fun Content() {
        // 🌟 สร้าง TabNavigator ไว้ข้างใน Screen นี้
        val dashboardTab = rememberScreen(SharedScreen.DashboardTab) as Tab
        val assetTab = rememberScreen(SharedScreen.AssetTab) as Tab
        val profileTab = rememberScreen(SharedScreen.ProfileTab) as Tab
        val debtTab = rememberScreen(SharedScreen.DebtTab) as Tab
        val socialTab = rememberScreen(SharedScreen.SocialTab) as Tab

        TabNavigator(dashboardTab) { navigator ->
            Scaffold(
                containerColor = LightBg,
                bottomBar = {
                    Surface(
                        shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp),
                        color = LightSoftWhite,
                        shadowElevation = 8.dp,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .navigationBarsPadding() // 🌟 2. ใส่คำสั่งนี้เพื่อดันเนื้อหาหนีแถบ Navigation ของระบบ
                                .padding(vertical = 12.dp, horizontal = 8.dp),
                            horizontalArrangement = Arrangement.SpaceAround,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            BottomBarItem(debtTab, navigator)
                            BottomBarItem(assetTab, navigator)
                            BottomBarItem(dashboardTab, navigator)
                            BottomBarItem(socialTab, navigator)
                            BottomBarItem(profileTab, navigator)
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