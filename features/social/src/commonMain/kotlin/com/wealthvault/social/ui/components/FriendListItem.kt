package com.wealthvault.social.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import com.wealthvault.core.generated.resources.Res
import com.wealthvault.core.generated.resources.ic_nav_profile
import com.wealthvault.core.generated.resources.ic_social_crown
import com.wealthvault.core.theme.LightBg
import com.wealthvault.core.theme.LightPrimary
import com.wealthvault.`user-api`.model.FriendData
import com.wealthvault.group_api.model.GroupMemberItem // 🌟 1. Import ตัวนี้เพิ่มเข้ามา
import org.jetbrains.compose.resources.painterResource

// =======================================================
// 🌟 1. สำหรับใช้ใน "หน้าเพื่อน" (รับ FriendData ตามเดิม)
// =======================================================
@Composable
fun FriendListItem(
    friend: FriendData,
    onClick: () -> Unit
) {
    val displayName = friend.username ?: friend.firstName ?: "Unknown"
    SharedUserListItem(displayName = displayName, profileUrl = friend.profile, onClick = onClick)
}

// =======================================================
// 🌟 2. สำหรับใช้ใน "หน้ากลุ่ม" (รับ GroupMemberItem เพิ่มเข้ามาใหม่)
// =======================================================
@Composable
fun FriendListItem(
    member: GroupMemberItem,
    isLeader: Boolean = false,
    onClick: () -> Unit
) {
    val displayName = member.username ?: member.firstName ?: "Unknown"
    SharedUserListItem(
        displayName = displayName,
        profileUrl = member.profile,
        isLeader = isLeader, // 🌟 ส่งต่อให้ตัววาด UI
        onClick = onClick
    )
}

// =======================================================
// 🌟 3. แกนกลาง UI (ใช้ร่วมกัน จะได้ไม่ต้องเขียนโค้ดวาด UI ซ้ำสองรอบ)
// =======================================================
@Composable
private fun SharedUserListItem(
    displayName: String,
    profileUrl: String?,
    isLeader: Boolean = false,
    onClick: () -> Unit
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(vertical = 8.dp) // 💡 เติม horizontal 24.dp ให้ขอบซ้ายขวาไม่ชิดจอเกินไปครับ
    ) {
        Box(
            modifier = Modifier
                .size(48.dp)
                .clip(CircleShape)
                .background(LightBg),
            contentAlignment = Alignment.Center
        ) {
            if (!profileUrl.isNullOrEmpty()) {
                AsyncImage(
                    model = profileUrl,
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

        Text(text = displayName, fontSize = 16.sp, color = LightPrimary)

        if (isLeader) {
            Spacer(modifier = Modifier.width(8.dp))
            Icon(
                painter = painterResource(Res.drawable.ic_social_crown), // 💡 ถ้าโหลดไฟล์ xml มงกุฎมา ให้เปลี่ยนเป็น painterResource(Res.drawable.ic_crown) นะครับ
                contentDescription = "Group Leader",
                tint = Color(0xFFF3A227), // สีเหลืองทอง สวยๆ
                modifier = Modifier.size(20.dp)
            )
        }
    }
}