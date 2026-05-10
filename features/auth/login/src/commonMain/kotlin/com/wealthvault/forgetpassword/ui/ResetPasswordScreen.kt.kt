package com.wealthvault.forgetpassword.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.wealthvault.core.generated.resources.Res
import com.wealthvault.core.generated.resources.ic_auth_eye
import com.wealthvault.core.generated.resources.ic_auth_eye_slash
import com.wealthvault.core.generated.resources.ic_auth_lock
import com.wealthvault.core.generated.resources.ic_common_back
import com.wealthvault.core.theme.LightBorder
import com.wealthvault.core.theme.LightMuted
import com.wealthvault.core.theme.LightPrimary
import com.wealthvault.core.theme.LightSurface
import com.wealthvault.core.theme.RedErr
import com.wealthvault.core.utils.getScreenModel
import org.jetbrains.compose.resources.painterResource

class ResetPasswordScreen(private val token: String) : Screen {
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val screenModel = getScreenModel<ForgetPasswordScreenModel>()

        var newPassword by remember { mutableStateOf("") }
        var confirmPassword by remember { mutableStateOf("") }

        val isLoading by screenModel.isLoading.collectAsState()
        val isPasswordReset by screenModel.isPasswordReset.collectAsState()
        val errorMessage by screenModel.errorMessage.collectAsState()

        // 🌟 ถ้าเปลี่ยนรหัสผ่านสำเร็จ ให้เด้งกลับไปหน้า Login
        LaunchedEffect(isPasswordReset) {
            if (isPasswordReset) {
                screenModel.clearFlags()
                navigator.popUntilRoot() // เด้งกลับไปหน้า Login
            }
        }

        Box(modifier = Modifier.fillMaxSize()) {
            ResetPasswordContent(
                newPassword = newPassword,
                onNewPasswordChange = {
                    newPassword = it
                    screenModel.resetState() // 🌟 ล้าง Error เมื่อเริ่มพิมพ์
                },
                confirmPassword = confirmPassword,
                onConfirmPasswordChange = {
                    confirmPassword = it
                    screenModel.resetState() // 🌟 ล้าง Error เมื่อเริ่มพิมพ์
                },
                errorMessage = errorMessage, // 🌟 โยน Error ให้ UI ไปแสดงผล
                onBackClick = { navigator.pop() },
                onSubmitClick = { screenModel.resetPassword(token, newPassword, confirmPassword) }
            )

            if (isLoading) {
                Box(
                    modifier = Modifier.fillMaxSize().background(Color.Black.copy(alpha = 0.2f)).clickable(enabled = false) {},
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = LightPrimary)
                }
            }
        }
    }
}

@Composable
fun ResetPasswordContent(
    newPassword: String,
    onNewPasswordChange: (String) -> Unit,
    confirmPassword: String,
    onConfirmPasswordChange: (String) -> Unit,
    errorMessage: String?,
    onBackClick: () -> Unit,
    onSubmitClick: () -> Unit
) {
    var isNewPasswordVisible by remember { mutableStateOf(false) }
    var isConfirmPasswordVisible by remember { mutableStateOf(false) }

    WavyBackground {
        Column(modifier = Modifier.fillMaxSize().statusBarsPadding().padding(24.dp)) {
            Icon(painter = painterResource(Res.drawable.ic_common_back), contentDescription = "Back", tint = LightPrimary, modifier = Modifier.size(24.dp).clickable { onBackClick() })
            Spacer(modifier = Modifier.height(30.dp))
            Text(text = "ตั้งรหัสผ่านใหม่", color = LightPrimary, style = MaterialTheme.typography.headlineLarge, modifier = Modifier.padding(horizontal = 8.dp))
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = "รหัสผ่านใหม่ของคุณต้องแตกต่างจาก\nรหัสผ่านที่เคยใช้งาน", color = LightMuted, style = MaterialTheme.typography.bodyMedium, modifier = Modifier.padding(horizontal = 8.dp))
            Spacer(modifier = Modifier.height(40.dp))

            // 🌟 1. ช่องรหัสผ่านใหม่
            Column(modifier = Modifier.fillMaxWidth()) {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp, start = 8.dp, end = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Bottom
                ) {
                    Text(
                        text = "รหัสผ่านใหม่",
                        color = LightPrimary,
                        style = MaterialTheme.typography.bodyLarge
                    )

                    if (errorMessage != null) {
                        Text(
                            text = errorMessage,
                            color = RedErr,
                            style = MaterialTheme.typography.labelSmall,
                            textAlign = TextAlign.End,
                            modifier = Modifier.weight(1f).padding(start = 8.dp)
                        )
                    }
                }

                BasicTextField(
                    value = newPassword,
                    onValueChange = onNewPasswordChange,
                    singleLine = true,
                    visualTransformation = if (isNewPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                    textStyle = LocalTextStyle.current.copy(color = Color.Black),
                    cursorBrush = SolidColor(if (errorMessage != null) RedErr else LightPrimary),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(44.dp),
                    decorationBox = { innerTextField ->
                        Row(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(LightSurface, RoundedCornerShape(percent = 30))
                                .border(
                                    width = 1.dp,
                                    color = if (errorMessage != null) RedErr else LightBorder,
                                    shape = RoundedCornerShape(percent = 30)
                                )
                                .padding(start = 16.dp, end = 8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                painter = painterResource(Res.drawable.ic_auth_lock),
                                contentDescription = "lock",
                                tint = if (errorMessage != null) RedErr else LightPrimary,
                                modifier = Modifier.size(26.dp)
                            )

                            Spacer(modifier = Modifier.width(12.dp))

                            Box(modifier = Modifier.weight(1f)) {
                                if (newPassword.isEmpty()) {
                                    Text("รหัสผ่านใหม่", color = Color.Gray)
                                }
                                innerTextField()
                            }

                            val icon = if (isNewPasswordVisible) painterResource(Res.drawable.ic_auth_eye)
                            else painterResource(Res.drawable.ic_auth_eye_slash)

                            IconButton(
                                onClick = { isNewPasswordVisible = !isNewPasswordVisible },
                                modifier = Modifier.size(48.dp)
                            ) {
                                Icon(
                                    painter = icon,
                                    contentDescription = "Toggle",
                                    tint = if (errorMessage != null) RedErr else LightPrimary,
                                    modifier = Modifier.size(24.dp)
                                )
                            }
                        }
                    }
                )
            }
            Spacer(modifier = Modifier.height(16.dp))

            // 🌟 2. ช่องยืนยันรหัสผ่านใหม่ (อัปเดตเป็น BasicTextField)
            Column(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = "ยืนยันรหัสผ่านใหม่",
                    color = LightPrimary,
                    style = MaterialTheme.typography.bodyLarge,
                    modifier = Modifier.padding(bottom = 8.dp, start = 8.dp)
                )

                BasicTextField(
                    value = confirmPassword,
                    onValueChange = onConfirmPasswordChange,
                    singleLine = true,
                    visualTransformation = if (isConfirmPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                    textStyle = LocalTextStyle.current.copy(color = Color.Black),
                    cursorBrush = SolidColor(if (errorMessage != null) RedErr else LightPrimary),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(44.dp),
                    decorationBox = { innerTextField ->
                        Row(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(LightSurface, RoundedCornerShape(percent = 30))
                                .border(
                                    width = 1.dp,
                                    color = if (errorMessage != null) RedErr else LightBorder,
                                    shape = RoundedCornerShape(percent = 30)
                                )
                                .padding(start = 16.dp, end = 8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            // Leading Icon (แม่กุญแจ)
                            Icon(
                                painter = painterResource(Res.drawable.ic_auth_lock),
                                contentDescription = "lock",
                                tint = if (errorMessage != null) RedErr else LightPrimary,
                                modifier = Modifier.size(26.dp)
                            )

                            Spacer(modifier = Modifier.width(12.dp))

                            // ช่องพิมพ์ยืนยันรหัสผ่าน
                            Box(modifier = Modifier.weight(1f)) {
                                if (confirmPassword.isEmpty()) {
                                    Text("ยืนยันรหัสผ่าน", color = Color.Gray)
                                }
                                innerTextField()
                            }

                            // Trailing Icon (ปุ่มลูกตา)
                            val icon = if (isConfirmPasswordVisible) painterResource(Res.drawable.ic_auth_eye)
                            else painterResource(Res.drawable.ic_auth_eye_slash)

                            IconButton(
                                onClick = { isConfirmPasswordVisible = !isConfirmPasswordVisible },
                                modifier = Modifier.size(48.dp)
                            ) {
                                Icon(
                                    painter = icon,
                                    contentDescription = "Toggle Confirm",
                                    tint = if (errorMessage != null) RedErr else LightPrimary,
                                    modifier = Modifier.size(24.dp)
                                )
                            }
                        }
                    }
                )
            }

            Spacer(modifier = Modifier.height(40.dp))

            Button(
                onClick = onSubmitClick,
                modifier = Modifier.fillMaxWidth().height(46.dp),
                shape = RoundedCornerShape(percent = 30),
                colors = ButtonDefaults.buttonColors(containerColor = LightPrimary)
            ) {
                Text("เปลี่ยนรหัสผ่าน", style = MaterialTheme.typography.bodyLarge, color = LightSurface)
            }
        }
    }
}