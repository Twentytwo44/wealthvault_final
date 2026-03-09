package com.wealthvault.social.ui.components.space

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.launch
import kotlin.math.roundToInt

@Composable
fun SharedAssetItem(
    name: String,
    tag: String,
    showDelete: Boolean,
    themeColor: Color = Color(0xFFC27A5A)
) {
    // 1. กำหนดความกว้างของปุ่มลบ
    val buttonWidthDp = 72.dp
    val density = LocalDensity.current
    val maxSwipePx = with(density) { buttonWidthDp.toPx() }

    // 2. ตัวแปรสำหรับจับการปัดนิ้ว
    val offsetX = remember { Animatable(0f) }
    val coroutineScope = rememberCoroutineScope()

    Box(
        modifier = Modifier
            .fillMaxWidth()

    ) {
        // --- กล่องหลัก (ที่จะขยับซ้ายขวา) ---
        Box(
            modifier = Modifier
                .fillMaxWidth()
                // เลื่อนตำแหน่ง X ตามนิ้วที่ปัด
                .offset { IntOffset(offsetX.value.roundToInt(), 0) }
                .then(
                    if (showDelete) {
                        Modifier.pointerInput(Unit) {
                            detectHorizontalDragGestures(
                                onDragEnd = {
                                    coroutineScope.launch {
                                        // ปล่อยนิ้ว: ถ้าปัดเกินครึ่งนึง ให้เปิดจนสุด, ถ้าไม่ถึง ให้เด้งปิด
                                        if (offsetX.value < -maxSwipePx / 2) {
                                            offsetX.animateTo(-maxSwipePx, tween(300))
                                        } else {
                                            offsetX.animateTo(0f, tween(300))
                                        }
                                    }
                                },
                                onDragCancel = {
                                    coroutineScope.launch { offsetX.animateTo(0f, tween(300)) }
                                },
                                onHorizontalDrag = { change, dragAmount ->
                                    change.consume()
                                    coroutineScope.launch {
                                        // บังคับไม่ให้ปัดเกินความกว้างของปุ่มลบ (หยุดกึกพอดี)
                                        val newOffset = (offsetX.value + dragAmount).coerceIn(-maxSwipePx, 0f)
                                        offsetX.snapTo(newOffset)
                                    }
                                }
                            )
                        }
                    } else Modifier
                )
        ) {
            // --- 1. เนื้อหา Asset ปกติ ---
            // (ใช้โค้ดเดิมของคุณ Champ เลยครับ)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 24.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .padding(vertical = 6.dp)
                        .size(40.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(Color(0xFFEFEBE9))
                )
                Spacer(modifier = Modifier.width(12.dp))
                Text(text = name, fontSize = 14.sp, color = Color(0xFF3A2F2A), modifier = Modifier.weight(1f))
                Surface(
                    color = Color(0xFF3498DB),
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier.padding(end = 24.dp)
                ) {
                    Text(text = tag, color = Color.White, fontSize = 10.sp, modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp))
                }
            }

            // --- 2. ปุ่มลบ ที่นำไปต่อท้ายด้านขวานอกจอ ---
            if (showDelete) {
                Box(modifier = Modifier.matchParentSize()) { // บังคับให้สูงเท่ากับเนื้อหา Asset พอดี
                    Surface(
                        color = Color(0xFFE74C3C),
                        // ทำขอบมนเฉพาะฝั่งขวา
                        shape = RoundedCornerShape(topEnd = 8.dp, bottomEnd = 8.dp),
                        modifier = Modifier
                            .align(Alignment.CenterEnd)
                            // 🌟 พระเอกอยู่ตรงนี้: ดันปุ่มออกไปด้านขวาจนพ้นหน้าจอ 🌟
                            .offset(x = buttonWidthDp)
                            .width(buttonWidthDp)
                            .fillMaxHeight()
                            .clickable {
                                // TODO: ใส่คำสั่งการลบตรงนี้
                                coroutineScope.launch {
                                    offsetX.animateTo(0f, tween(300)) // กดเสร็จเด้งกลับ
                                }
                            }
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Text(text = "ลบ", color = Color.White, fontSize = 12.sp)
                        }
                    }
                }
            }
        }
    }
}