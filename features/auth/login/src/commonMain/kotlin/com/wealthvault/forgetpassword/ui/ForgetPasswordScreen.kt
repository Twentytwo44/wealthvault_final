package com.wealthvault.forgetpassword.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.wealthvault.core.generated.resources.Res
import com.wealthvault.core.generated.resources.ic_auth_email
import com.wealthvault.core.generated.resources.ic_common_back
import com.wealthvault.core.theme.LightBorder
import com.wealthvault.core.theme.LightMuted
import com.wealthvault.core.theme.LightPrimary
import com.wealthvault.core.theme.LightSurface
import com.wealthvault.core.theme.WvBgGradientEnd
import com.wealthvault.core.theme.WvBgGradientStart
import com.wealthvault.core.theme.WvWaveGradientEnd
import com.wealthvault.core.theme.WvWaveGradientStart
import com.wealthvault.core.utils.getScreenModel
import org.jetbrains.compose.resources.painterResource

class ForgetPasswordScreen : Screen {
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val screenModel = getScreenModel<ForgetPasswordScreenModel>()

        var emailText by remember { mutableStateOf("") }

        // 🌟 ดึง State จาก ScreenModel
        val isLoading by screenModel.isLoading.collectAsState()
        val isOtpSent by screenModel.isOtpSent.collectAsState()
        val errorMessage by screenModel.errorMessage.collectAsState()

        val snackbarHostState = remember { SnackbarHostState() }

        // 🌟 ถ้ายิง API ขอ OTP สำเร็จ ให้ไปหน้าถัดไป
        LaunchedEffect(isOtpSent) {
            if (isOtpSent) {
                screenModel.clearFlags() // เคลียร์ค่ากันเด้งซ้ำ
                navigator.push(ConfirmOTPScreen(emailText))
            }
        }

        // 🌟 ดักจับ Error เพื่อโชว์ Snackbar
        LaunchedEffect(errorMessage) {
            errorMessage?.let { msg ->
                snackbarHostState.showSnackbar(message = msg)
                screenModel.resetState()
            }
        }

        Box(modifier = Modifier.fillMaxSize()) {
            ForgetPasswordContent(
                email = emailText,
                onEmailChange = { emailText = it },
                onBackClick = { navigator.pop() },
                onSendOtpClick = { screenModel.sendOtp(emailText) }
            )

            // 🌟 หน้าจอโหลดดิ้ง
            if (isLoading) {
                Box(
                    modifier = Modifier.fillMaxSize().background(Color.Black.copy(alpha = 0.2f)).clickable(enabled = false) {},
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = LightPrimary)
                }
            }

            SnackbarHost(hostState = snackbarHostState, modifier = Modifier.align(Alignment.BottomCenter))
        }
    }
}

@Composable
fun ForgetPasswordContent(email: String, onEmailChange: (String) -> Unit, onBackClick: () -> Unit, onSendOtpClick: () -> Unit) {
    WavyBackground {
        Column(modifier = Modifier.fillMaxSize().statusBarsPadding().padding(24.dp)) {
            Icon(painter = painterResource(Res.drawable.ic_common_back), contentDescription = "Back", tint = LightPrimary, modifier = Modifier.size(24.dp).clickable { onBackClick() })
            Spacer(modifier = Modifier.height(40.dp))
            Text(text = "ลืมรหัสผ่าน?", color = LightPrimary, style = MaterialTheme.typography.headlineLarge, modifier = Modifier.padding(horizontal = 8.dp))
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = "กรุณากรอกอีเมลที่ลงทะเบียนไว้\nเพื่อรับรหัสยืนยัน (OTP)", color = LightMuted, style = MaterialTheme.typography.bodyMedium, modifier = Modifier.padding(horizontal = 8.dp))
            Spacer(modifier = Modifier.height(48.dp))

            Column(modifier = Modifier.fillMaxWidth()) {
                Text(text = "อีเมล", color = LightPrimary, style = MaterialTheme.typography.titleMedium, modifier = Modifier.padding(bottom = 8.dp, start = 8.dp))
                OutlinedTextField(
                    value = email, onValueChange = onEmailChange, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(percent = 30), singleLine = true,
                    leadingIcon = { Icon(painter = painterResource(Res.drawable.ic_auth_email), contentDescription = "email", tint = LightPrimary, modifier = Modifier.size(30.dp)) },
                    colors = OutlinedTextFieldDefaults.colors(focusedContainerColor = LightSurface, unfocusedContainerColor = LightSurface, focusedBorderColor = LightPrimary, unfocusedBorderColor = LightBorder)
                )
            }
            Spacer(modifier = Modifier.height(40.dp))
            Button(
                onClick = onSendOtpClick, modifier = Modifier.fillMaxWidth().height(50.dp), shape = RoundedCornerShape(percent = 30),
                colors = ButtonDefaults.buttonColors(containerColor = LightPrimary)
            ) {
                Text("ส่งรหัสยืนยัน", style = MaterialTheme.typography.titleMedium, color = LightSurface)
            }
        }
    }
}

// 🌟 ตัวพื้นหลังใช้ร่วมกันทั้ง 3 หน้า ไว้ที่ไฟล์แรกที่เดียวพอครับ
@Composable
fun WavyBackground(
    topWaveBrush: Brush = Brush.verticalGradient(colors = listOf(WvWaveGradientStart, WvWaveGradientEnd)),
    bottomBgBrush: Brush = Brush.verticalGradient(colors = listOf(WvBgGradientStart, WvBgGradientEnd)),
    content: @Composable () -> Unit
) {
    Box(modifier = Modifier.fillMaxSize().background(bottomBgBrush).drawBehind {
        val path = Path().apply {
            moveTo(0f, 0f); lineTo(0f, size.height * 0.25f)
            cubicTo(x1 = size.width * 0.4f, y1 = size.height * 0.10f, x2 = size.width * 0.6f, y2 = size.height * 0.45f, x3 = size.width, y3 = size.height * 0.35f)
            lineTo(size.width, 0f); close()
        }
        drawPath(path = path, brush = topWaveBrush)
    }) { content() }
}