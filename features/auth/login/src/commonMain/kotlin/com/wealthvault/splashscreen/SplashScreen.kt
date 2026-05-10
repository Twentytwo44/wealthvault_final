package com.wealthvault.splashscreen

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.wealthvault.core.utils.getScreenModel
import com.wealthvault.login.ui.LoginScreen
import com.wealthvault.navigation.MainScreen

// 🌟 Import Res และภาพ โลโก้
import com.wealthvault.core.generated.resources.Res
import com.wealthvault.core.generated.resources.wealthvault_logo
import org.jetbrains.compose.resources.painterResource

class SplashScreen : Screen {
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val screenModel = getScreenModel<SplashScreenModel>()
        val state by screenModel.state.collectAsState()

        // 🌟 1. สร้างตัวแปรสำหรับจัดการความโปร่งใส (Alpha) เริ่มต้นที่ 0f (มองไม่เห็น)
        val alphaAnimation = remember { Animatable(0f) }

        // 🌟 2. สั่งให้เล่นแอนิเมชันทันทีที่หน้านี้ถูกเปิดขึ้นมา
        LaunchedEffect(Unit) {
            alphaAnimation.animateTo(
                targetValue = 1f, // ค่อยๆ ชัดขึ้นจนเต็ม 100%
                animationSpec = tween(
                    durationMillis = 1000, // ใช้เวลาเฟด 1 วินาที (ปรับช้าเร็วได้ที่นี่)
                    easing = FastOutSlowInEasing // จังหวะเฟดแบบนุ่มนวล
                )
            )
        }

        LaunchedEffect(state) {
            when (state) {
                is SplashState.GoToLogin -> navigator.replaceAll(LoginScreen())
                is SplashState.GoToMain -> navigator.replaceAll(MainScreen())
                else -> {} // Loading
            }
        }

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFFFFF9F5)),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .alpha(alphaAnimation.value) // 🌟 3. ผูกค่าแอนิเมชันเข้ากับความโปร่งใสของทั้ง Column
            ) {
                // 1. รูปโลโก้
                Image(
                    painter = painterResource(Res.drawable.wealthvault_logo),
                    contentDescription = "App Logo",
                    modifier = Modifier.size(160.dp)
                )

                Spacer(modifier = Modifier.height(16.dp))

                // 2. ชื่อแอป
                Text(
                    text = "Wealth & Vault",
                    style = MaterialTheme.typography.headlineSmall.copy(
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 2.sp
                    ),
                    color = Color(0xFFC27A5A)
                )

                Spacer(modifier = Modifier.height(48.dp))

                // 3. เส้นโหลด
                LinearProgressIndicator(
                    color = Color(0xFFC27A5A),
                    trackColor = Color(0xFFC27A5A).copy(alpha = 0.2f),
                    strokeCap = StrokeCap.Round,
                    modifier = Modifier
                        .width(120.dp)
                        .height(4.dp)
                )
            }
        }
    }
}