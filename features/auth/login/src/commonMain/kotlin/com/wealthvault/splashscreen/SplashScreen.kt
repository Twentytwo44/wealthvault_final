package com.wealthvault.splashscreen

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import cafe.adriel.voyager.core.registry.rememberScreen
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.wealthvault.core.utils.getScreenModel
import com.wealthvault.introduction.ui.IntroScreen
import com.wealthvault.main.SharedScreen

class SplashScreen : Screen {

    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val screenModel = getScreenModel<SplashScreenModel>()

        // 🚨 1. กฎเหล็ก: ต้องดึง State ออกมาไว้ข้างนอก LaunchedEffect เสมอ
        val userData by screenModel.state.collectAsState()

        // 🌟 2. เบิกตั๋วรอไว้

        val mainScreen = rememberScreen(SharedScreen.Main)

        // 🌟 3. สั่งดึงข้อมูลตอนเปิดหน้าจอ (ทำงานแค่ครั้งเดียว)

        // 🌟 4. ดักฟัง State เปลี่ยนแปลง แล้วสั่งเปลี่ยนหน้าจอ
        LaunchedEffect(userData) {
            // เช็กเงื่อนไขข้อมูลตรงนี้ได้เลย (คุณต้องปรับตาม Data ของคุณนะครับ)

            // ⚠️ ข้อควรระวัง: ต้องเช็กด้วยว่ามัน "กำลังโหลดอยู่" หรือ "โหลดเสร็จแล้วแต่ได้ null"
            // สมมติว่าถ้า userData เป็น String หรือ Object และมีค่า
            if (userData.birthday != null) {
                navigator.replaceAll(mainScreen)
            } else {
               navigator.replaceAll(IntroScreen())
            }
        }

        // หน้าตาของจอระหว่างรอเช็กข้อมูล (เอาโลโก้มาใส่แทนวงกลมได้เลย)
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator() // ขึ้นวงกลมโหลดติ้วๆ
        }
    }
}
