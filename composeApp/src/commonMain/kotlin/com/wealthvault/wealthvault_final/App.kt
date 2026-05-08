package com.wealthvault.wealthvault_final

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.tooling.preview.Preview
import cafe.adriel.voyager.navigator.CurrentScreen
import cafe.adriel.voyager.navigator.Navigator
import com.wealthvault.core.theme.WealthVaultTheme
import com.wealthvault.core.utils.LocalRootNavigator
import com.wealthvault.data_store.TokenStore
import com.wealthvault.login.ui.LoginScreen
import com.wealthvault.splashscreen.SplashScreen
import org.koin.compose.koinInject

//val LocalRootNavigator = staticCompositionLocalOf<Navigator> {
//    error("ยังไม่ได้ Provide Root Navigator!")
//}
@Composable
@Preview
fun App() {
    MaterialTheme {
        WealthVaultTheme {
            // 🚩 เปลี่ยนจาก LoginScreen() เป็น SplashScreen()
            Navigator(SplashScreen()) { navigator ->
                CompositionLocalProvider(LocalRootNavigator provides navigator) {

                    // 🌟 เพิ่มจุดดักฟัง "Global Logout" ตรงนี้
                    val tokenStore = koinInject<TokenStore>()
                    val accessToken by tokenStore.accessToken.collectAsState(initial = "loading")

                    LaunchedEffect(accessToken) {
                        // ถ้าแอปโหลดเสร็จแล้ว (ไม่ใช่ "loading") และ Token หายไป
                        if (accessToken == null) {
                            navigator.replaceAll(LoginScreen())
                        }
                    }

                    CurrentScreen()
                }
            }
        }
    }
}
