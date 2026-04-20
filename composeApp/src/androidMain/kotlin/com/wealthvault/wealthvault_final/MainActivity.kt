package com.wealthvault.wealthvault_final

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import cafe.adriel.voyager.core.registry.ScreenRegistry
import com.wealthvault.login.ui.loginScreenModule
import com.wealthvault.navigation.mainScreenModule
import com.wealthvault.profile.ui.profileScreenModule

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
        ScreenRegistry {
            loginScreenModule()
            mainScreenModule()
            profileScreenModule()


            // ... (ใส่ Module อื่นๆ ให้ครบ)
        }

        setContent {
            // 🌟 2. เรียกฟังก์ชัน App() ตามเดิมครับ
            App()
        }
    }
}

@Preview
@Composable
fun AppAndroidPreview() {
    App()
}
