package com.wealthvault.social.ui.share

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
// --- Import ชิ้นส่วนจากโฟลเดอร์ components/space ---
import com.wealthvault.social.ui.components.space.AssetSelectionItem
import com.wealthvault.social.ui.components.space.SpaceTopBar

@Composable
fun AssetShareScreen(
    targetName: String = "Twentytwo",
    onBackClick: () -> Unit,
    onShareConfirm: () -> Unit
) {
    val themeColor = Color(0xFFC27A5A)

    Scaffold(
        containerColor = Color(0xFFFFF8F3) // สีพื้นหลัง
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            SpaceTopBar(title = "แชร์สินทรัพย์", onBackClick = onBackClick)
            HorizontalDivider(color = themeColor.copy(alpha = 0.2f))

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 24.dp)
            ) {
                Spacer(modifier = Modifier.height(20.dp))

                // 1. คำอธิบายหน้าจอ (ปรับเป็นสีเทาให้ดูซอฟต์และอ่านง่ายขึ้น)
                Text(
                    text = "คุณต้องการให้ $targetName เห็นสินทรัพย์อะไรของคุณบ้าง?",
                    fontSize = 14.sp,
                    color = Color.Gray,
                    lineHeight = 22.sp
                )

                Spacer(modifier = Modifier.height(28.dp))

                // 2. หัวข้อหลัก (เพิ่มแถบสีแนวตั้งด้านหน้า ให้ดีไซน์ลิงก์กับหน้า Asset/Debt)
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .width(4.dp)
                            .height(20.dp)
                            .clip(RoundedCornerShape(50))
                            .background(themeColor)
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        text = "เลือกสินทรัพย์ที่ต้องการแชร์",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Medium,
                        color = themeColor
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                // 3. กล่องรายการทรัพย์สินให้เลือก
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    shape = RoundedCornerShape(16.dp),
                    // เพิ่มขอบบางๆ ให้การ์ดดูมีมิติแบบหน้าอื่นๆ
                    border = androidx.compose.foundation.BorderStroke(1.dp, themeColor.copy(alpha = 0.1f))
                ) {
                    LazyColumn(modifier = Modifier.padding(16.dp)) {
                        item { AssetSelectionItem("Apple inc.", "$30032.42", "Stock", Color(0xFF4A90E2), false, themeColor) }
                        item { Spacer(modifier = Modifier.height(8.dp)) }
                        item { AssetSelectionItem("Gold", "$20323.42", "Gold", Color(0xFFF5A623), false, themeColor) }
                        item { Spacer(modifier = Modifier.height(8.dp)) }
                        item { AssetSelectionItem("Cash", "$32093.00", "Cash", Color(0xFF7ED321), true, themeColor) }
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // 4. ปุ่มยืนยันด้านล่าง
                Button(
                    onClick = onShareConfirm,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp),
                    shape = RoundedCornerShape(percent = 30),
                    colors = ButtonDefaults.buttonColors(containerColor = themeColor)
                ) {
                    Text("แบ่งปันข้อมูล", fontSize = 16.sp, color = Color.White, fontWeight = FontWeight.Medium)
                }
                Spacer(modifier = Modifier.height(32.dp))
            }
        }
    }
}