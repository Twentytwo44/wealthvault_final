package com.wealthvault.register.ui

import androidx.compose.foundation.BorderStroke
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
//import com.example.register.ui.RegisterScreenModel
import com.wealthvault.core.utils.getScreenModel

// Import สีจาก theme ของเรา
import com.wealthvault.core.theme.LightPrimary
import com.wealthvault.core.theme.LightSurface
import com.wealthvault.core.theme.LightBorder
import com.wealthvault.core.theme.LightMuted
import com.wealthvault.core.theme.WvBgGradientStart
import com.wealthvault.core.theme.WvBgGradientEnd
import com.wealthvault.core.theme.WvWaveGradientEnd
import com.wealthvault.core.theme.WvWaveGradientStart

@Composable
fun RegisterContent(
    username: String,
    onUsernameChange: (String) -> Unit,
    password: String,
    onPasswordChange: (String) -> Unit,
    isLoading: Boolean,
    onRegisterClick: () -> Unit
) {
    // ลบตัวแปรสีตรงนี้ออกไปได้เลย เพราะเราดึงจาก Theme มาใช้แล้ว
    WavyBackground {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = "Wealth & Vault",
                color = LightPrimary, // ใช้ LightPrimary
                fontSize = 28.sp,
                modifier = Modifier.padding(horizontal = 8.dp)
            )

            Spacer(modifier = Modifier.height(150.dp))

            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // 1. กล่อง Input 3 ช่อง (อีเมล, รหัสผ่าน, ยืนยันรหัสผ่าน)
                // --- ช่องอีเมล ---
                Column(modifier = Modifier.fillMaxWidth()) {
                    Text(
                        text = "อีเมล",
                        color = LightPrimary,
                        fontSize = 18.sp,
                        modifier = Modifier.padding(bottom = 8.dp, start = 8.dp)
                    )
                    OutlinedTextField(
                        value = username,
                        onValueChange = onUsernameChange,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp),
                        shape = RoundedCornerShape(percent = 30),
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedContainerColor = LightSurface,
                            unfocusedContainerColor = LightSurface,
                            focusedBorderColor = LightPrimary,
                            unfocusedBorderColor = LightBorder,
                        )
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                // --- ช่องรหัสผ่าน ---
                Column(modifier = Modifier.fillMaxWidth()) {
                    Text(
                        text = "รหัสผ่าน",
                        color = LightPrimary,
                        fontSize = 18.sp,
                        modifier = Modifier.padding(bottom = 8.dp, start = 8.dp)
                    )
                    OutlinedTextField(
                        value = password,
                        onValueChange = onPasswordChange,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp),
                        shape = RoundedCornerShape(percent = 30),
                        singleLine = true,
                        visualTransformation = PasswordVisualTransformation(),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedContainerColor = LightSurface,
                            unfocusedContainerColor = LightSurface,
                            focusedBorderColor = LightPrimary,
                            unfocusedBorderColor = LightBorder,
                        )
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                // --- ช่องยืนยันรหัสผ่าน ---
                Column(modifier = Modifier.fillMaxWidth()) {
                    Text(
                        text = "ยืนยันรหัสผ่าน",
                        color = LightPrimary,
                        fontSize = 18.sp,
                        modifier = Modifier.padding(bottom = 8.dp, start = 8.dp)
                    )
                    OutlinedTextField(
                        // TODO: ในอนาคตอาจจะต้องเพิ่มตัวแปร confirmPassword แยกจาก password นะครับ
                        value = password,
                        onValueChange = onPasswordChange,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp),
                        shape = RoundedCornerShape(percent = 30),
                        singleLine = true,
                        visualTransformation = PasswordVisualTransformation(),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedContainerColor = LightSurface,
                            unfocusedContainerColor = LightSurface,
                            focusedBorderColor = LightPrimary,
                            unfocusedBorderColor = LightBorder,
                        )
                    )
                }

                Spacer(modifier = Modifier.height(32.dp))

                // 3. ปุ่มสร้างบัญชี
                Button(
                    onClick = onRegisterClick,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    shape = RoundedCornerShape(percent = 30),
                    colors = ButtonDefaults.buttonColors(containerColor = LightPrimary)
                ) {
                    Text("สร้างบัญชี", fontSize = 18.sp, color = LightSurface)
                }

                Spacer(modifier = Modifier.height(24.dp))

                // 4. มีบัญชีอยู่แล้ว เข้าสู่ระบบ?
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(text = "มีบัญชีอยู่แล้ว ", color = LightMuted, fontSize = 14.sp)
                    Text(
                        text = "เข้าสู่ระบบ?",
                        color = LightPrimary,
                        fontSize = 14.sp,
                        textDecoration = TextDecoration.Underline,
                        modifier = Modifier.clickable { /* TODO: นำทางไปหน้าเข้าสู่ระบบ */ }
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))

                // 5. เส้นคั่น "หรือ"
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 32.dp)
                ) {
                    HorizontalDivider(
                        modifier = Modifier.weight(1f),
                        color = LightBorder,
                        thickness = 2.dp
                    )
                    Text(
                        text = " หรือ ",
                        color = LightMuted,
                        fontSize = 14.sp,
                        modifier = Modifier.padding(horizontal = 8.dp)
                    )
                    HorizontalDivider(
                        modifier = Modifier.weight(1f),
                        color = LightBorder,
                        thickness = 2.dp
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))

                // 6. ปุ่ม Google
                OutlinedButton(
                    onClick = { /* TODO: Google Register */ },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp)
                        .padding(horizontal = 48.dp),
                    shape = RoundedCornerShape(percent = 30),
                    border = BorderStroke(1.dp, LightBorder),
                    colors = ButtonDefaults.outlinedButtonColors(containerColor = LightSurface)
                ) {
                    Text("Google", color = LightPrimary, fontSize = 16.sp)
                }
            }
        }
    }
}


@Composable
fun WavyBackground(
    // เปลี่ยนมารับค่าเป็น Brush (การไล่สี) แทน Color
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
            .background(bottomBgBrush) // ระบายสีพื้นหลังด้วย Gradient
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
                // วาดเส้นคลื่นแล้วระบายด้วย Gradient
                drawPath(path = path, brush = topWaveBrush)
            }
    ) {
        content()
    }
}