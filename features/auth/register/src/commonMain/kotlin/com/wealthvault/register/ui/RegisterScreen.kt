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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow

// 🌟 Import ของที่เราต้องใช้
import com.wealthvault.core.utils.getScreenModel
import com.wealthvault.core.theme.*

// 🌟 Import Resource สำหรับรูปภาพ
import com.wealthvault.core.generated.resources.Res
import org.jetbrains.compose.resources.painterResource
import com.wealthvault.core.generated.resources.ic_auth_email
import com.wealthvault.core.generated.resources.ic_auth_google
import com.wealthvault.core.generated.resources.ic_auth_eye
import com.wealthvault.core.generated.resources.ic_auth_eye_slash
import com.wealthvault.core.generated.resources.ic_auth_lock

// ==========================================
// 🌟 1. สร้างคลาส Screen สำหรับให้ Voyager เรียกใช้
// ==========================================
class RegisterScreen : Screen {
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val screenModel = getScreenModel<RegisterScreenModel>()

        // 🌟 จำสถานะของช่อง "ยืนยันรหัสผ่าน" แยกต่างหาก
        var confirmPasswordText by remember { mutableStateOf("") }

        RegisterContent(
            username = screenModel.username,
            onUsernameChange = { screenModel.username = it },
            password = screenModel.password,
            onPasswordChange = { screenModel.password = it },
            confirmPassword = confirmPasswordText, // ส่งค่า confirmPassword ลงไป
            onConfirmPasswordChange = { confirmPasswordText = it }, // รับค่า confirmPassword กลับมา
            isLoading = screenModel.isLoading,
            onRegisterClick = {
                // เช็คว่ารหัสผ่าน 2 ช่องตรงกันไหม ถ้าตรงค่อยให้ ScreenModel ทำงานต่อ
                if (screenModel.password == confirmPasswordText) {
                    screenModel.onRegisterClick {
                        // สมัครสำเร็จ เด้งกลับไปหน้า Login หรือไปหน้าหลัก
                        navigator.pop()
                    }
                } else {
                    // TODO: แสดง Error ว่ารหัสผ่านไม่ตรงกัน
                }
            },
            onLoginClick = {
                // กด "เข้าสู่ระบบ?" -> ให้ดึงหน้าตัวเองออก เพื่อกลับไปหน้า Login
                navigator.pop()
            },
            onGoogleClick = {
                screenModel.onGoogleClick { /* TODO */ }
            }
        )
    }
}

@Composable
fun RegisterContent(
    username: String,
    onUsernameChange: (String) -> Unit,
    password: String,
    onPasswordChange: (String) -> Unit,
    confirmPassword: String,
    onConfirmPasswordChange: (String) -> Unit,
    onLoginClick: () -> Unit,
    isLoading: Boolean,
    onRegisterClick: () -> Unit,
    onGoogleClick: () -> Unit
) {
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
                color = LightPrimary,
                style = MaterialTheme.typography.headlineLarge,
                modifier = Modifier.padding(horizontal = 8.dp)
            )

            Spacer(modifier = Modifier.height(150.dp))

            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // 1. ช่องอีเมล
                Column(modifier = Modifier.fillMaxWidth()) {
                    Text(
                        text = "อีเมล",
                        color = LightPrimary,
                        style = MaterialTheme.typography.titleMedium,
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
                        leadingIcon = {
                            Icon(
                                painter = painterResource(Res.drawable.ic_auth_email),
                                contentDescription = "email",
                                tint = LightPrimary,
                                modifier = Modifier.size(26.dp)
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

                Spacer(modifier = Modifier.height(16.dp))

                var isPasswordVisible by remember { mutableStateOf(false) }

                // 2. ช่องรหัสผ่าน
                Column(modifier = Modifier.fillMaxWidth()) {
                    Text(
                        text = "รหัสผ่าน",
                        color = LightPrimary,
                        style = MaterialTheme.typography.titleMedium,
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
                        visualTransformation = if (isPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                        leadingIcon = {
                            Icon(
                                painter = painterResource(Res.drawable.ic_auth_lock),
                                contentDescription = "lock",
                                tint = LightPrimary,
                                modifier = Modifier.size(26.dp)
                            )
                        },
                        trailingIcon = {
                            val icon = if (isPasswordVisible) painterResource(Res.drawable.ic_auth_eye) else painterResource(Res.drawable.ic_auth_eye_slash)
                            IconButton(onClick = { isPasswordVisible = !isPasswordVisible }) {
                                Icon(
                                    painter = icon,
                                    contentDescription = "Toggle Password Visibility",
                                    tint = LightPrimary,
                                    modifier = Modifier.size(24.dp)
                                )
                            }
                        },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedContainerColor = LightSurface,
                            unfocusedContainerColor = LightSurface,
                            focusedBorderColor = LightPrimary,
                            unfocusedBorderColor = LightBorder,
                        )
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                var isConfirmPasswordVisible by remember { mutableStateOf(false) }

                // 3. ช่องยืนยันรหัสผ่าน
                Column(modifier = Modifier.fillMaxWidth()) {
                    Text(
                        text = "ยืนยันรหัสผ่าน",
                        color = LightPrimary,
                        style = MaterialTheme.typography.titleMedium,
                        modifier = Modifier.padding(bottom = 8.dp, start = 8.dp)
                    )
                    OutlinedTextField(
                        value = confirmPassword,
                        onValueChange = onConfirmPasswordChange,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp),
                        shape = RoundedCornerShape(percent = 30),
                        singleLine = true,
                        visualTransformation = if (isConfirmPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                        leadingIcon = {
                            Icon(
                                painter = painterResource(Res.drawable.ic_auth_lock),
                                contentDescription = "lock",
                                tint = LightPrimary,
                                modifier = Modifier.size(26.dp)
                            )
                        },
                        trailingIcon = {
                            val icon = if (isConfirmPasswordVisible) painterResource(Res.drawable.ic_auth_eye) else painterResource(Res.drawable.ic_auth_eye_slash)
                            IconButton(onClick = { isConfirmPasswordVisible = !isConfirmPasswordVisible }) {
                                Icon(
                                    painter = icon,
                                    contentDescription = "Toggle Confirm Password Visibility",
                                    tint = LightPrimary,
                                    modifier = Modifier.size(24.dp)
                                )
                            }
                        },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedContainerColor = LightSurface,
                            unfocusedContainerColor = LightSurface,
                            focusedBorderColor = LightPrimary,
                            unfocusedBorderColor = LightBorder,
                        )
                    )
                }

                Spacer(modifier = Modifier.height(32.dp))

                // 4. ปุ่มสร้างบัญชี
                Button(
                    onClick = onRegisterClick,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp), // ปรับเป็น 50.dp เหมือนหน้า Login
                    shape = RoundedCornerShape(percent = 30),
                    colors = ButtonDefaults.buttonColors(containerColor = LightPrimary)
                ) {
                    Text("สร้างบัญชี", style = MaterialTheme.typography.titleMedium, color = LightSurface)
                }

                Spacer(modifier = Modifier.height(24.dp))

                // 5. มีบัญชีอยู่แล้ว เข้าสู่ระบบ?
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(text = "มีบัญชีอยู่แล้ว ", color = LightMuted, style = MaterialTheme.typography.bodyMedium)
                    Text(
                        text = "เข้าสู่ระบบ?",
                        color = LightPrimary,
                        style = MaterialTheme.typography.bodyMedium,
                        textDecoration = TextDecoration.Underline,
                        modifier = Modifier.clickable { onLoginClick() }
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))

                // 6. เส้นคั่น หรือ
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 32.dp)
                ) {
                    HorizontalDivider(modifier = Modifier.weight(1f), color = LightBorder, thickness = 2.dp)
                    Text(text = " หรือ ", color = LightMuted, style = MaterialTheme.typography.bodyMedium, modifier = Modifier.padding(horizontal = 8.dp))
                    HorizontalDivider(modifier = Modifier.weight(1f), color = LightBorder, thickness = 2.dp)
                }

                Spacer(modifier = Modifier.height(24.dp))

                // 7. ปุ่ม Google
                OutlinedButton(
                    onClick = onGoogleClick,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp)
                        .padding(horizontal = 48.dp),
                    shape = RoundedCornerShape(percent = 30),
                    border = BorderStroke(1.dp, LightBorder),
                    colors = ButtonDefaults.outlinedButtonColors(containerColor = LightSurface)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            painter = painterResource(Res.drawable.ic_auth_google),
                            contentDescription = "Google Logo",
                            modifier = Modifier.size(36.dp),
                            tint = Color.Unspecified
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Google", color = LightPrimary, style = MaterialTheme.typography.bodyLarge)
                    }
                }
            }
        }
    }
}

@Composable
fun WavyBackground(
    topWaveBrush: Brush = Brush.verticalGradient(colors = listOf(WvWaveGradientStart, WvWaveGradientEnd)),
    bottomBgBrush: Brush = Brush.verticalGradient(colors = listOf(WvBgGradientStart, WvBgGradientEnd)),
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