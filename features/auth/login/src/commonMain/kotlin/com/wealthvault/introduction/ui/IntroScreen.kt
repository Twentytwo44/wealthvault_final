package com.wealthvault.introduction.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
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
import androidx.compose.material3.MaterialTheme
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
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.wealthvault.core.theme.LightPrimary
import com.wealthvault.core.theme.WvBgGradientEnd
import com.wealthvault.core.theme.WvBgGradientStart
import com.wealthvault.core.theme.WvWaveGradientEnd
import com.wealthvault.core.theme.WvWaveGradientStart
import kotlinx.coroutines.launch

// 🌟 Import Res และภาพ โลโก้
import com.wealthvault.core.generated.resources.Res
import com.wealthvault.core.generated.resources.intro1
import com.wealthvault.core.generated.resources.intro2
import com.wealthvault.core.generated.resources.intro3
import com.wealthvault.core.generated.resources.intro4
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.painterResource
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.draw.drawWithContent
class IntroScreen() : Screen {
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow

        IntroContent(
            onBackClick = {},
            onFinish = {
                navigator.push(IntroQuestionScreen())
            }
        )
    }
}

@Composable
fun IntroContent(
    onBackClick: () -> Unit,
    onFinish: () -> Unit
) {
    // 🌟 เพิ่ม DrawableResource เข้าไปใน Data Class สำหรับแต่ละหน้า
    val introPages = listOf(
        IntroPageData("ทรัพย์สิน", "บันทึกทรัพย์สินที่คุณมีได้หลากหลายประเภท", Res.drawable.intro1),
        IntroPageData("รวบรวม", "รวบรวมทรัพย์สินและหนี้สิน\nของคุณไว้ในที่เดียวกัน", Res.drawable.intro2),
        IntroPageData("จัดการ", "ดูภาพรวมของทรัพย์สินเพื่อจัดการ\nทรัพย์สินและผู้ที่เกี่ยวข้อง", Res.drawable.intro3),
        IntroPageData("แบ่งปัน & ส่งต่อ", "แบ่งปันทรัพย์สินของคุณให้ครอบครัว\nและคนที่คุณรักได้ทราบ", Res.drawable.intro4)
    )

    val pagerState = rememberPagerState(pageCount = { 4 })
    val coroutineScope = rememberCoroutineScope()

    WavyBackground {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(vertical = 24.dp),
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Spacer(modifier = Modifier.weight(1f))
            }

            Spacer(modifier = Modifier.height(16.dp))

            HorizontalPager(
                state = pagerState,
                modifier = Modifier.weight(1f)
            ) { page ->
                IntroPageScreen(pageData = introPages[page], primaryColor = LightPrimary)
            }

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

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp, vertical = 16.dp),
            ) {
                Button(
                    onClick = {
                        if (pagerState.currentPage < 3) {
                            coroutineScope.launch {
                                pagerState.animateScrollToPage(pagerState.currentPage + 1)
                            }
                        } else {
                            onFinish()
                        }
                    },
                    modifier = Modifier
                        .align(Alignment.End)
                        .width(140.dp)
                        .height(46.dp),
                    shape = RoundedCornerShape(percent = 30),
                    colors = ButtonDefaults.buttonColors(containerColor = LightPrimary)
                ) {
                    Text("ต่อไป", style = MaterialTheme.typography.bodyLarge, color = Color.White)
                }
            }

            Spacer(modifier = Modifier.height(20.dp))
        }
    }
}

// 🌟 เพิ่มตัวแปร imageRes เพื่อรับภาพเข้ามา
data class IntroPageData(val title: String, val description: String, val imageRes: DrawableResource)

@Composable
fun IntroPageScreen(pageData: IntroPageData, primaryColor: Color) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 24.dp).padding(top = 10.dp),
        horizontalAlignment = Alignment.CenterHorizontally

    ) {
        Text(text = pageData.title, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Medium, color = primaryColor)
        Spacer(modifier = Modifier.height(6.dp))
        Text(text = pageData.description, style = MaterialTheme.typography.bodyLarge, color = primaryColor.copy(alpha = 0.8f), textAlign = TextAlign.Center)

        Spacer(modifier = Modifier.height(24.dp))

        // 🌟 แก้ไขรูปภาพ: กว้างขึ้น, ตัดความสูงล่าง-บน, และทำขอบฟุ้ง (เบลอขอบ)
        Image(
            painter = painterResource(pageData.imageRes),
            contentDescription = "Intro Image",
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight()
                .graphicsLayer { alpha = 0.99f }
                .drawWithContent {
                    drawContent()
                    drawRect(
                        brush = Brush.radialGradient(
                            // 🌟 ใช้ colorStops เพื่อล็อกความชัดให้อยู่ตรงกลางเยอะๆ
                            0.0f to Color.Black,       // จุดศูนย์กลาง (ชัด 100%)
                            0.75f to Color.Black,      // ลากความชัด 100% กว้างออกมาจนถึง 75% ของรูป
                            1.0f to Color.Transparent, // ค่อยเริ่มเบลอให้โปร่งใสในช่วง 25% สุดท้ายตรงขอบรูป
                            radius = size.height * 0.6f // 🌟 เปลี่ยนมาอิงตามความสูงของรูปแทน เพื่อให้คลุมขอบบนล่างได้พอดี
                        ),
                        blendMode = BlendMode.DstIn
                    )
                }
        )
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