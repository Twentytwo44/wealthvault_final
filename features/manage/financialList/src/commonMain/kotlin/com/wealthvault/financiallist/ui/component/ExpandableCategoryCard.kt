package com.wealthvault.financiallist.ui.component

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
//import androidx.compose.material.icons.Icons
//import androidx.compose.material.icons.filled.KeyboardArrowDown
//import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun ExpandableCategoryCard(
    title: String,
    itemCount: Int,
    themeColor: Color,
    initiallyExpanded: Boolean = false,
    content: @Composable () -> Unit // ใส่การ์ดข้อมูลข้างในตรงนี้
) {
    var expanded by remember { mutableStateOf(initiallyExpanded) }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 16.dp)
    ) {
        // --- ส่วนหัว (กดกาง/หุบได้) ---
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .clickable { expanded = !expanded }
                .padding(vertical = 8.dp)
        ) {
            // แถบสีแนวตั้งด้านซ้าย
            Box(
                modifier = Modifier
                    .width(4.dp)
                    .height(20.dp)
                    .clip(RoundedCornerShape(50))
                    .background(themeColor)
            )

            Spacer(modifier = Modifier.width(12.dp))

            // ชื่อหมวดหมู่
            Text(
                text = title,
                fontSize = 16.sp,
                color = themeColor,
                fontWeight = FontWeight.Medium
            )

            Spacer(modifier = Modifier.weight(1f))

            // จำนวนรายการ และ ไอคอนลูกศร
            Text(
                text = "$itemCount รายการ",
                fontSize = 14.sp,
                color = themeColor.copy(alpha = 0.8f)
            )
            Spacer(modifier = Modifier.width(4.dp))
//            Icon(
//                imageVector = if (expanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
//                contentDescription = null,
//                tint = themeColor.copy(alpha = 0.8f),
//                modifier = Modifier.size(20.dp)
//            )
        }

        // เส้นคั่นแนวนอน
        HorizontalDivider(color = themeColor.copy(alpha = 0.3f), thickness = 1.dp)

        // --- ส่วนเนื้อหา (กาง/หุบ) ---
        AnimatedVisibility(visible = expanded) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp)
            ) {
                content()
            }
        }
    }
}

// Widget กล่องข้อมูลจำลอง (สีขาวขอบมน ตามรูปดีไซน์)
@Composable
fun MockItemCard() {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(72.dp)
            .padding(bottom = 12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFF3E5D8)),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        // พื้นที่ว่างสีขาว สำหรับใส่ข้อมูลจริงในอนาคต
    }
}

// Widget กล่องข้อมูลที่มีตัวหนังสือ (สำหรับหมวด "บัญชีให้ลูก")
@Composable
fun RealItemCard(title: String, bank: String, amount: String) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFF3E5D8)),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = title, fontSize = 16.sp, fontWeight = FontWeight.Medium, color = Color(0xFF3A2F2A))
            Spacer(modifier = Modifier.height(8.dp))
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text(text = "ธนาคาร", fontSize = 14.sp, color = Color.Gray)
                Text(text = bank, fontSize = 14.sp, color = Color(0xFF3A2F2A))
            }
            Spacer(modifier = Modifier.height(4.dp))
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text(text = "มูลค่าทรัพย์สิน", fontSize = 14.sp, color = Color.Gray)
                Text(text = amount, fontSize = 14.sp, color = Color(0xFF3A2F2A))
            }
        }
    }
}