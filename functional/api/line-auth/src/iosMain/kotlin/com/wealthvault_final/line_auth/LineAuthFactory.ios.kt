package com.wealthvault_final.line_auth

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import com.wealthvault_final.line_auth.model.LineUser
import org.koin.compose.koinInject

@Composable
actual fun rememberLineAuth(
    onSuccess: (LineUser) -> Unit,
    onError: (String) -> Unit
): LineAuth {
    // 1. ดึงโค้ด Swift ที่เราฝากไว้ใน Koin ออกมาใช้
    val swiftLineAuth = koinInject<SwiftLineAuth>()

    // 2. สร้าง LineAuth ตัวจริงส่งกลับไปให้ Compose UI
    return remember(swiftLineAuth, onSuccess, onError) {
        object : LineAuth {
            override fun login() {
                // พอ UI กดปุ่ม login() เราก็สั่งให้ Swift ทำงาน พร้อมแนบ Callback ไปให้
                swiftLineAuth.login(onSuccess, onError)
            }
        }
    }
}
