package com.wealthvault.main

import cafe.adriel.voyager.core.registry.ScreenProvider

sealed class SharedScreen : ScreenProvider {
    object Login : SharedScreen()
    object Main : SharedScreen() // หน้าที่มี BottomBar

    // ตั๋วสำหรับ Tab ต่างๆ
    object DashboardTab : SharedScreen()
    object ProfileTab : SharedScreen()
    object AssetTab: SharedScreen()
    object DebtTab: SharedScreen()
    object SocialTab: SharedScreen()
}
