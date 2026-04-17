package com.wealthvault.core.utils

import androidx.compose.runtime.staticCompositionLocalOf
import cafe.adriel.voyager.navigator.Navigator


//val LocalBottomBarState = compositionLocalOf { mutableStateOf(true) }
//
val LocalRootNavigator = staticCompositionLocalOf<Navigator> {
    error("ไม่พบ Root Navigator! อย่าลืมครอบ App ด้วย Navigator ก่อนใช้งาน")
}
