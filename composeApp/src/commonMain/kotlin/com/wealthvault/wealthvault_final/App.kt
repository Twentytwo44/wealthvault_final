package com.wealthvault.wealthvault_final

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.tooling.preview.Preview
import cafe.adriel.voyager.navigator.CurrentScreen
import cafe.adriel.voyager.navigator.Navigator
import com.wealthvault.core.theme.WealthVaultTheme
import com.wealthvault.core.utils.LocalRootNavigator
import com.wealthvault.login.ui.LoginScreen

//val LocalRootNavigator = staticCompositionLocalOf<Navigator> {
//    error("ยังไม่ได้ Provide Root Navigator!")
//}
@Composable
@Preview
fun App() {
    MaterialTheme {
        // 🌟 ใส่ปีกกาหลัง Navigator เพื่อดึงตัว navigator ออกมา
        WealthVaultTheme{
        Navigator(LoginScreen()) { navigator ->
            // 🌟 ฝาก navigator ตัวแม่สุดไว้ใน LocalRootNavigator
            CompositionLocalProvider(LocalRootNavigator provides navigator) {
                CurrentScreen() // คำสั่งวาดหน้าจอของ Voyager
            }
            }
        }
    }
}
