package com.wealthvault.login.ui

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
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

// Import ของที่เราต้องใช้
import com.wealthvault.core.utils.getScreenModel
import com.wealthvault.core.theme.*
import com.wealthvault.core.generated.resources.Res
import org.jetbrains.compose.resources.painterResource
import com.wealthvault.core.generated.resources.ic_auth_email
import com.wealthvault.core.generated.resources.ic_auth_google
import com.wealthvault.core.generated.resources.ic_auth_eye
import com.wealthvault.core.generated.resources.ic_auth_eye_slash
import com.wealthvault.core.generated.resources.ic_auth_lock
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.window.Dialog
import com.wealthvault.forgetpassword.ui.ForgetPasswordScreen
import com.wealthvault.register.ui.RegisterScreen

class LoginScreen(private val navigateToScreen: Screen) : Screen {
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val screenModel = getScreenModel<LoginScreenModel>()

        LoginContent(
            username = screenModel.username,
            onUsernameChange = {
                screenModel.username = it
                screenModel.errorMessage = null // 🌟 ทริค UX: ล้าง Error ทันทีเมื่อผู้ใช้เริ่มพิมพ์แก้
            },
            password = screenModel.password,
            onPasswordChange = {
                screenModel.password = it
                screenModel.errorMessage = null // 🌟 ทริค UX: ล้าง Error ทันทีเมื่อผู้ใช้เริ่มพิมพ์แก้
            },
            isLoading = screenModel.isLoading,
            errorMessage = screenModel.errorMessage,
            onLoginClick = {
                screenModel.onLoginClick {
                    navigator.replaceAll(navigateToScreen)
                }
            },
            onGoogleClick = { screenModel.onGoogleClick { /* TODO */ } },
            onForgotPasswordClick = { navigator.push(ForgetPasswordScreen()) },
            onRegisterClick = { navigator.push(RegisterScreen()) }
        )
    }
}

@Composable
fun LoginContent(
    username: String,
    onUsernameChange: (String) -> Unit,
    password: String,
    onPasswordChange: (String) -> Unit,
    isLoading: Boolean,
    errorMessage: String?,
    onLoginClick: () -> Unit,
    onGoogleClick: () -> Unit,
    onForgotPasswordClick: () -> Unit,
    onRegisterClick: () -> Unit
) {
    // 🌟 เช็คว่ามี Error ไหม
    val hasError = errorMessage != null

    WealthVaultTheme {
        WavyBackground {

            if (isLoading) {
                Dialog(onDismissRequest = {}) {
                    Box(
                        modifier = Modifier
                            .size(100.dp)
                            .background(Color.White, RoundedCornerShape(16.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(color = LightPrimary)
                    }
                }
            }

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
                    // --- ช่องอีเมล ---
                    Column(modifier = Modifier.fillMaxWidth()) {
                        // 🌟 ใช้ Row เพื่อวาง "อีเมล" ไว้ซ้าย และ "ข้อความ Error" ไว้ขวา
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = 8.dp, start = 8.dp, end = 8.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.Bottom // จัดให้อยู่ระดับเดียวกัน
                        ) {
                            Text(
                                text = "อีเมล",
                                color = LightPrimary, // 🌟 กลับไปใช้สีเดิม ไม่แดงแล้ว
                                style = MaterialTheme.typography.titleMedium
                            )

                            // 🌟 แสดง Error ตรงมุมขวาบน
                            if (hasError) {
                                Text(
                                    text = errorMessage ?: "",
                                    color = RedErr,
                                    style = MaterialTheme.typography.labelMedium
                                )
                            }
                        }

                        OutlinedTextField(
                            value = username,
                            onValueChange = onUsernameChange,
                            modifier = Modifier.fillMaxWidth().height(56.dp),
                            shape = RoundedCornerShape(percent = 30),
                            singleLine = true,
                            leadingIcon = {
                                Icon(painterResource(Res.drawable.ic_auth_email), contentDescription = "email", tint = LightPrimary, modifier = Modifier.size(26.dp))
                            },
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedContainerColor = LightSurface,
                                unfocusedContainerColor = LightSurface,
                                focusedBorderColor = LightPrimary,
                                unfocusedBorderColor = LightBorder
                                // 🌟 เอาพวก errorBorderColor ออกหมดแล้ว
                            )
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))
                    var isPasswordVisible by remember { mutableStateOf(false) }

                    // --- ช่องรหัสผ่าน ---
                    Column(modifier = Modifier.fillMaxWidth()) {
                        Text(
                            text = "รหัสผ่าน",
                            color = LightPrimary, // 🌟 กลับไปใช้สีเดิม ไม่แดงแล้ว
                            style = MaterialTheme.typography.titleMedium,
                            modifier = Modifier.padding(bottom = 8.dp, start = 8.dp)
                        )
                        OutlinedTextField(
                            value = password,
                            onValueChange = onPasswordChange,
                            modifier = Modifier.fillMaxWidth().height(56.dp),
                            shape = RoundedCornerShape(percent = 30),
                            singleLine = true,
                            visualTransformation = if (isPasswordVisible) androidx.compose.ui.text.input.VisualTransformation.None else PasswordVisualTransformation(),
                            leadingIcon = {
                                Icon(painterResource(Res.drawable.ic_auth_lock), contentDescription = "lock", tint = LightPrimary, modifier = Modifier.size(26.dp))
                            },
                            trailingIcon = {
                                val icon = if (isPasswordVisible) painterResource(Res.drawable.ic_auth_eye) else painterResource(Res.drawable.ic_auth_eye_slash)
                                IconButton(onClick = { isPasswordVisible = !isPasswordVisible }) {
                                    Icon(painter = icon, contentDescription = "Toggle Password Visibility", tint = LightPrimary, modifier = Modifier.size(24.dp))
                                }
                            },
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedContainerColor = LightSurface,
                                unfocusedContainerColor = LightSurface,
                                focusedBorderColor = LightPrimary,
                                unfocusedBorderColor = LightBorder
                                // 🌟 เอาพวก errorBorderColor ออกหมดแล้ว
                            )
                        )
                    }

                    // --- ปุ่มลืมรหัสผ่าน ---
                    Spacer(modifier = Modifier.height(10.dp))
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                        Text(
                            text = "ลืมรหัสผ่าน",
                            color = LightMuted,
                            style = MaterialTheme.typography.bodyMedium,
                            modifier = Modifier.clickable { onForgotPasswordClick() }.padding(vertical = 2.dp, horizontal = 6.dp)
                        )
                    }

                    // 🌟 คืนพื้นที่ว่างให้สวยงาม (เอา Error ตรงกลางออกไปแล้ว)
                    Spacer(modifier = Modifier.height(32.dp))

                    // --- ปุ่มเข้าสู่ระบบ ---
                    Button(
                        onClick = onLoginClick,
                        modifier = Modifier.fillMaxWidth().height(50.dp),
                        shape = RoundedCornerShape(percent = 30),
                        colors = ButtonDefaults.buttonColors(containerColor = LightPrimary)
                    ) {
                        Text("เข้าสู่ระบบ", style = MaterialTheme.typography.titleMedium, color = LightSurface)
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    // --- ยังไม่มีบัญชี ---
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center, verticalAlignment = Alignment.CenterVertically) {
                        Text(text = "ยังไม่มีบัญชี ", color = LightMuted, style = MaterialTheme.typography.bodyMedium)
                        Text(
                            text = "สร้างบัญชี?",
                            color = LightPrimary,
                            style = MaterialTheme.typography.bodyMedium,
                            textDecoration = TextDecoration.Underline,
                            modifier = Modifier.clickable { onRegisterClick() }
                        )
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    // --- เส้นคั่น หรือ ---
                    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth().padding(horizontal = 32.dp)) {
                        HorizontalDivider(modifier = Modifier.weight(1f), color = LightBorder, thickness = 2.dp)
                        Text(text = " หรือ ", color = LightMuted, style = MaterialTheme.typography.bodyMedium, modifier = Modifier.padding(horizontal = 8.dp))
                        HorizontalDivider(modifier = Modifier.weight(1f), color = LightBorder, thickness = 2.dp)
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    // --- ปุ่ม Google ---
                    OutlinedButton(
                        onClick = onGoogleClick,
                        modifier = Modifier.fillMaxWidth().height(56.dp).padding(horizontal = 48.dp),
                        shape = RoundedCornerShape(percent = 30),
                        border = BorderStroke(1.dp, LightBorder),
                        colors = ButtonDefaults.outlinedButtonColors(containerColor = LightSurface)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(painter = painterResource(Res.drawable.ic_auth_google), contentDescription = "Google Logo", modifier = Modifier.size(36.dp), tint = Color.Unspecified)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Google", color = LightPrimary, style = MaterialTheme.typography.bodyLarge)
                        }
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