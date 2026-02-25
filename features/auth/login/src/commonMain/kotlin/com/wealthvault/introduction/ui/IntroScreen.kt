package com.wealthvault.introduction.ui // ปรับ package ให้ตรงกับโปรเจค

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
//import androidx.compose.material.icons.Icons
//import androidx.compose.material.icons.automirrored.filled.ArrowBack
//import androidx.compose.material.icons.automirrored.filled.ArrowForward
//import androidx.compose.material.icons.filled.DateRange
//import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.wealthvault.core.theme.WvBgGradientEnd
import com.wealthvault.core.theme.WvBgGradientStart
import com.wealthvault.core.theme.WvWaveGradientEnd
import com.wealthvault.core.theme.WvWaveGradientStart
import kotlinx.coroutines.launch
import com.wealthvault.core.theme.LightPrimary
import com.wealthvault.core.theme.LightSurface
import com.wealthvault.core.theme.LightBorder
import com.wealthvault.core.theme.LightMuted
import com.wealthvault.core.theme.WvBgGradientStart
import com.wealthvault.core.theme.WvBgGradientEnd

@Composable
fun IntroContent(
    firstName: String,
    onFirstNameChange: (String) -> Unit,
    lastName: String,
    onLastNameChange: (String) -> Unit,
    phoneNum: String,
    onPhoneNumChange: (String) -> Unit,
    dob: String, // Date of Birth (วันเกิด)
    onDobChange: (String) -> Unit,
    onFinish: () -> Unit // เมื่อกดปุ่ม ต่อไป ในหน้าสุดท้าย
) {

    // ข้อมูลสำหรับ 4 หน้าแรก
    val introPages = listOf(
        IntroPageData("ทรัพย์สิน", "บันทึกทรัพย์สินที่คุณมีได้หลากหลายประเภท"),
        IntroPageData("รวบรวม", "รวบรวมทรัพย์สินและหนี้สิน\nของคุณไว้ในที่เดียวกัน"),
        IntroPageData("จัดการ", "ดูภาพรวมของทรัพย์สินเพื่อจัดการ\nทรัพย์สินและผู้ที่เกี่ยวข้อง"),
        IntroPageData(
            "แบ่งปัน & ส่งต่อ",
            "แบ่งปันทรัพย์สินของคุณให้ครอบครัว\nและคนที่คุณรักได้ทราบ"
        )
    )

    // ตัวจัดการหน้า (มีทั้งหมด 5 หน้า: Info 4 + Form 1)
    val pagerState = rememberPagerState(pageCount = { 5 })
    val coroutineScope = rememberCoroutineScope()

    WavyBackground{
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(vertical = 24.dp),
        ) {
            // --- แถบบนสุด: ปุ่มย้อนกลับ ---
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                //            Icon(
                //                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                //                contentDescription = "Back",
                //                tint = primaryColor,
                //                modifier = Modifier
                //                    .size(28.dp)
                //                    .clickable {
                //                        // ถ้าไม่ได้อยู่หน้าแรก ให้กดย้อนกลับได้
                //                        if (pagerState.currentPage > 0) {
                //                            coroutineScope.launch {
                //                                pagerState.animateScrollToPage(pagerState.currentPage - 1)
                //                            }
                //                        }
                //                    }
                //            )
                // พื้นที่ว่างเพื่อให้ไอคอนอยู่ซ้ายสุด
                Spacer(modifier = Modifier.weight(1f))
            }

            Spacer(modifier = Modifier.height(16.dp))

            // --- ส่วนเนื้อหาแบบเลื่อนได้ (Pager) ---
            HorizontalPager(
                state = pagerState,
                modifier = Modifier.weight(1f) // กินพื้นที่ตรงกลางทั้งหมด
            ) { page ->
                if (page < 4) {
                    // หน้า 1-4: แสดงข้อมูลและรูปภาพ
                    IntroPageScreen(pageData = introPages[page], primaryColor = LightPrimary)
                } else {
                    // หน้า 5: แบบฟอร์มข้อมูลส่วนตัว
                    ProfileFormScreen(
                        firstName = firstName,
                        onFirstNameChange = onFirstNameChange,
                        lastName = lastName,
                        onLastNameChange = onLastNameChange,
                        dob = dob,
                        onDobChange = onDobChange,
                        phoneNum = phoneNum,
                        onPhoneNumChange = onPhoneNumChange,
                        primaryColor = LightPrimary,
                        inputBgColor = LightSurface,
                        borderColor = LightBorder
                    )
                }
            }

            // --- จุดบอกตำแหน่งหน้า (Indicators) ---
            if (pagerState.currentPage < 4) {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(vertical = 16.dp),
                    horizontalArrangement = Arrangement.Center
                ) {
                    repeat(4) { iteration ->
                        val color =
                            if (pagerState.currentPage == iteration) LightPrimary else Color.LightGray
                        Box(
                            modifier = Modifier
                                .padding(horizontal = 4.dp)
                                .size(8.dp)
                                .clip(CircleShape)
                                .background(color)
                        )
                    }
                }
            } else {
                // หน้าฟอร์มไม่มีจุด ให้เว้นที่ว่างไว้
                Spacer(modifier = Modifier.height(40.dp))
            }

            // --- ปุ่ม ต่อไป ---
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
            ) {
                Button(
                    onClick = {
                        if (pagerState.currentPage < 4) {
                            // เลื่อนไปหน้าถัดไป
                            coroutineScope.launch {
                                pagerState.animateScrollToPage(pagerState.currentPage + 1)
                            }
                        } else {
                            // หน้าสุดท้ายแล้ว ทำงานคำสั่ง onFinish
                            onFinish()
                        }
                    },
                    modifier = Modifier
                        .align(Alignment.End) // 👈 ดันปุ่มไปชิดขวาสุดของหน้าจอ
                        .width(140.dp) // 👈 กำหนดความกว้างให้สั้นลง (ปรับตัวเลขให้ยาว/สั้นได้ตามใจชอบครับ)
                        .height(50.dp),
                    shape = RoundedCornerShape(percent = 30),
                    colors = ButtonDefaults.buttonColors(containerColor = LightPrimary)
                ) {
                    Text("ต่อไป", fontSize = 18.sp, color = Color.White)
                    // Spacer(modifier = Modifier.width(8.dp))
                    // Icon(imageVector = Icons.AutoMirrored.Filled.ArrowForward, contentDescription = "Next", tint = Color.White, modifier = Modifier.size(20.dp))
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

// Data Class สำหรับเก็บข้อความหน้า Intro
data class IntroPageData(val title: String, val description: String)

// Composable สำหรับหน้าให้ข้อมูล 1-4
@Composable
fun IntroPageScreen(pageData: IntroPageData, primaryColor: Color) {
    Column(
        modifier = Modifier.fillMaxSize()
            .padding(horizontal = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally

    ) {
        Text(text = pageData.title, fontSize = 28.sp, fontWeight = FontWeight.Medium, color = primaryColor)
        Spacer(modifier = Modifier.height(16.dp))
        Text(text = pageData.description, fontSize = 14.sp, color = primaryColor.copy(alpha = 0.8f), textAlign = TextAlign.Center)

        Spacer(modifier = Modifier.height(48.dp))

        // TODO: เปลี่ยน Box นี้เป็นรูปภาพ Illustration ของจริง
        Box(
            modifier = Modifier
                .size(width = 250.dp, height = 450.dp)
                .background(Color(0xFFFEE0BB), shape = RoundedCornerShape(16.dp)),
            contentAlignment = Alignment.Center
        ) {

        }
    }
}

// Composable สำหรับหน้า 5 (แบบฟอร์มข้อมูลส่วนตัว)
@Composable
fun ProfileFormScreen(
    firstName: String, onFirstNameChange: (String) -> Unit,
    lastName: String, onLastNameChange: (String) -> Unit,
    dob: String, onDobChange: (String) -> Unit,
    phoneNum: String, onPhoneNumChange: (String) -> Unit,
    primaryColor: Color, inputBgColor: Color, borderColor: Color
) {
    Column(
        modifier = Modifier.fillMaxSize()
            .padding(horizontal = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = "ข้อมูลส่วนตัว", fontSize = 28.sp, fontWeight = FontWeight.Medium, color = primaryColor)

        Spacer(modifier = Modifier.height(32.dp))

        // รูปโปรไฟล์
        Box(contentAlignment = Alignment.BottomEnd) {
            Box(
                modifier = Modifier
                    .size(100.dp)
                    .clip(CircleShape)
                    .border(2.dp, primaryColor, CircleShape)
                    .background(WvBgGradientStart),
                contentAlignment = Alignment.Center
            ) {
                // TODO: ใส่รูปโปรไฟล์ หรือไอคอนคน
//                Icon(imageVector = Icons.Default.Edit, contentDescription = "Profile", tint = primaryColor.copy(alpha = 0.3f), modifier = Modifier.size(40.dp))
            }
            // ไอคอนแก้ไขรูปโปรไฟล์
            Box(
                modifier = Modifier
                    .size(28.dp)
                    .clip(CircleShape)
                    .background(primaryColor)
                    .clickable { /* TODO: เลือกรูป */ },
                contentAlignment = Alignment.Center
            ) {
//                Icon(imageVector = Icons.Default.Edit, contentDescription = "Edit", tint = Color.White, modifier = Modifier.size(16.dp))
            }
        }

        Spacer(modifier = Modifier.height(40.dp))

        // ช่องกรอก ชื่อจริง
        InputField(label = "ชื่อจริง", value = firstName, onValueChange = onFirstNameChange, primaryColor = primaryColor, inputBgColor = inputBgColor, borderColor = borderColor)
        Spacer(modifier = Modifier.height(16.dp))

        // ช่องกรอก นามสกุล
        InputField(label = "นามสกุล", value = lastName, onValueChange = onLastNameChange, primaryColor = primaryColor, inputBgColor = inputBgColor, borderColor = borderColor)
        Spacer(modifier = Modifier.height(16.dp))

        // ช่องกรอก วันเกิด
        Column(modifier = Modifier.fillMaxWidth()) {
            Text(text = "วันเกิด", color = primaryColor, fontSize = 16.sp, modifier = Modifier.padding(bottom = 8.dp, start = 8.dp))
            OutlinedTextField(
                value = dob,
                onValueChange = onDobChange,
                modifier = Modifier.fillMaxWidth().height(56.dp),
                shape = RoundedCornerShape(percent = 30),
                singleLine = true,
                trailingIcon = {
//                    Icon(imageVector = Icons.Default.DateRange, contentDescription = "Calendar", tint = primaryColor)
                },
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = inputBgColor, unfocusedContainerColor = inputBgColor,
                    focusedBorderColor = primaryColor, unfocusedBorderColor = borderColor,
                )
            )
        }
        Spacer(modifier = Modifier.height(16.dp))
        InputField(label = "เบอร์โทร", value = phoneNum, onValueChange = onPhoneNumChange, primaryColor = primaryColor, inputBgColor = inputBgColor, borderColor = borderColor)
        Spacer(modifier = Modifier.height(16.dp))
    }
}

// Widget ช่วยสร้างช่องกรอกข้อความ (จะได้โค้ดไม่ซ้ำซ้อน)
@Composable
fun InputField(label: String, value: String, onValueChange: (String) -> Unit, primaryColor: Color, inputBgColor: Color, borderColor: Color) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(text = label, color = primaryColor, fontSize = 16.sp, modifier = Modifier.padding(bottom = 8.dp, start = 8.dp))
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            modifier = Modifier.fillMaxWidth().height(56.dp),
            shape = RoundedCornerShape(percent = 30),
            singleLine = true,
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = inputBgColor, unfocusedContainerColor = inputBgColor,
                focusedBorderColor = primaryColor, unfocusedBorderColor = borderColor,
            )
        )
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