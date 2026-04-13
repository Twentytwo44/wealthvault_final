package com.wealthvault.profile.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.wealthvault.core.generated.resources.Res
import com.wealthvault.core.generated.resources.ic_form_check
import com.wealthvault.core.generated.resources.ic_nav_profile
import com.wealthvault.core.theme.LightBg
import com.wealthvault.core.theme.LightBorder
import com.wealthvault.core.theme.LightPrimary
import com.wealthvault.core.theme.LightSoftWhite
import com.wealthvault.`user-api`.model.FriendData
import org.jetbrains.compose.resources.painterResource

@Composable
fun SelectPersonItem(
    friend: FriendData,
    isSelected: Boolean,
    onSelectedChange: (Boolean) -> Unit
) {
    Surface(
        color = LightSoftWhite,
        shape = RoundedCornerShape(16.dp),
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 12.dp)
            // 🌟 ทำให้จิ้มตรงไหนของกล่องก็ได้เพื่อเปลี่ยนค่า Checkbox
            .clickable { onSelectedChange(!isSelected) },
        border = BorderStroke(1.dp, LightBorder)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // --- 1. รูปโปรไฟล์ ---
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(LightBg),
                contentAlignment = Alignment.Center
            ) {
                if (friend.profile.toString().isNotEmpty()) {
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

            // --- 2. ข้อมูลชื่อและอีเมล ---
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = friend.username.toString().ifEmpty { "ไม่ระบุชื่อ" },
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.SemiBold,
                    color = Color(0xFF3A2F2A)
                )
                if (friend.email.toString().isNotEmpty()) {
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = friend.email ?: "",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color(0xFF9E918B)
                    )
                }
            }

            // --- 3. Checkbox ---
            Box(
                modifier = Modifier
                    .size(24.dp)
                    .clip(RoundedCornerShape(8.dp)) // 🌟 ปรับความมนตรงนี้ได้เลย (เช่น 8.dp หรือ 12.dp)
                    .background(if (isSelected) LightPrimary else Color.Transparent)
                    .border(
                        width = 2.dp,
                        color = if (isSelected) LightPrimary else Color.LightGray,
                        shape = RoundedCornerShape(8.dp) // 🌟 ต้องใส่ Shape ให้เท่ากับ clip
                    )
                    .clickable { onSelectedChange(!isSelected) },
                contentAlignment = Alignment.Center
            ) {
                if (isSelected) {
                    Icon(
                        painter = painterResource(Res.drawable.ic_form_check),
                        contentDescription = null,
                        tint = LightSoftWhite,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }
    }
}
