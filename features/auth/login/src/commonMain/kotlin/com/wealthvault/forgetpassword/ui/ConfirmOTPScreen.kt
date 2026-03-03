package com.wealthvault.fogetpassword.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
//import androidx.compose.material.icons.Icons
//import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

import com.wealthvault.core.theme.LightPrimary
import com.wealthvault.core.theme.LightSurface
import com.wealthvault.core.theme.LightBorder
import com.wealthvault.core.theme.LightMuted
import com.wealthvault.core.theme.WvBgGradientEnd
import com.wealthvault.core.theme.WvBgGradientStart
import com.wealthvault.core.theme.WvWaveGradientEnd
import com.wealthvault.core.theme.WvWaveGradientStart

@Composable
fun ConfirmOTPContent(
    emailSentTo: String = "อีเมลของคุณ", // แสดงอีเมลเพื่อย้ำเตือน
    otpCode: String,
    onOtpChange: (String) -> Unit,
    onBackClick: () -> Unit,
    onVerifyClick: () -> Unit,
    onResendClick: () -> Unit
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
                text = "ยืนยันรหัส OTP",
                color = LightPrimary,
                fontSize = 28.sp,
                modifier = Modifier.padding(horizontal = 8.dp)
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "เราได้ส่งรหัส 6 หลักไปที่\n$emailSentTo",
                color = LightMuted,
                fontSize = 14.sp,
                modifier = Modifier.padding(horizontal = 8.dp)
            )

            Spacer(modifier = Modifier.height(48.dp))

            // ช่องกรอก OTP
            OutlinedTextField(
                value = otpCode,
                onValueChange = {
                    // บังคับให้พิมพ์ได้แค่ตัวเลข และไม่เกิน 6 ตัว
                    if (it.length <= 6 && it.all { char -> char.isDigit() }) {
                        onOtpChange(it)
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(64.dp),
                shape = RoundedCornerShape(percent = 30),
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                textStyle = LocalTextStyle.current.copy(
                    textAlign = TextAlign.Center,
                    fontSize = 24.sp,
                    letterSpacing = 16.sp, // เว้นระยะห่างตัวอักษรให้ดูเหมือนช่องๆ
                    fontWeight = FontWeight.Bold
                ),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = LightSurface,
                    unfocusedContainerColor = LightSurface,
                    focusedBorderColor = LightPrimary,
                    unfocusedBorderColor = LightBorder,
                )
            )

            Spacer(modifier = Modifier.height(40.dp))

            Button(
                onClick = onVerifyClick,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(percent = 30),
                colors = ButtonDefaults.buttonColors(containerColor = LightPrimary)
            ) {
                Text("ยืนยัน", fontSize = 18.sp, color = LightSurface)
            }

            Spacer(modifier = Modifier.height(24.dp))

            // ขอรหัสใหม่
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = "ไม่ได้รับรหัส? ", color = LightMuted, fontSize = 14.sp)
                Text(
                    text = "ส่งรหัสอีกครั้ง",
                    color = LightPrimary,
                    fontSize = 14.sp,
                    textDecoration = TextDecoration.Underline,
                    modifier = Modifier.clickable { onResendClick() }
                )
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