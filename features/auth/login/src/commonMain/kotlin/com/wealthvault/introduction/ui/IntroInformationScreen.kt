package com.wealthvault.introduction.ui // ถ้าอยากย้าย package ไปโฟลเดอร์ ic_nav_profile ก็เปลี่ยนตรงนี้ได้นะครับ

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

// Import Theme
import com.wealthvault.core.theme.LightPrimary
import com.wealthvault.core.theme.LightSurface
import com.wealthvault.core.theme.LightBorder
import com.wealthvault.core.theme.WvBgGradientStart
import com.wealthvault.core.theme.WvBgGradientEnd
import com.wealthvault.core.theme.WvWaveGradientStart
import com.wealthvault.core.theme.WvWaveGradientEnd

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
                // TODO: ใส่ Icon ย้อนกลับตรงนี้
                Text(
                    text = "< ย้อนกลับ",
                    color = LightPrimary,
                    modifier = Modifier.clickable { onBackClick() }
                )
                Spacer(modifier = Modifier.weight(1f))
            }

            Spacer(modifier = Modifier.height(16.dp))

            // --- ส่วนเนื้อหาฟอร์ม (ใส่ Scroll เผื่อคีย์บอร์ดบังจอ) ---
            Column(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp)
                    .verticalScroll(rememberScrollState()), // 💡 เพิ่ม Scroll ป้องกันคีย์บอร์ดบังปุ่ม
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

                // ช่องกรอกข้อมูลต่างๆ
                InputField(label = "ชื่อจริง", value = firstName, onValueChange = onFirstNameChange, primaryColor = LightPrimary, inputBgColor = LightSurface, borderColor = LightBorder)
                Spacer(modifier = Modifier.height(16.dp))

                InputField(label = "นามสกุล", value = lastName, onValueChange = onLastNameChange, primaryColor = LightPrimary, inputBgColor = LightSurface, borderColor = LightBorder)
                Spacer(modifier = Modifier.height(16.dp))

                Column(modifier = Modifier.fillMaxWidth()) {
                    Text(text = "วันเกิด", color = LightPrimary, fontSize = 16.sp, modifier = Modifier.padding(bottom = 8.dp, start = 8.dp))
                    OutlinedTextField(
                        value = dob,
                        onValueChange = onDobChange,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp),
                        shape = RoundedCornerShape(percent = 30),
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedContainerColor = LightSurface, unfocusedContainerColor = LightSurface,
                            focusedBorderColor = LightPrimary, unfocusedBorderColor = LightBorder,
                        )
                    )
                }

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

// Widget ช่วยสร้างช่องกรอกข้อความ
@Composable
fun InputField(label: String, value: String, onValueChange: (String) -> Unit, primaryColor: Color, inputBgColor: Color, borderColor: Color) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(text = label, color = primaryColor, fontSize = 16.sp, modifier = Modifier.padding(bottom = 8.dp, start = 8.dp))
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            shape = RoundedCornerShape(percent = 30),
            singleLine = true,
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = inputBgColor, unfocusedContainerColor = inputBgColor,
                focusedBorderColor = primaryColor, unfocusedBorderColor = borderColor,
            )
        )
    }
}

