package com.wealthvault.fogetpassword.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
//import androidx.compose.material.icons.Icons
//import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

import com.wealthvault.core.theme.LightPrimary
import com.wealthvault.core.theme.LightSurface
import com.wealthvault.core.theme.LightBorder
import com.wealthvault.core.theme.LightMuted

@Composable
fun ResetPasswordContent(
    newPassword: String,
    onNewPasswordChange: (String) -> Unit,
    confirmPassword: String,
    onConfirmPasswordChange: (String) -> Unit,
    onBackClick: () -> Unit,
    onSubmitClick: () -> Unit
) {
    WavyBackground {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp)
        ) {
//            Icon(
//                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
//                contentDescription = "Back",
//                tint = LightPrimary,
//                modifier = Modifier
//                    .size(28.dp)
//                    .clickable { onBackClick() }
//            )

            Spacer(modifier = Modifier.height(40.dp))

            Text(
                text = "ตั้งรหัสผ่านใหม่",
                color = LightPrimary,
                fontSize = 28.sp,
                modifier = Modifier.padding(horizontal = 8.dp)
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "รหัสผ่านใหม่ของคุณต้องแตกต่างจาก\nรหัสผ่านที่เคยใช้งาน",
                color = LightMuted,
                fontSize = 14.sp,
                modifier = Modifier.padding(horizontal = 8.dp)
            )

            Spacer(modifier = Modifier.height(48.dp))

            // ช่อง รหัสผ่านใหม่
            Column(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = "รหัสผ่านใหม่",
                    color = LightPrimary,
                    fontSize = 18.sp,
                    modifier = Modifier.padding(bottom = 8.dp, start = 8.dp)
                )
                OutlinedTextField(
                    value = newPassword,
                    onValueChange = onNewPasswordChange,
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

            // ช่อง ยืนยันรหัสผ่านใหม่
            Column(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = "ยืนยันรหัสผ่านใหม่",
                    color = LightPrimary,
                    fontSize = 18.sp,
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
                    visualTransformation = PasswordVisualTransformation(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = LightSurface,
                        unfocusedContainerColor = LightSurface,
                        focusedBorderColor = LightPrimary,
                        unfocusedBorderColor = LightBorder,
                    )
                )
            }

            Spacer(modifier = Modifier.height(40.dp))

            Button(
                onClick = onSubmitClick,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(percent = 30),
                colors = ButtonDefaults.buttonColors(containerColor = LightPrimary)
            ) {
                Text("เปลี่ยนรหัสผ่าน", fontSize = 18.sp, color = LightSurface)
            }
        }
    }
}