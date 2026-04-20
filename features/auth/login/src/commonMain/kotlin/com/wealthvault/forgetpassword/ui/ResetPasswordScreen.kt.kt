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
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
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
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
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
            Spacer(modifier = Modifier.height(40.dp))
            Text(text = "ตั้งรหัสผ่านใหม่", color = LightPrimary, style = MaterialTheme.typography.headlineLarge, modifier = Modifier.padding(horizontal = 8.dp))
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = "รหัสผ่านใหม่ของคุณต้องแตกต่างจาก\nรหัสผ่านที่เคยใช้งาน", color = LightMuted, style = MaterialTheme.typography.bodyMedium, modifier = Modifier.padding(horizontal = 8.dp))
            Spacer(modifier = Modifier.height(48.dp))

            // 🌟 1. ช่องรหัสผ่านใหม่
            Column(modifier = Modifier.fillMaxWidth()) {
                // 🌟 เปลี่ยนมาใช้ Row เพื่อดึงข้อความ Error มาไว้มุมขวา
                Row(
                    modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp, start = 8.dp, end = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Bottom
                ) {
                    Text(
                        text = "รหัสผ่านใหม่",
                        color = LightPrimary,
                        style = MaterialTheme.typography.titleMedium
                    )

                    // 🌟 แสดง Error Message เล็กๆ สีแดงด้านขวา
                    if (errorMessage != null) {
                        Text(
                            text = errorMessage,
                            color = Color(0xFFE53935), // สีแดงแจ้งเตือน
                            style = MaterialTheme.typography.labelSmall,
                            textAlign = TextAlign.End,
                            modifier = Modifier.weight(1f).padding(start = 8.dp)
                        )
                    }
                }

                OutlinedTextField(
                    value = newPassword,
                    onValueChange = onNewPasswordChange,
                    modifier = Modifier.fillMaxWidth().height(56.dp),
                    shape = RoundedCornerShape(percent = 30),
                    singleLine = true,
                    // 🌟 จัดการเปิด-ปิดตา
                    visualTransformation = if (isNewPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                    leadingIcon = {
                        Icon(painterResource(Res.drawable.ic_auth_lock), contentDescription = "lock", tint = LightPrimary, modifier = Modifier.size(26.dp))
                    },
                    trailingIcon = {
                        val icon = if (isNewPasswordVisible) painterResource(Res.drawable.ic_auth_eye) else painterResource(Res.drawable.ic_auth_eye_slash)
                        IconButton(onClick = { isNewPasswordVisible = !isNewPasswordVisible }) {
                            Icon(painter = icon, contentDescription = "Toggle", tint = LightPrimary, modifier = Modifier.size(24.dp))
                        }
                    },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = LightSurface,
                        unfocusedContainerColor = LightSurface,
                        focusedBorderColor = LightPrimary,
                        unfocusedBorderColor = LightBorder,
                        errorBorderColor = Color(0xFFE53935), // 🌟 สีกรอบตอน Error
                        errorLeadingIconColor = Color(0xFFE53935)
                    ),
                    isError = errorMessage != null // 🌟
                )
            }
            Spacer(modifier = Modifier.height(16.dp))

            // 🌟 2. ช่องยืนยันรหัสผ่านใหม่
            Column(modifier = Modifier.fillMaxWidth()) {
                Text(text = "ยืนยันรหัสผ่านใหม่", color = LightPrimary, style = MaterialTheme.typography.titleMedium, modifier = Modifier.padding(bottom = 8.dp, start = 8.dp))
                OutlinedTextField(
                    value = confirmPassword,
                    onValueChange = onConfirmPasswordChange,
                    modifier = Modifier.fillMaxWidth().height(56.dp),
                    shape = RoundedCornerShape(percent = 30),
                    singleLine = true,
                    // 🌟 จัดการเปิด-ปิดตา
                    visualTransformation = if (isConfirmPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                    leadingIcon = {
                        Icon(painterResource(Res.drawable.ic_auth_lock), contentDescription = "lock", tint = LightPrimary, modifier = Modifier.size(26.dp))
                    },
                    trailingIcon = {
                        val icon = if (isConfirmPasswordVisible) painterResource(Res.drawable.ic_auth_eye) else painterResource(Res.drawable.ic_auth_eye_slash)
                        IconButton(onClick = { isConfirmPasswordVisible = !isConfirmPasswordVisible }) {
                            Icon(painter = icon, contentDescription = "Toggle Confirm", tint = LightPrimary, modifier = Modifier.size(24.dp))
                        }
                    },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = LightSurface,
                        unfocusedContainerColor = LightSurface,
                        focusedBorderColor = LightPrimary,
                        unfocusedBorderColor = LightBorder,
                        errorBorderColor = Color(0xFFE53935), // 🌟 สีกรอบตอน Error
                        errorLeadingIconColor = Color(0xFFE53935)
                    ),
                    isError = errorMessage != null // 🌟
                )
            }

            Spacer(modifier = Modifier.height(32.dp))


            Button(
                onClick = onSubmitClick,
                modifier = Modifier.fillMaxWidth().height(50.dp),
                shape = RoundedCornerShape(percent = 30),
                colors = ButtonDefaults.buttonColors(containerColor = LightPrimary)
            ) {
                Text("เปลี่ยนรหัสผ่าน", style = MaterialTheme.typography.titleMedium, color = LightSurface)
            }
        }
    }
}