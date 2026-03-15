package com.wealthvault.core.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable

@Composable
fun WealthVaultTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        // ถ้าอนาคตมี ColorScheme ก็เอามาใส่ตรงนี้ได้ครับ
        typography = getAppTypography(), // 🌟 ดึงฟอนต์ Prompt มาใช้ทั้งแอป
        content = content
    )
}