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
import androidx.compose.ui.text.input.PasswordVisualTransformation
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

// 🌟 สร้าง Screen ครอบรับค่า Token มาจากหน้าสอง
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

        val snackbarHostState = remember { SnackbarHostState() }

        // 🌟 ถ้าเปลี่ยนรหัสผ่านสำเร็จ ให้เด้งกลับไปหน้า Login
        LaunchedEffect(isPasswordReset) {
            if (isPasswordReset) {
                screenModel.clearFlags()
                navigator.popUntilRoot() // เด้งกลับไปหน้า Login
            }
        }

        LaunchedEffect(errorMessage) {
            errorMessage?.let { msg ->
                snackbarHostState.showSnackbar(message = msg)
                screenModel.resetState()
            }
        }

        Box(modifier = Modifier.fillMaxSize()) {
            ResetPasswordContent(
                newPassword = newPassword,
                onNewPasswordChange = { newPassword = it },
                confirmPassword = confirmPassword,
                onConfirmPasswordChange = { confirmPassword = it },
                onBackClick = { navigator.pop() },
                onSubmitClick = { screenModel.resetPassword(token, newPassword, confirmPassword) } // 🌟 ส่ง Token ไปใช้
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
fun ResetPasswordContent(newPassword: String, onNewPasswordChange: (String) -> Unit, confirmPassword: String, onConfirmPasswordChange: (String) -> Unit, onBackClick: () -> Unit, onSubmitClick: () -> Unit) {
    WavyBackground {
        Column(modifier = Modifier.fillMaxSize().statusBarsPadding().padding(24.dp)) {
            Icon(painter = painterResource(Res.drawable.ic_common_back), contentDescription = "Back", tint = LightPrimary, modifier = Modifier.size(24.dp).clickable { onBackClick() })
            Spacer(modifier = Modifier.height(40.dp))
            Text(text = "ตั้งรหัสผ่านใหม่", color = LightPrimary, fontSize = 28.sp, modifier = Modifier.padding(horizontal = 8.dp))
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = "รหัสผ่านใหม่ของคุณต้องแตกต่างจาก\nรหัสผ่านที่เคยใช้งาน", color = LightMuted, fontSize = 14.sp, modifier = Modifier.padding(horizontal = 8.dp))
            Spacer(modifier = Modifier.height(48.dp))

            Column(modifier = Modifier.fillMaxWidth()) {
                Text(text = "รหัสผ่านใหม่", color = LightPrimary, fontSize = 18.sp, modifier = Modifier.padding(bottom = 8.dp, start = 8.dp))
                OutlinedTextField(
                    value = newPassword, onValueChange = onNewPasswordChange, modifier = Modifier.fillMaxWidth().height(56.dp), shape = RoundedCornerShape(percent = 30), singleLine = true,
                    visualTransformation = PasswordVisualTransformation(),
                    colors = OutlinedTextFieldDefaults.colors(focusedContainerColor = LightSurface, unfocusedContainerColor = LightSurface, focusedBorderColor = LightPrimary, unfocusedBorderColor = LightBorder)
                )
            }
            Spacer(modifier = Modifier.height(16.dp))

            Column(modifier = Modifier.fillMaxWidth()) {
                Text(text = "ยืนยันรหัสผ่านใหม่", color = LightPrimary, fontSize = 18.sp, modifier = Modifier.padding(bottom = 8.dp, start = 8.dp))
                OutlinedTextField(
                    value = confirmPassword, onValueChange = onConfirmPasswordChange, modifier = Modifier.fillMaxWidth().height(56.dp), shape = RoundedCornerShape(percent = 30), singleLine = true,
                    visualTransformation = PasswordVisualTransformation(),
                    colors = OutlinedTextFieldDefaults.colors(focusedContainerColor = LightSurface, unfocusedContainerColor = LightSurface, focusedBorderColor = LightPrimary, unfocusedBorderColor = LightBorder)
                )
            }
            Spacer(modifier = Modifier.height(40.dp))

            Button(
                onClick = onSubmitClick, modifier = Modifier.fillMaxWidth().height(56.dp), shape = RoundedCornerShape(percent = 30),
                colors = ButtonDefaults.buttonColors(containerColor = LightPrimary)
            ) {
                Text("เปลี่ยนรหัสผ่าน", fontSize = 18.sp, color = LightSurface)
            }
        }
    }
}