package com.wealthvault.login.ui

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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.example.login.ui.LoginScreenModel
import com.wealthvault.core.theme.LightBorder
import com.wealthvault.core.theme.LightMuted
import com.wealthvault.core.theme.LightPrimary
import com.wealthvault.core.theme.LightSurface
import com.wealthvault.core.theme.WvBgGradientEnd
import com.wealthvault.core.theme.WvBgGradientStart
import com.wealthvault.core.theme.WvWaveGradientEnd
import com.wealthvault.core.theme.WvWaveGradientStart
import com.wealthvault.core.utils.getScreenModel

class LoginScreen : Screen {
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val screenModel = getScreenModel<LoginScreenModel>()
//
//        val lineAuth = rememberLineAuth(
//            onSuccess = { user ->
//                // เมื่อสำเร็จ โยนกลับไปให้ ScreenModel คิดต่อ
//                screenModel.onLineSuccess(user) {
//                    println("ไปหน้าต่อไปได้เลย!")
//                    // navigator.push(HomeScreen())
//                }
//            },
//            onError = { error ->
//                // เมื่อพัง โยนกลับไปให้ ScreenModel โชว์ Error
//                screenModel.onLineError(error)
//            }
//        )

        // เรียกใช้ Stateless UI ที่เราแยกไว้
        LoginContent(
            username = screenModel.username,
            onUsernameChange = { screenModel.username = it },
            password = screenModel.password,
            onPasswordChange = { screenModel.password = it },
            isLoading = screenModel.isLoading,
            onLoginClick = {
                screenModel.onLoginClick {
                    // จัดการการเปลี่ยนหน้าตรงนี้
                }
            },
            onGoogleClick = {
                screenModel.onGoogleClick {
                }
            },
            onLineClick = {
//                screenModel.onLineClick(lineAuth)
            },
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
    onLoginClick: () -> Unit,
    onGoogleClick: () -> Unit,
    onLineClick:() -> Unit,
) {

    WavyBackground{
        // 👇 เอา Column หลักกลับมาใส่ตรงนี้ครับ 👇
        Column(
            modifier = Modifier
                .fillMaxSize() // ขยายเต็มหน้าจอ
                .padding(24.dp), // เว้นขอบซ้ายขวา
            horizontalAlignment = Alignment.CenterHorizontally, // จัดให้อยู่ตรงกลางแนวนอน
            verticalArrangement = Arrangement.Center // จัดให้อยู่ตรงกลางแนวตั้ง
        ) {

            // ชื่อแอป
            Text(
                text = "Wealth & Vault",
                color = LightPrimary,
                fontSize = 28.sp,
                modifier = Modifier.padding(horizontal = 8.dp)
            )

            // 💡 แนะนำ: ลด Spacer ลงเหลือสัก 80.dp - 100.dp ครับ เพราะ 200.dp อาจจะดันของตกขอบจอล่างได้
            Spacer(modifier = Modifier.height(150.dp))

            // กล่องที่รวม Input และปุ่มต่างๆ
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // 1. ช่องอีเมล
                Column(modifier = Modifier.fillMaxWidth()) {
                    Text(
                        text = "อีเมล",
                        color = LightPrimary,
                        fontSize = 18.sp,
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
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedContainerColor = LightSurface,
                            unfocusedContainerColor = LightSurface,
                            focusedBorderColor = LightPrimary,
                            unfocusedBorderColor = LightBorder,
                        )
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                // 2. ช่องรหัสผ่าน
                Column(modifier = Modifier.fillMaxWidth()) {
                    Text(
                        text = "รหัสผ่าน",
                        color = LightPrimary,
                        fontSize = 18.sp,
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
                        visualTransformation = PasswordVisualTransformation(),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedContainerColor = LightSurface,
                            unfocusedContainerColor = LightSurface,
                            focusedBorderColor = LightPrimary,
                            unfocusedBorderColor = LightBorder,
                        )
                    )
                }

                // ... โค้ดส่วนที่เหลือ (ปุ่มลืมรหัสผ่าน, ปุ่มเข้าสู่ระบบ, ปุ่ม Google) เหมือนเดิมเป๊ะเลยครับ ...

                // 3. ปุ่มลืมรหัสผ่าน
                Text(
                    text = "ลืมรหัสผ่าน",
                    color = LightMuted,
                    fontSize = 16.sp,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 12.dp, end = 16.dp)
                        .clickable { /* TODO: นำทางไปหน้าลืมรหัส */ },
                    textAlign = TextAlign.End
                )

                Spacer(modifier = Modifier.height(32.dp))

                // 4. ปุ่มเข้าสู่ระบบ
                Button(
                    onClick = onLoginClick,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    shape = RoundedCornerShape(percent = 30),
                    colors = ButtonDefaults.buttonColors(containerColor = LightPrimary)
                ) {
                    Text("เข้าสู่ระบบ", fontSize = 18.sp, color = LightSurface)
                }

                Spacer(modifier = Modifier.height(24.dp))

                // 5. ยังไม่มีบัญชี
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(text = "ยังไม่มีบัญชี ", color = LightMuted, fontSize = 14.sp)
                    Text(
                        text = "สร้างบัญชี?",
                        color = LightPrimary,
                        fontSize = 14.sp,
                        textDecoration = TextDecoration.Underline,
                        modifier = Modifier.clickable { /* TODO: นำทางไปหน้าสมัคร */ }
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))

                // 6. เส้นคั่น หรือ
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 32.dp)
                ) {
                    HorizontalDivider(
                        modifier = Modifier.weight(1f),
                        color = LightBorder,
                        thickness = 2.dp
                    )
                    Text(
                        text = " หรือ ",
                        color = LightMuted,
                        fontSize = 14.sp,
                        modifier = Modifier.padding(horizontal = 8.dp)
                    )
                    HorizontalDivider(
                        modifier = Modifier.weight(1f),
                        color = LightBorder,
                        thickness = 2.dp
                    )
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
                    Text("Google", color = LightPrimary, fontSize = 16.sp)
                }

                Spacer(modifier = Modifier.height(24.dp))

                // 7. ปุ่ม Google
                OutlinedButton(
                    onClick = onLineClick,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp)
                        .padding(horizontal = 48.dp),
                    shape = RoundedCornerShape(percent = 30),
                    border = BorderStroke(1.dp, LightBorder),
                    colors = ButtonDefaults.outlinedButtonColors(containerColor = LightSurface)
                ) {
                    Text("Line login", color = LightPrimary, fontSize = 16.sp)
                }
            } // ปิด Column กล่อง Input
        } // ปิด Column หลัก
    } // ปิด WavyBackground
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
