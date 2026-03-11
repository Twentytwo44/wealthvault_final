package com.wealthvault.profile.ui.components


import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun ClosePersonItem(
    name: String,
    showDelete: Boolean = false,
    onDeleteClick: () -> Unit = {}
) {
    // ถ้ามีปุ่มลบ (หน้าแก้ไข) ให้พื้นหลังสีขาว ถ้าไม่มี (หน้าดูเฉยๆ) ให้พื้นหลังสีเทาอ่อน
    val bgColor = if (showDelete) Color.White else Color(0xFFF5F5F5)

    Surface(
        color = bgColor,
        shape = RoundedCornerShape(16.dp),
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 12.dp),
        shadowElevation = if (showDelete) 2.dp else 0.dp // เพิ่มเงาบางๆ ในหน้าแก้ไข
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // รูปโปรไฟล์คนใกล้ชิด (กล่องสีเทา)
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(Color(0xFFE0DCDA))
            )

            Spacer(modifier = Modifier.width(16.dp))

            // ชื่อ
            Text(
                text = name,
                fontSize = 14.sp,
                color = Color(0xFF3A2F2A),
                modifier = Modifier.weight(1f)
            )

            // ปุ่มลบ (แสดงเฉพาะตอน showDelete = true)
            if (showDelete) {
                Text(
                    text = "ลบ",
                    fontSize = 14.sp,
                    color = Color(0xFFE74C3C),
                    modifier = Modifier
                        .clickable { onDeleteClick() }
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                )
            }
        }
    }
}