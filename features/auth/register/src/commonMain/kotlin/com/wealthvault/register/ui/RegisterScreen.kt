package com.wealthvault.register.ui

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.*
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.wealthvault.core.utils.getScreenModel
import com.wealthvault.core.theme.*
import com.wealthvault.core.generated.resources.Res
import org.jetbrains.compose.resources.painterResource
import com.wealthvault.core.generated.resources.register
import com.wealthvault.core.generated.resources.ic_auth_email
import com.wealthvault.core.generated.resources.ic_auth_google
import com.wealthvault.core.generated.resources.ic_auth_eye
import com.wealthvault.core.generated.resources.ic_auth_eye_slash
import com.wealthvault.core.generated.resources.ic_auth_lock

class RegisterScreen : Screen {
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val screenModel = getScreenModel<RegisterScreenModel>()

        RegisterContent(
            username = screenModel.username,
            onUsernameChange = {
                screenModel.username = it
                screenModel.errorMessage = null
            },
            password = screenModel.password,
            onPasswordChange = {
                screenModel.password = it
                screenModel.errorMessage = null
            },
            confirmPassword = screenModel.confirmPassword,
            onConfirmPasswordChange = {
                screenModel.confirmPassword = it
                screenModel.errorMessage = null
            },
            isLoading = screenModel.isLoading,
            errorMessage = screenModel.errorMessage,
            onRegisterClick = {
                if (!screenModel.isLoading) {
                    // 🌟 1. กำหนดรูปแบบของอีเมล (Regex)
                    val emailRegex = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[a-zA-Z]{2,}$".toRegex()

                    // 🌟 2. ดักเช็คว่ากรอกครบไหม และอีเมลถูกต้องไหม
                    if (screenModel.username.isBlank() || screenModel.password.isBlank() || screenModel.confirmPassword.isBlank()) {
                        screenModel.errorMessage = "กรุณากรอกข้อมูลให้ครบถ้วน"
                    } else if (!screenModel.username.matches(emailRegex)) {
                        screenModel.errorMessage = "รูปแบบอีเมลไม่ถูกต้อง"
                    } else {
                        // 🌟 3. ถ้าผ่านหมดค่อยให้เรียก API สมัครสมาชิก
                        screenModel.onRegisterClick {
                            navigator.pop()
                        }
                    }
                }
            },
            onLoginClick = { navigator.pop() },
            onGoogleClick = { screenModel.onGoogleClick { /* TODO */ } }
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
    errorMessage: String?,
    onRegisterClick: () -> Unit,
    onGoogleClick: () -> Unit
) {
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
                .verticalScroll(rememberScrollState())
                .statusBarsPadding()
                .imePadding()
                .padding(horizontal = 24.dp, vertical = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top
        ) {

            Spacer(modifier = Modifier.height(20.dp))

            Text(
                text = "Wealth & Vault",
                color = LightPrimary,
                style = MaterialTheme.typography.titleLarge.copy(
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp
                ),
                modifier = Modifier.padding(horizontal = 8.dp)
            )

            Image(
                painter = painterResource(Res.drawable.register),
                contentDescription = "Register Illustration",
                modifier = Modifier
                    .size(140.dp)
                    .padding(10.dp),
                contentScale = ContentScale.Fit
            )

            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // --- 1. ช่องอีเมล ---
                Column(modifier = Modifier.fillMaxWidth()) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 8.dp, start = 8.dp, end = 8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.Bottom
                    ) {
                        Text(text = "อีเมล", color = LightPrimary, style = MaterialTheme.typography.bodyLarge)

                        // 🌟 ข้อความ Error จะโชว์ตรงนี้ แต่ไม่มีขอบแดงในกล่อง
                        if (errorMessage != null) {
                            Text(
                                text = errorMessage,
                                color = RedErr,
                                style = MaterialTheme.typography.labelMedium,
                                textAlign = TextAlign.End,
                                modifier = Modifier.weight(1f).padding(start = 8.dp)
                            )
                        }
                    }

                    CustomRegisterTextField(
                        value = username,
                        onValueChange = onUsernameChange,
                        placeholder = "อีเมล",
                        leadingIcon = Res.drawable.ic_auth_email
                        // เอา parameter isError ออกแล้ว เพื่อล้างไฮไลท์สีแดง
                    )
                }

                Spacer(modifier = Modifier.height(14.dp))
                var isPasswordVisible by remember { mutableStateOf(false) }

                // --- 2. ช่องรหัสผ่าน ---
                Column(modifier = Modifier.fillMaxWidth()) {
                    Text(
                        text = "รหัสผ่าน",
                        color = LightPrimary,
                        style = MaterialTheme.typography.bodyLarge,
                        modifier = Modifier.padding(bottom = 8.dp, start = 8.dp)
                    )
                    CustomRegisterTextField(
                        value = password,
                        onValueChange = onPasswordChange,
                        placeholder = "รหัสผ่าน",
                        leadingIcon = Res.drawable.ic_auth_lock,
                        isPassword = true,
                        isPasswordVisible = isPasswordVisible,
                        onPasswordVisibleChange = { isPasswordVisible = it }
                    )
                }

                Spacer(modifier = Modifier.height(14.dp))
                var isConfirmPasswordVisible by remember { mutableStateOf(false) }

                // --- 3. ช่องยืนยันรหัสผ่าน ---
                Column(modifier = Modifier.fillMaxWidth()) {
                    Text(
                        text = "ยืนยันรหัสผ่าน",
                        color = LightPrimary,
                        style = MaterialTheme.typography.bodyLarge,
                        modifier = Modifier.padding(bottom = 8.dp, start = 8.dp)
                    )
                    CustomRegisterTextField(
                        value = confirmPassword,
                        onValueChange = onConfirmPasswordChange,
                        placeholder = "ยืนยันรหัสผ่าน",
                        leadingIcon = Res.drawable.ic_auth_lock,
                        isPassword = true,
                        isPasswordVisible = isConfirmPasswordVisible,
                        onPasswordVisibleChange = { isConfirmPasswordVisible = it }
                    )
                }

                Spacer(modifier = Modifier.height(36.dp))

                // --- 4. ปุ่มสร้างบัญชี ---
                Button(
                    onClick = onRegisterClick,
                    enabled = !isLoading,
                    modifier = Modifier.fillMaxWidth().height(46.dp),
                    shape = RoundedCornerShape(percent = 30),
                    colors = ButtonDefaults.buttonColors(containerColor = LightPrimary)
                ) {
                    Text("สร้างบัญชี", style = MaterialTheme.typography.bodyLarge, color = LightSurface)
                }

                Spacer(modifier = Modifier.height(20.dp))

                // --- 5. มีบัญชีอยู่แล้ว? ---
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center, verticalAlignment = Alignment.CenterVertically) {
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

                // --- 6. เส้นคั่น หรือ ---
                Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth().padding(horizontal = 32.dp)) {
                    HorizontalDivider(modifier = Modifier.weight(1f), color = LightBorder, thickness = 2.dp)
                    Text(text = " หรือ ", color = LightMuted, style = MaterialTheme.typography.bodyMedium, modifier = Modifier.padding(horizontal = 8.dp))
                    HorizontalDivider(modifier = Modifier.weight(1f), color = LightBorder, thickness = 2.dp)
                }

                Spacer(modifier = Modifier.height(24.dp))

                // --- 7. ปุ่ม Google ---
                OutlinedButton(
                    onClick = onGoogleClick,
                    enabled = !isLoading,
                    modifier = Modifier.fillMaxWidth().height(46.dp).padding(horizontal = 48.dp),
                    shape = RoundedCornerShape(percent = 30),
                    border = BorderStroke(1.dp, LightBorder),
                    colors = ButtonDefaults.outlinedButtonColors(containerColor = LightSurface)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(painterResource(Res.drawable.ic_auth_google), contentDescription = "Google", modifier = Modifier.size(24.dp), tint = Color.Unspecified)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Google", color = LightPrimary, style = MaterialTheme.typography.bodyLarge)
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }
}

// 🌟 นำ isError ออกจากพารามิเตอร์ เพื่อไม่ให้มันรับค่ามาวาดเส้นสีแดงแล้วครับ
@Composable
fun CustomRegisterTextField(
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    leadingIcon: org.jetbrains.compose.resources.DrawableResource,
    isPassword: Boolean = false,
    isPasswordVisible: Boolean = false,
    onPasswordVisibleChange: (Boolean) -> Unit = {}
) {
    BasicTextField(
        value = value,
        onValueChange = onValueChange,
        singleLine = true,
        visualTransformation = if (isPassword && !isPasswordVisible) PasswordVisualTransformation() else VisualTransformation.None,
        keyboardOptions = if (isPassword) KeyboardOptions(keyboardType = KeyboardType.Password) else KeyboardOptions.Default,
        textStyle = LocalTextStyle.current.copy(color = Color.Black, fontSize = 16.sp),
        cursorBrush = SolidColor(LightPrimary), // กลับมาใช้สีส้มปกติ
        modifier = Modifier.fillMaxWidth().height(44.dp),
        decorationBox = { innerTextField ->
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .background(LightSurface, RoundedCornerShape(percent = 30))
                    .border(
                        width = 1.dp,
                        color = LightBorder, // สีขอบปกติเสมอ
                        shape = RoundedCornerShape(percent = 30)
                    )
                    .padding(start = 16.dp, end = if (isPassword) 4.dp else 16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    painter = painterResource(leadingIcon),
                    contentDescription = null,
                    tint = LightPrimary, // สีไอคอนปกติเสมอ
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(12.dp))
                Box(modifier = Modifier.weight(1f)) {
                    if (value.isEmpty()) {
                        Text(placeholder, color = Color.Gray)
                    }
                    innerTextField()
                }
                if (isPassword) {
                    val icon = if (isPasswordVisible) painterResource(Res.drawable.ic_auth_eye) else painterResource(Res.drawable.ic_auth_eye_slash)
                    IconButton(
                        onClick = { onPasswordVisibleChange(!isPasswordVisible) },
                        modifier = Modifier.size(44.dp)
                    ) {
                        Icon(painter = icon, contentDescription = "Toggle", tint = LightPrimary, modifier = Modifier.size(22.dp)) // สีลูกตาปกติเสมอ
                    }
                }
            }
        }
    )
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