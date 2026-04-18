package com.wealthvault.social.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.wealthvault.core.generated.resources.Res
import com.wealthvault.core.generated.resources.ic_nav_profile
import com.wealthvault.core.generated.resources.ic_nav_social
import com.wealthvault.core.theme.LightBg
import com.wealthvault.core.theme.LightPrimary
import com.wealthvault.group_api.model.GetAllGroupData
import org.jetbrains.compose.resources.painterResource

@Composable
fun GroupListItem(
    group: GetAllGroupData,
    onClick: () -> Unit
) {
    val displayName = group.groupName ?: "Unknown Group"

    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(vertical = 8.dp)
    ) {
        // 🌟 1. รูปโปรไฟล์กลุ่ม (ปรับเป็นสี่เหลี่ยมขอบมน)
        Box(
            modifier = Modifier
                .size(48.dp)
                .clip(CircleShape)
                .background(LightBg),
            contentAlignment = Alignment.Center
        ) {
            if (!group.groupProfile.isNullOrEmpty()) {
                AsyncImage(
                    model = group.groupProfile,
                    contentDescription = "Group Profile",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )
            } else {
                Icon(
                    painter = painterResource(Res.drawable.ic_nav_social),
                    contentDescription = "Default Group",
                    tint = LightPrimary, // ใช้ themeColor ให้ดูเนียนตา
                    modifier = Modifier.size(28.dp)
                )
            }
        }

        Spacer(modifier = Modifier.width(16.dp))

        // 🌟 2. ชื่อกลุ่ม (ใช้ Modifier.weight(1f) เพื่อดันส่วนที่เหลือไปชิดขวา)
        Text(
            text = displayName,
            style = MaterialTheme.typography.bodyMedium, // 🌟 ใช้ Typography ใหม่ที่ตั้งค่าไว้
            color = LightPrimary,
            modifier = Modifier.weight(1f)
        )

        // 🌟 3. ส่วนแสดงจำนวนสมาชิก (ไอคอน + ตัวเลข) จะถูกดันมาชิดขวาอัตโนมัติ
        if (group.memberCount != null) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    painter = painterResource(Res.drawable.ic_nav_profile),
                    contentDescription = "Members Count",
                    tint = LightPrimary,
                    modifier = Modifier.size(14.dp).padding(bottom = 2.dp)
                )

                Spacer(modifier = Modifier.width(4.dp))

                Text(
                    text = group.memberCount.toString(),
                    style = MaterialTheme.typography.bodyMedium, // 🌟 ใช้ Typography ใหม่
                    color = LightPrimary
                )
            }
        }
    }
}