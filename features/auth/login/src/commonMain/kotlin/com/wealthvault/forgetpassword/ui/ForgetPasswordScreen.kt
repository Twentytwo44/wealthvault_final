package com.wealthvault.forgetpassword.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
//import androidx.compose.material.icons.Icons
//import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

// Import สี (ปรับ package ให้ตรงกับโปรเจคจริงนะครับ)
import com.wealthvault.core.theme.LightPrimary
import com.wealthvault.core.theme.LightSurface
import com.wealthvault.core.theme.LightBorder
import com.wealthvault.core.theme.LightMuted
import com.wealthvault.core.theme.WvBgGradientEnd
import com.wealthvault.core.theme.WvBgGradientStart
import com.wealthvault.core.theme.WvWaveGradientEnd
import com.wealthvault.core.theme.WvWaveGradientStart

// import WavyBackground มาจากที่ที่คุณเก็บไว้ด้วยนะครับ

@Composable
fun ForgetPasswordContent(
    email: String,
    onEmailChange: (String) -> Unit,
    onBackClick: () -> Unit,
    onSendOtpClick: () -> Unit
) {
    WavyBackground {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp)
        ) {
            // ปุ่มย้อนกลับ
//            Icon(
//                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
//                contentDescription = "Back",
//                tint = LightPrimary,
//                modifier = Modifier
//                    .size(28.dp)
//                    .clickable { onBackClick() }
//            )

            Spacer(modifier = Modifier.height(40.dp))

            // หัวข้อ
            Text(
                text = "ลืมรหัสผ่าน?",
                color = LightPrimary,
                fontSize = 28.sp,
                modifier = Modifier.padding(horizontal = 8.dp)
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "กรุณากรอกอีเมลที่ลงทะเบียนไว้\nเพื่อรับรหัสยืนยัน (OTP)",
                color = LightMuted,
                fontSize = 14.sp,
                modifier = Modifier.padding(horizontal = 8.dp)
            )

            Spacer(modifier = Modifier.height(48.dp))

            // ช่องกรอกอีเมล
            Column(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = "อีเมล",
                    color = LightPrimary,
                    fontSize = 18.sp,
                    modifier = Modifier.padding(bottom = 8.dp, start = 8.dp)
                )
                OutlinedTextField(
                    value = email,
                    onValueChange = onEmailChange,
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

            Spacer(modifier = Modifier.height(40.dp))

            // ปุ่มส่งรหัส
            Button(
                onClick = onSendOtpClick,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(percent = 30),
                colors = ButtonDefaults.buttonColors(containerColor = LightPrimary)
            ) {
                Text("ส่งรหัสยืนยัน", fontSize = 18.sp, color = LightSurface)
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