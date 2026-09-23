package com.wealthvault.register.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import com.wealthvault.core.generated.resources.Res
import com.wealthvault.core.generated.resources.ic_auth_email
import com.wealthvault.core.generated.resources.ic_auth_lock
import com.wealthvault.core.theme.LightPrimary
import com.wealthvault.core.theme.LightMuted
import com.wealthvault.core.theme.LightSurface
import com.wealthvault.core.theme.RedErr

@Composable
internal fun RegisterForm(
    username: String,
    onUsernameChange: (String) -> Unit,
    password: String,
    onPasswordChange: (String) -> Unit,
    confirmPassword: String,
    onConfirmPasswordChange: (String) -> Unit,
    isLoading: Boolean,
    errorMessage: String?,
    onRegisterClick: () -> Unit,
    onLoginClick: () -> Unit,
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 8.dp, start = 8.dp, end = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Bottom,
            ) {
                Text(text = "อีเมล", color = LightPrimary, style = MaterialTheme.typography.bodyLarge)
                errorMessage?.let { message ->
                    Text(
                        text = message,
                        color = RedErr,
                        style = MaterialTheme.typography.labelMedium,
                        textAlign = TextAlign.End,
                        modifier = Modifier.weight(1f).padding(start = 8.dp),
                    )
                }
            }
            CustomRegisterTextField(
                value = username,
                onValueChange = onUsernameChange,
                placeholder = "อีเมล",
                leadingIcon = Res.drawable.ic_auth_email,
            )
        }

        Spacer(modifier = Modifier.height(14.dp))
        var isPasswordVisible by remember { mutableStateOf(false) }
        Column(modifier = Modifier.fillMaxWidth()) {
            Text(
                text = "รหัสผ่าน",
                color = LightPrimary,
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.padding(bottom = 8.dp, start = 8.dp),
            )
            CustomRegisterTextField(
                value = password,
                onValueChange = onPasswordChange,
                placeholder = "รหัสผ่าน",
                leadingIcon = Res.drawable.ic_auth_lock,
                isPassword = true,
                isPasswordVisible = isPasswordVisible,
                onPasswordVisibleChange = { isPasswordVisible = it },
            )
        }

        Spacer(modifier = Modifier.height(14.dp))
        var isConfirmPasswordVisible by remember { mutableStateOf(false) }
        Column(modifier = Modifier.fillMaxWidth()) {
            Text(
                text = "ยืนยันรหัสผ่าน",
                color = LightPrimary,
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.padding(bottom = 8.dp, start = 8.dp),
            )
            CustomRegisterTextField(
                value = confirmPassword,
                onValueChange = onConfirmPasswordChange,
                placeholder = "ยืนยันรหัสผ่าน",
                leadingIcon = Res.drawable.ic_auth_lock,
                isPassword = true,
                isPasswordVisible = isConfirmPasswordVisible,
                onPasswordVisibleChange = { isConfirmPasswordVisible = it },
            )
        }

        Spacer(modifier = Modifier.height(36.dp))
        Button(
            onClick = onRegisterClick,
            enabled = !isLoading,
            modifier = Modifier.fillMaxWidth().height(46.dp),
            shape = RoundedCornerShape(percent = 30),
            colors = ButtonDefaults.buttonColors(containerColor = LightPrimary),
        ) {
            Text("สร้างบัญชี", style = MaterialTheme.typography.bodyLarge, color = LightSurface)
        }

        Spacer(modifier = Modifier.height(20.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(text = "มีบัญชีอยู่แล้ว ", color = LightMuted, style = MaterialTheme.typography.bodyMedium)
            Text(
                text = "เข้าสู่ระบบ?",
                color = LightPrimary,
                style = MaterialTheme.typography.bodyMedium,
                textDecoration = TextDecoration.Underline,
                modifier = Modifier.clickable(onClick = onLoginClick),
            )
        }
        Spacer(modifier = Modifier.height(24.dp))
    }
}
