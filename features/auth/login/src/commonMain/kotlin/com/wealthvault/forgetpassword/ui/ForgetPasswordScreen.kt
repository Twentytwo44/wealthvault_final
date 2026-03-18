package com.wealthvault.forgetpassword.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow

// Import สีจาก Theme ของคุณ Champ
import com.wealthvault.core.theme.LightPrimary
import com.wealthvault.core.theme.LightSurface
import com.wealthvault.core.theme.LightBorder
import com.wealthvault.core.theme.LightMuted
import com.wealthvault.core.theme.WvBgGradientEnd
import com.wealthvault.core.theme.WvBgGradientStart
import com.wealthvault.core.theme.WvWaveGradientEnd
import com.wealthvault.core.theme.WvWaveGradientStart

// Import รูปภาพและไอคอน
import org.jetbrains.compose.resources.painterResource
import com.wealthvault.core.generated.resources.Res
import com.wealthvault.core.generated.resources.ic_common_back
import com.wealthvault.core.generated.resources.ic_auth_email

// ==========================================
// 🌟 1. สร้างคลาส Screen สำหรับให้ Voyager เรียกใช้
// ==========================================
class ForgetPasswordScreen : Screen {
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow

        // 🌟 จำสถานะของการกรอกอีเมลไว้ชั่วคราว (ถ้าอนาคตมี ViewModel ค่อยย้ายไปใส่ใน ViewModel ครับ)
        var emailText by remember { mutableStateOf("") }

        ForgetPasswordContent(
            email = emailText,
            onEmailChange = { emailText = it },
            onBackClick = {
                // กดปุ่มย้อนกลับ -> ให้ดึงหน้าตัวเองออก เพื่อกลับไปหน้า Login
                navigator.pop()
            },
            onSendOtpClick = {
                // TODO: ใส่โค้ดส่งคำขอ OTP ไปที่เซิร์ฟเวอร์
                // เมื่อส่งสำเร็จ อาจจะ navigator.push(ResetPasswordScreen()) ต่อไป
            }
        )
    }
}

// ==========================================
// 🌟 2. ส่วนของ UI (Content)
// ==========================================
@Composable
fun ForgetPasswordContent(
    email: String,
    onEmailChange: (String) -> Unit,
    onBackClick: () -> Unit,
    onSendOtpClick: () -> Unit
) {
    WavyBackground {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding() // 🌟 เติมกันไว้ไม่ให้ตัวหนังสือทะลุรอยบากกล้อง
                .padding(24.dp)
        ) {
            // --- ปุ่มย้อนกลับ ---
            Icon(
                painter = painterResource(Res.drawable.ic_common_back),
                contentDescription = "Back",
                tint = LightPrimary,
                modifier = Modifier
                    .size(24.dp)
                    .clickable { onBackClick() }
            )

            Spacer(modifier = Modifier.height(40.dp))

            // --- หัวข้อ ---
            Text(
                text = "ลืมรหัสผ่าน?",
                color = LightPrimary,
                style = MaterialTheme.typography.headlineLarge,
                modifier = Modifier.padding(horizontal = 8.dp)
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "กรุณากรอกอีเมลที่ลงทะเบียนไว้\nเพื่อรับรหัสยืนยัน (OTP)",
                color = LightMuted,
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(horizontal = 8.dp)
            )

            Spacer(modifier = Modifier.height(48.dp))

            // --- ช่องกรอกอีเมล ---
            Column(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = "อีเมล",
                    color = LightPrimary,
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.padding(bottom = 8.dp, start = 8.dp)
                )
                OutlinedTextField(
                    value = email,
                    onValueChange = onEmailChange,
                    modifier = Modifier
                        .fillMaxWidth(),
                    shape = RoundedCornerShape(percent = 30),
                    singleLine = true,
                    leadingIcon = {
                        Icon(
                            painter = painterResource(Res.drawable.ic_auth_email),
                            contentDescription = "email",
                            tint = LightPrimary,
                            modifier = Modifier.size(30.dp)
                        )
                    },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = LightSurface,
                        unfocusedContainerColor = LightSurface,
                        focusedBorderColor = LightPrimary,
                        unfocusedBorderColor = LightBorder,
                    )
                )
            }

            Spacer(modifier = Modifier.height(40.dp))

            // --- ปุ่มส่งรหัส ---
            Button(
                onClick = onSendOtpClick,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                shape = RoundedCornerShape(percent = 30),
                colors = ButtonDefaults.buttonColors(containerColor = LightPrimary)
            ) {
                Text("ส่งรหัสยืนยัน", style = MaterialTheme.typography.titleMedium, color = LightSurface)
            }
        }
    }
}

// ==========================================
// 🌟 3. พื้นหลังเส้นคลื่น (ก๊อปปี้มาจากหน้า Login)
// ==========================================
@Composable
fun WavyBackground(
    topWaveBrush: Brush = Brush.verticalGradient(
        colors = listOf(WvWaveGradientStart, WvWaveGradientEnd)
    ),
    bottomBgBrush: Brush = Brush.verticalGradient(
        colors = listOf(WvBgGradientStart, WvBgGradientEnd)
    ),
    content: @Composable () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(bottomBgBrush)
            .drawBehind {
                val path = Path().apply {
                    moveTo(0f, 0f)
                    lineTo(0f, size.height * 0.25f)
                    cubicTo(
                        x1 = size.width * 0.4f, y1 = size.height * 0.10f,
                        x2 = size.width * 0.6f, y2 = size.height * 0.45f,
                        x3 = size.width, y3 = size.height * 0.35f
                    )
                    lineTo(size.width, 0f)
                    close()
                }
                drawPath(path = path, brush = topWaveBrush)
            }
    ) {
        content()
    }
}