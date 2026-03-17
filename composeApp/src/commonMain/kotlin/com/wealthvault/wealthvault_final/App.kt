package com.wealthvault.wealthvault_final

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import cafe.adriel.voyager.navigator.Navigator // 🌟 Import Navigator
import com.wealthvault.navigation.MainAppDestination // 🌟 Import หน้าจอที่เราเพิ่งสร้าง

@Composable
@Preview
fun App() {
    MaterialTheme {
        // 🌟 ครอบ Navigator ใหญ่สุดไว้ที่นี่ และเรียกใช้ MainAppDestination!
        Navigator(MainAppDestination())
    }
}