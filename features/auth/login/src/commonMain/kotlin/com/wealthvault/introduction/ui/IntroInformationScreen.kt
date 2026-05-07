package com.wealthvault.introduction.ui // ถ้าอยากย้าย package ไปโฟลเดอร์ ic_nav_profile ก็เปลี่ยนตรงนี้ได้นะครับ

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cafe.adriel.voyager.core.screen.Screen
import com.wealthvault.core.generated.resources.Res
import com.wealthvault.core.generated.resources.ic_common_back
import com.wealthvault.core.theme.LightBorder
import com.wealthvault.core.theme.LightPrimary
import com.wealthvault.core.theme.LightSurface
import com.wealthvault.core.theme.WvBgGradientStart
import org.jetbrains.compose.resources.painterResource

class IntroQuestionScreen() : Screen {
    @Composable
    override fun Content(){
        IntroQuestionContent(
            firstName = "",
            onFirstNameChange = {},
            lastName = "",
            onLastNameChange = {},
            phoneNum = "",
            onPhoneNumChange = {},
            dob = "",
            onDobChange = {},
            onBackClick = {},
            onNextClick = {}
        )
    }
}

@Composable
fun IntroQuestionContent(
    firstName: String,
    onFirstNameChange: (String) -> Unit,
    lastName: String,
    onLastNameChange: (String) -> Unit,
    phoneNum: String,
    onPhoneNumChange: (String) -> Unit,
    dob: String, // วันเกิด
    onDobChange: (String) -> Unit,
    onBackClick: () -> Unit, // ปุ่มย้อนกลับ
    onNextClick: () -> Unit // เมื่อกดปุ่ม ต่อไป
) {
    WavyBackground {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(vertical = 24.dp)
        ) {
            // --- แถบบนสุด: ปุ่มย้อนกลับ ---
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // 🌟 ใส่ Icon ย้อนกลับให้เหมือนหน้าอื่นๆ
                Icon(
                    painter = painterResource(Res.drawable.ic_common_back),
                    contentDescription = "Back",
                    tint = LightPrimary,
                    modifier = Modifier.size(24.dp).clickable { onBackClick() }
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "ย้อนกลับ",
                    color = LightPrimary,
                    fontSize = 16.sp,
                    modifier = Modifier.clickable { onBackClick() }
                )
                Spacer(modifier = Modifier.weight(1f))
            }

            Spacer(modifier = Modifier.height(16.dp))

            // --- ส่วนเนื้อหาฟอร์ม ---
            Column(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp)
                    .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "ข้อมูลส่วนตัว",
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Medium,
                    color = LightPrimary
                )

                Spacer(modifier = Modifier.height(32.dp))

                // รูปโปรไฟล์
                Box(contentAlignment = Alignment.BottomEnd) {
                    Box(
                        modifier = Modifier
                            .size(100.dp)
                            .clip(CircleShape)
                            .border(2.dp, LightPrimary, CircleShape)
                            .background(WvBgGradientStart),
                        contentAlignment = Alignment.Center
                    ) {
                        // TODO: ใส่รูปโปรไฟล์ หรือไอคอนคน
                    }
                    // ไอคอนแก้ไขรูปโปรไฟล์
                    Box(
                        modifier = Modifier
                            .size(28.dp)
                            .clip(CircleShape)
                            .background(LightPrimary)
                            .clickable { /* TODO: เลือกรูป */ },
                        contentAlignment = Alignment.Center
                    ) {
                        // TODO: ใส่ไอคอนดินสอ
                    }
                }

                Spacer(modifier = Modifier.height(40.dp))

                // 🌟 เปลี่ยนมาเรียกใช้ InputField ตัวใหม่
                InputField(label = "ชื่อจริง", value = firstName, onValueChange = onFirstNameChange, primaryColor = LightPrimary, inputBgColor = LightSurface, borderColor = LightBorder)
                Spacer(modifier = Modifier.height(16.dp))

                InputField(label = "นามสกุล", value = lastName, onValueChange = onLastNameChange, primaryColor = LightPrimary, inputBgColor = LightSurface, borderColor = LightBorder)
                Spacer(modifier = Modifier.height(16.dp))

                InputField(label = "วันเกิด", value = dob, onValueChange = onDobChange, primaryColor = LightPrimary, inputBgColor = LightSurface, borderColor = LightBorder)
                Spacer(modifier = Modifier.height(16.dp))

                InputField(label = "เบอร์โทร", value = phoneNum, onValueChange = onPhoneNumChange, primaryColor = LightPrimary, inputBgColor = LightSurface, borderColor = LightBorder)

                Spacer(modifier = Modifier.height(32.dp))
            }

            // --- ปุ่ม ต่อไป (ยึดติดขอบล่างเสมอ) ---
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp),
            ) {
                Button(
                    onClick = onNextClick,
                    modifier = Modifier
                        .align(Alignment.End)
                        .width(140.dp)
                        .height(50.dp),
                    shape = RoundedCornerShape(percent = 30),
                    colors = ButtonDefaults.buttonColors(containerColor = LightPrimary)
                ) {
                    Text("ต่อไป", fontSize = 18.sp, color = Color.White)
                }
            }
        }
    }
}

// 🌟 อัปเดต Widget ช่วยสร้างช่องกรอกข้อความ เป็น BasicTextField
@Composable
fun InputField(label: String, value: String, onValueChange: (String) -> Unit, primaryColor: Color, inputBgColor: Color, borderColor: Color) {
    // State สำหรับจับว่าช่องนี้กำลังถูกกดพิมพ์อยู่หรือเปล่า (เพื่อเปลี่ยนสีกรอบ)
    var isFocused by remember { mutableStateOf(false) }

    Column(modifier = Modifier.fillMaxWidth()) {
        Text(text = label, color = primaryColor, fontSize = 16.sp, modifier = Modifier.padding(bottom = 8.dp, start = 8.dp))

        BasicTextField(
            value = value,
            onValueChange = onValueChange,
            singleLine = true,
            textStyle = LocalTextStyle.current.copy(color = Color.Black, fontSize = 16.sp),
            cursorBrush = SolidColor(primaryColor),
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp) // 🌟 ล็อกความสูง 50.dp ไม่ให้จม
                .onFocusChanged { isFocused = it.isFocused },
            decorationBox = { innerTextField ->
                Row(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(inputBgColor, RoundedCornerShape(percent = 30))
                        .border(
                            width = 1.dp,
                            color = if (isFocused) primaryColor else borderColor, // 🌟 เปลี่ยนสีกรอบตอนกดพิมพ์
                            shape = RoundedCornerShape(percent = 30)
                        )
                        .padding(horizontal = 16.dp),
                    verticalAlignment = Alignment.CenterVertically // 🌟 จัดตัวหนังสือให้อยู่กึ่งกลางเป๊ะ
                ) {
                    Box(modifier = Modifier.weight(1f)) {
                        innerTextField()
                    }
                }
            }
        )
    }
}