package com.wealthvault.introduction.ui

import androidx.compose.foundation.background
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
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
import cafe.adriel.voyager.core.screen.Screen
import com.wealthvault.core.theme.LightPrimary
import com.wealthvault.core.theme.WvBgGradientEnd
import com.wealthvault.core.theme.WvBgGradientStart
import com.wealthvault.core.theme.WvWaveGradientEnd
import com.wealthvault.core.theme.WvWaveGradientStart
import kotlinx.coroutines.launch


class IntroScreen() : Screen {
    @Composable
    override fun Content(){
        IntroContent(
            onBackClick = {},
            onFinish = {

            }
        )
    }
}
@Composable
fun IntroContent(
    onBackClick: () -> Unit, // เพิ่มปุ่มย้อนกลับให้เผื่อใช้
    onFinish: () -> Unit // เมื่อกดปุ่ม ต่อไป ในหน้าสุดท้าย (หน้า 4)
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

    // ตัวจัดการหน้า (เหลือ 4 หน้าถ้วน)
    val pagerState = rememberPagerState(pageCount = { 4 })
    val coroutineScope = rememberCoroutineScope()

    WavyBackground {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(vertical = 24.dp),
        ) {
            // --- แถบบนสุด: ปุ่มย้อนกลับ ---
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // ถ้ามี Icon ย้อนกลับ สามารถเปิดคอมเมนต์มาใช้ได้เลยครับ
                // Icon(
                //     imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                //     contentDescription = "Back",
                //     tint = LightPrimary,
                //     modifier = Modifier
                //         .size(28.dp)
                //         .clickable { onBackClick() }
                // )
                Spacer(modifier = Modifier.weight(1f))
            }

            Spacer(modifier = Modifier.height(16.dp))

            // --- ส่วนเนื้อหาแบบเลื่อนได้ (Pager) ---
            HorizontalPager(
                state = pagerState,
                modifier = Modifier.weight(1f) // กินพื้นที่ตรงกลางทั้งหมด
            ) { page ->
                // หน้า 1-4: แสดงข้อมูลและรูปภาพ
                IntroPageScreen(pageData = introPages[page], primaryColor = LightPrimary)
            }

            // --- จุดบอกตำแหน่งหน้า (Indicators) ---
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 16.dp),
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

            // --- ปุ่ม ต่อไป ---
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
            ) {
                Button(
                    onClick = {
                        if (pagerState.currentPage < 3) {
                            // ถ้ายังไม่ถึงหน้าสุดท้าย เลื่อนไปหน้าถัดไป
                            coroutineScope.launch {
                                pagerState.animateScrollToPage(pagerState.currentPage + 1)
                            }
                        } else {
                            // หน้าสุดท้ายแล้ว (หน้า 4) ทำงานคำสั่ง onFinish ไปยังหน้าต่อไป
                            onFinish()
                        }
                    },
                    modifier = Modifier
                        .align(Alignment.End) // ชิดขวาสุดของหน้าจอ
                        .width(140.dp)
                        .height(50.dp),
                    shape = RoundedCornerShape(percent = 30),
                    colors = ButtonDefaults.buttonColors(containerColor = LightPrimary)
                ) {
                    Text("ต่อไป", fontSize = 18.sp, color = Color.White)
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
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally

    ) {
        Text(text = pageData.title, fontSize = 28.sp, fontWeight = FontWeight.Medium, color = primaryColor)
        Spacer(modifier = Modifier.height(16.dp))
        Text(text = pageData.description, fontSize = 14.sp, color = primaryColor.copy(alpha = 0.8f), textAlign = TextAlign.Center)

        Spacer(modifier = Modifier.height(48.dp))

        // Box รูปภาพประกอบ
        Box(
            modifier = Modifier
                .size(width = 250.dp, height = 450.dp)
                .background(Color(0xFFFEE0BB), shape = RoundedCornerShape(16.dp)),
            contentAlignment = Alignment.Center
        ) {
            // ใส่ภาพ Illustration ตรงนี้
        }
    }
}

@Composable
fun WavyBackground(
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
            .background(bottomBgBrush)
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
                drawPath(path = path, brush = topWaveBrush)
            }
    ) {
        content()
    }
}
