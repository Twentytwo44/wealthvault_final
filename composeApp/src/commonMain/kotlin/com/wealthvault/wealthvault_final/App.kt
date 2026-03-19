package com.wealthvault.wealthvault_final

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import cafe.adriel.voyager.navigator.Navigator
import com.wealthvault.profile.ui.ProfileScreen

@Composable
@Preview
fun App() {

    MaterialTheme {
        // 🌟 ครอบ Navigator ใหญ่สุดไว้ที่นี่ และเรียกใช้ MainAppDestination!
//        Navigator(MainAppDestination())
//        Navigator(LoginScreen(navigateToScreen = MainAppDestination()))
//        Navigator(LoginScreen(navigateToScreen = MainAppDestination()))
        Navigator(ProfileScreen())
    }
}
