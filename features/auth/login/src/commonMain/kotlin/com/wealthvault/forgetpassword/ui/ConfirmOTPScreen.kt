package com.wealthvault.forgetpassword.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalTextStyle
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.wealthvault.core.generated.resources.Res
import com.wealthvault.core.generated.resources.ic_common_back
import com.wealthvault.core.theme.LightBorder
import com.wealthvault.core.theme.LightMuted
import com.wealthvault.core.theme.LightPrimary
import com.wealthvault.core.theme.LightSurface
import com.wealthvault.core.utils.getScreenModel
import org.jetbrains.compose.resources.painterResource

// 🌟 สร้าง Screen ครอบรับค่า Email มาจากหน้าแรก
class ConfirmOTPScreen(private val email: String) : Screen {
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val screenModel = getScreenModel<ForgetPasswordScreenModel>()

        var otpCode by remember { mutableStateOf("") }

        val isLoading by screenModel.isLoading.collectAsState()
        val isOtpVerified by screenModel.isOtpVerified.collectAsState()
        val resetToken by screenModel.resetToken.collectAsState()
        val errorMessage by screenModel.errorMessage.collectAsState()

        val snackbarHostState = remember { SnackbarHostState() }

        // 🌟 ถ้ายืนยันสำเร็จ (Token มาแล้ว) ให้ไปหน้าตั้งรหัสผ่านใหม่
        LaunchedEffect(isOtpVerified) {
            if (isOtpVerified && resetToken.isNotEmpty()) {
                screenModel.clearFlags()
                navigator.push(ResetPasswordScreen(resetToken)) // ส่ง Token ไปให้หน้า 3
            }
        }

        LaunchedEffect(errorMessage) {
            errorMessage?.let { msg ->
                snackbarHostState.showSnackbar(message = msg)
                screenModel.resetState()
            }
        }

        Box(modifier = Modifier.fillMaxSize()) {
            ConfirmOTPContent(
                emailSentTo = email,
                otpCode = otpCode,
                onOtpChange = { otpCode = it },
                onBackClick = { navigator.pop() },
                onVerifyClick = { screenModel.verifyOtp(email, otpCode) },
                onResendClick = { screenModel.sendOtp(email) }
            )

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
fun ConfirmOTPContent(emailSentTo: String, otpCode: String, onOtpChange: (String) -> Unit, onBackClick: () -> Unit, onVerifyClick: () -> Unit, onResendClick: () -> Unit) {
    WavyBackground {
        Column(modifier = Modifier.fillMaxSize().statusBarsPadding().padding(24.dp)) {
            Icon(painter = painterResource(Res.drawable.ic_common_back), contentDescription = "Back", tint = LightPrimary, modifier = Modifier.size(24.dp).clickable { onBackClick() })
            Spacer(modifier = Modifier.height(40.dp))
            Text(text = "ยืนยันรหัส OTP", color = LightPrimary, fontSize = 28.sp, modifier = Modifier.padding(horizontal = 8.dp))
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = "เราได้ส่งรหัส 6 หลักไปที่\n$emailSentTo", color = LightMuted, fontSize = 14.sp, modifier = Modifier.padding(horizontal = 8.dp))
            Spacer(modifier = Modifier.height(48.dp))

            OutlinedTextField(
                value = otpCode,
                onValueChange = { if (it.length <= 6 && it.all { char -> char.isDigit() }) onOtpChange(it) },
                modifier = Modifier.fillMaxWidth().height(64.dp),
                shape = RoundedCornerShape(percent = 30), singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                textStyle = LocalTextStyle.current.copy(textAlign = TextAlign.Center, fontSize = 24.sp, letterSpacing = 16.sp, fontWeight = FontWeight.Bold),
                colors = OutlinedTextFieldDefaults.colors(focusedContainerColor = LightSurface, unfocusedContainerColor = LightSurface, focusedBorderColor = LightPrimary, unfocusedBorderColor = LightBorder)
            )

            Spacer(modifier = Modifier.height(40.dp))
            Button(
                onClick = onVerifyClick, modifier = Modifier.fillMaxWidth().height(56.dp), shape = RoundedCornerShape(percent = 30),
                colors = ButtonDefaults.buttonColors(containerColor = LightPrimary)
            ) {
                Text("ยืนยัน", fontSize = 18.sp, color = LightSurface)
            }

            Spacer(modifier = Modifier.height(24.dp))
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center, verticalAlignment = Alignment.CenterVertically) {
                Text(text = "ไม่ได้รับรหัส? ", color = LightMuted, fontSize = 14.sp)
                Text(text = "ส่งรหัสอีกครั้ง", color = LightPrimary, fontSize = 14.sp, textDecoration = TextDecoration.Underline, modifier = Modifier.clickable { onResendClick() })
            }
        }
    }
}