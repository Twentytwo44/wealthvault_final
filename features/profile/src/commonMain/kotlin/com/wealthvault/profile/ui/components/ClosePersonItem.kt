package com.wealthvault.profile.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha // 🌟 Import alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import com.wealthvault.core.generated.resources.Res
import com.wealthvault.core.generated.resources.ic_nav_profile
import com.wealthvault.core.theme.LightBg
import com.wealthvault.core.theme.LightBorder
import com.wealthvault.core.theme.LightPrimary
import com.wealthvault.core.theme.LightSoftWhite
import com.wealthvault.`user-api`.model.CloseFriendData
import org.jetbrains.compose.resources.painterResource

@Composable
fun ClosePersonItem(
    friend: CloseFriendData,
    showDelete: Boolean = false,
    isEnabled: Boolean = true, // 🌟 1. รับค่า isEnabled เข้ามา (ตั้งค่าเริ่มต้นเป็น true)
    onDeleteClick: () -> Unit = {}
) {

    val bgColor = if (isEnabled) LightSoftWhite else Color(0xFFF0F0F0)

    Surface(
        color = bgColor,
        shape = RoundedCornerShape(16.dp),
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 12.dp)
            // 🌟 3. พระเอกของเรา! ถ้า isEnabled เป็น false ให้ภาพเบลอๆ จางๆ (50%)
            .alpha(if (isEnabled) 1f else 0.6f),
        shadowElevation = if (showDelete || isEnabled) 1.dp else 0.dp, // ซ่อนเงาถ้า Disable
        border = BorderStroke(1.dp, LightBorder)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // ส่วนรูปโปรไฟล์
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(LightBg),
                contentAlignment = Alignment.Center
            ) {
                if (friend.profile.isNotEmpty()) {
                    AsyncImage(
                        model = friend.profile,
                        contentDescription = "Profile Picture",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                } else {
                    Icon(
                        painter = painterResource(Res.drawable.ic_nav_profile),
                        contentDescription = "Default Profile",
                        tint = LightPrimary,
                        modifier = Modifier.size(24.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.width(16.dp))

            // ส่วนชื่อและอีเมล
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = friend.username.ifEmpty { "ไม่ระบุชื่อ" },
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.SemiBold,
                    color = Color(0xFF3A2F2A)
                )
                if (friend.email.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = friend.email,
                        style = MaterialTheme.typography.bodySmall,
                        color = Color(0xFF9E918B)
                    )
                }
            }

            // ปุ่มลบ
            if (showDelete) {
                Text(
                    text = "ลบ",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color(0xFFE57373),
                    // 🌟 4. ปิดการคลิกถ้าอยู่ในโหมด Disable (ถ้าต้องการ)
                    modifier = Modifier
                        .clickable(enabled = isEnabled) { onDeleteClick() }
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                )
            }
        }
    }
}