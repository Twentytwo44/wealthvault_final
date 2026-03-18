package com.wealthvault.notification.ui

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
//import androidx.compose.material.icons.Icons
//import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.wealthvault.core.generated.resources.Res
import com.wealthvault.core.generated.resources.ic_common_back

// Import สีจาก Theme ของเรา (เช็ค import ให้ตรงกับโครงสร้างจริงนะครับ)
import com.wealthvault.core.theme.LightPrimary
import com.wealthvault.core.theme.LightSurface
import com.wealthvault.core.theme.LightBorder
import com.wealthvault.core.theme.LightMuted
import com.wealthvault.core.theme.LightBg
import com.wealthvault.core.theme.LightText
import org.jetbrains.compose.resources.painterResource

@Composable
fun NotificationScreen(
    onBackClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(LightBg) // สีพื้นหลังแอป
            .statusBarsPadding()
            .padding(horizontal = 24.dp, vertical = 24.dp)
    ) {
        // --- ส่วนหัว (Header) ---
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            Icon(
                painter = painterResource(Res.drawable.ic_common_back),
                contentDescription = "Back",
                tint = LightPrimary,
                modifier = Modifier.size(24.dp).clickable { onBackClick() }
            )
            Spacer(modifier = Modifier.width(16.dp))
            Text(
                text = "การแจ้งเตือน",
                fontSize = 24.sp,
                color = LightPrimary,
                fontWeight = FontWeight.Medium
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        // --- ส่วนรายการการแจ้งเตือน ---
        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier.fillMaxSize()
        ) {
            // 1. แจ้งเตือนประกัน
            item {
                StandardNotificationCard(
                    title = "ประกัน ... ของคุณใกล้หมดอายุแล้ว",
                    subtitleLeft = "วันหมดอายุ 08 ม.ค. 2580"
                )
            }

            // 2. แจ้งเตือนวันผ่อนชำระ
            item {
                StandardNotificationCard(
                    title = "ใกล้ถึงวันผ่อนชำระ ... แล้ว",
                    subtitleLeft = "วันผ่อนชำระ 08 ม.ค. 2580"
                )
            }

            // 3. แจ้งเตือนการแชร์ (มีข้อมูลขวา)
            item {
                StandardNotificationCard(
                    title = "คุณได้แชร์ทรัพย์สินนี้กับ Twentytwo",
                    subtitleLeft = "ชื่อสินทรัพย์ ...",
                    subtitleRight = "..."
                )
            }

            // 4. แจ้งเตือนการแชร์ (แบบมี 2 บรรทัดย่อย)
            item {
                StandardNotificationCard(
                    title = "ใกล้ถึงเวลาที่คุณจะแชร์ทรัพย์สินนี้กับ Twentytwo แล้ว",
                    subtitleLeft = "ชื่อสินทรัพย์ ...",
                    subtitleRight = "...",
                    secondLineLeft = "วันที่แชร์",
                    secondLineRight = "08 ม.ค. 2580(อีก30วัน)"
                )
            }

            // 5. แจ้งเตือนคำเชิญ (มีปุ่มกด)
            item {
                InviteNotificationCard(
                    title = "มีคำเชิญเข้าร่วมกลุ่ม ... ถึงคุณ",
                    inviter = "Twentytwo",
                    onDeclineClick = { /* TODO: ปฏิเสธ */ },
                    onAcceptClick = { /* TODO: เข้าร่วม */ }
                )
            }
        }
    }
}

// ---------------------------------------------------------
// Component ย่อยสำหรับการ์ดแจ้งเตือนแบบต่างๆ
// ---------------------------------------------------------

/**
 * การ์ดแจ้งเตือนแบบมาตรฐาน (รองรับข้อมูล 1-2 บรรทัด และจัดซ้าย-ขวาได้)
 */
@Composable
fun StandardNotificationCard(
    title: String,
    subtitleLeft: String,
    subtitleRight: String? = null,
    secondLineLeft: String? = null,
    secondLineRight: String? = null
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = LightSurface),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
        border = BorderStroke(1.dp, LightBorder)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // หัวข้อหลัก
            Text(text = title, fontSize = 16.sp, color = LightText)
            Spacer(modifier = Modifier.height(8.dp))

            // บรรทัดย่อยที่ 1
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text(text = subtitleLeft, fontSize = 14.sp, color = LightMuted)
                if (subtitleRight != null) {
                    Text(text = subtitleRight, fontSize = 14.sp, color = LightMuted)
                }
            }

            // บรรทัดย่อยที่ 2 (ถ้ามี)
            if (secondLineLeft != null || secondLineRight != null) {
                Spacer(modifier = Modifier.height(4.dp))
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text(text = secondLineLeft ?: "", fontSize = 14.sp, color = LightMuted)
                    Text(text = secondLineRight ?: "", fontSize = 14.sp, color = LightMuted)
                }
            }
        }
    }
}

/**
 * การ์ดแจ้งเตือนแบบมีปุ่มคำเชิญ (ปฏิเสธ / เข้าร่วม)
 */
@Composable
fun InviteNotificationCard(
    title: String,
    inviter: String,
    onDeclineClick: () -> Unit,
    onAcceptClick: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = LightSurface),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
        border = BorderStroke(1.dp, LightBorder)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // หัวข้อหลัก
            Text(text = title, fontSize = 16.sp, color = LightText)
            Spacer(modifier = Modifier.height(8.dp))

            // ผู้เชิญ
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text(text = "ผู้เชิญ", fontSize = 14.sp, color = LightMuted)
                Text(text = inviter, fontSize = 14.sp, color = LightMuted)
            }

            Spacer(modifier = Modifier.height(16.dp))

            // ปุ่มกด (ชิดขวา)
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                // ปุ่มปฏิเสธ (ขอบส้ม)
                OutlinedButton(
                    onClick = onDeclineClick,
                    border = BorderStroke(1.dp, LightPrimary),
                    shape = RoundedCornerShape(percent = 50),
                    modifier = Modifier.height(40.dp)
                ) {
                    Text("ปฏิเสธ", color = LightPrimary, fontSize = 14.sp)
                }

                Spacer(modifier = Modifier.width(12.dp))

                // ปุ่มเข้าร่วม (พื้นส้ม)
                Button(
                    onClick = onAcceptClick,
                    colors = ButtonDefaults.buttonColors(containerColor = LightPrimary),
                    shape = RoundedCornerShape(percent = 50),
                    modifier = Modifier.height(40.dp)
                ) {
                    Text("เข้าร่วม", color = Color.White, fontSize = 14.sp)
                }
            }
        }
    }
}