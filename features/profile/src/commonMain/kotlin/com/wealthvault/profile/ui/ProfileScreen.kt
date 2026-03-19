package com.wealthvault.profile.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
//import androidx.compose.material.icons.Icons
//import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.wealthvault.profile.ui.components.ClosePersonItem
import com.wealthvault.core.generated.resources.Res
import com.wealthvault.core.generated.resources.ic_profile_setting
import org.jetbrains.compose.resources.painterResource


@Composable
fun ProfileScreen(
    // 🌟 เปลี่ยน parameter เป็น onSettingsClick แทน
    onSettingsClick: () -> Unit
) {
    val themeColor = Color(0xFFC27A5A)
    val bgColor = Color(0xFFFFF8F3)

    Column(
        modifier = Modifier
            .fillMaxSize()
            // ถ้ามีพื้นหลังสีครีม ใส่ .background(bgColor) ตรงนี้ได้ครับ
            .padding(horizontal = 24.dp)
            .padding(top = 24.dp)
    ) {

        // --- Header ---
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "โปรไฟล์",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Medium,
                color = themeColor
            )

            // 🌟 ไอคอนฟันเฟือง กดแล้วไปหน้า Settings
            Icon(
                painter = painterResource(Res.drawable.ic_profile_setting),
                contentDescription = "Settings",
                tint = Color(0xFFC47B5D),
                modifier = Modifier
                    .size(28.dp)
                    .clickable { onSettingsClick() } // กดแล้วเรียกคำสั่งเปลี่ยนหน้า
            )
        }

        Spacer(modifier = Modifier.height(32.dp))

        // --- Profile Info ---
        Row(verticalAlignment = Alignment.CenterVertically) {
            // รูปโปรไฟล์
            Box(
                modifier = Modifier
                    .size(90.dp)
                    .clip(CircleShape)
                    .background(Color(0xFFE0DCDA))
            )

            Spacer(modifier = Modifier.width(20.dp))

            Column {
                Text(
                    text = "Twentytwo01",
                    style = MaterialTheme.typography.titleMedium,
                    color = Color(0xFF3A2F2A)
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "nptwosudinw@gmail.com",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.Gray
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "13/08/2549",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color(0xFF3A2F2A)
                )
            }
        }

        Spacer(modifier = Modifier.height(32.dp))
        HorizontalDivider(color = themeColor.copy(alpha = 0.3f))
        Spacer(modifier = Modifier.height(24.dp))

        // --- Settings Summary ---
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "เปิดให้เห็นสินทรัพย์ทั้งหมดให้คนใกล้ชิดเมื่ออายุ",
                style = MaterialTheme.typography.labelMedium,
                color = Color(0xFF3A2F2A),
                modifier = Modifier.weight(1f)
            )
            Text(
                text = "80 ปี",
                style = MaterialTheme.typography.bodyMedium,
                color = Color.Gray
            )
        }

        Spacer(modifier = Modifier.height(32.dp))

        // --- Close People List ---
        Text(
            text = "คนใกล้ชิด",
            style = MaterialTheme.typography.bodyLarge,
            fontWeight = FontWeight.Medium,
            color = Color(0xFF3A2F2A)
        )
        Spacer(modifier = Modifier.height(16.dp))

        // สมมติว่ามี Component ClosePersonItem อยู่แล้ว
         ClosePersonItem(name = "Nai")
    }
}