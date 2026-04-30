package com.wealthvault_final.`financial-asset`.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Icon
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Email
import androidx.compose.material.icons.outlined.Group
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

// 🌟 Import Theme กลางของแอป
import com.wealthvault.core.theme.LightBg
import com.wealthvault.core.theme.LightPrimary
import com.wealthvault.core.theme.LightText

@Composable
fun ShareItemCard(
    name: String,
    date: String,
    groupCount: String? = null,
    isEmail: Boolean = false
) {
    // 🌟 เปลี่ยนเป็นสีโปร่งใส (Transparent) เพื่อให้มันเนียนกลืนไปกับ Card แม่ในหน้า Summary
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.Transparent),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {

            // =====================================
            // 🌟 1. ส่วนของ Icon Box (แยกตามประเภท)
            // =====================================
            if (isEmail) {
                // โหมดอีเมล: สี่เหลี่ยมขอบมน ขอบสีส้ม (รูปที่ 2)
                Box(
                    modifier = Modifier
                        .size(46.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color.White)
                        .border(1.dp, LightPrimary.copy(alpha = 0.3f), RoundedCornerShape(12.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Outlined.Email, contentDescription = null, tint = LightPrimary, modifier = Modifier.size(24.dp))
                }
            } else if (groupCount != null) {
                // โหมดกลุ่ม: สี่เหลี่ยมขอบมน สีเทา (รูปที่ 2)
                Box(
                    modifier = Modifier
                        .size(46.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color(0xFFE0E0E0)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Outlined.Group, contentDescription = null, tint = Color.Gray, modifier = Modifier.size(24.dp))
                }
            } else {
                // โหมดเพื่อน: วงกลม (รูปที่ 3)
                Box(
                    modifier = Modifier
                        .size(46.dp)
                        .clip(CircleShape)
                        .background(LightBg),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Outlined.Person, contentDescription = null, tint = LightText.copy(alpha = 0.4f), modifier = Modifier.size(24.dp))
                }
            }

            Spacer(modifier = Modifier.width(16.dp))

            // =====================================
            // 🌟 2. ส่วนของข้อมูลข้อความและป้ายกำกับ
            // =====================================
            Column(modifier = Modifier.weight(1f)) {
                // ชื่อ
                Text(
                    text = name,
                    style = MaterialTheme.typography.bodyLarge,
                    color = LightText,
                    fontWeight = if (isEmail) FontWeight.Normal else FontWeight.Medium
                )

                // Badge สำหรับจำนวนคนในกลุ่ม
                if (groupCount != null) {
                    Spacer(modifier = Modifier.height(6.dp))
                    Row(
                        modifier = Modifier
                            .background(LightBg, RoundedCornerShape(8.dp))
                            .padding(horizontal = 8.dp, vertical = 2.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Outlined.Group, contentDescription = null, tint = LightPrimary, modifier = Modifier.size(12.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = groupCount,
                            style = MaterialTheme.typography.labelSmall,
                            color = LightPrimary,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                // วันที่เข้าถึงข้อมูล (ถ้ามี)
                if (date.isNotBlank()) {
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = if (isEmail) "วันหมดอายุ: $date" else date,
                        style = MaterialTheme.typography.labelMedium,
                        color = Color.Gray
                    )
                }
            }
        }
    }
}