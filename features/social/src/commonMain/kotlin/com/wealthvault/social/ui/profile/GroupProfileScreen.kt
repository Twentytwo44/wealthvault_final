package com.wealthvault.social.ui.profile

import androidx.compose.foundation.layout.*
//import androidx.compose.material.icons.Icons
//import androidx.compose.material.icons.filled.Group
//import androidx.compose.material.icons.filled.PersonAdd
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
// Import component ที่เราเขียนไว้
import com.wealthvault.social.ui.components.space.SpaceTopBar
import com.wealthvault.social.ui.components.profile.ProfileHeader

@Composable
fun GroupProfileScreen(
    onBackClick: () -> Unit,
    onLeaveGroupClick: () -> Unit,
    onMembersClick: () -> Unit,
    onAddMemberClick: () -> Unit
) {
    val themeColor = Color(0xFFC27A5A)
    val bgColor = Color(0xFFFFF8F3)

    Scaffold(
        containerColor = bgColor,
        bottomBar = {
            // ปุ่มออกจากกลุ่มด้านล่างสุด
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 32.dp),
                contentAlignment = Alignment.Center
            ) {
                TextButton(onClick = onLeaveGroupClick) {
                    Text(text = "ออกจากกลุ่ม", color = Color(0xFFE74C3C), fontSize = 14.sp)
                }
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            SpaceTopBar(title = "โปรไฟล์กลุ่ม", onBackClick = onBackClick)
            HorizontalDivider(color = themeColor.copy(alpha = 0.2f))
            Spacer(modifier = Modifier.height(24.dp))

            // เนื่องจากไม่มีอีเมล จึงไม่ใส่ subtitle
            ProfileHeader(name = "Family", subtitle = "")

            Spacer(modifier = Modifier.height(32.dp))

            // เมนูไอคอน (สมาชิก / เพิ่ม)
            Row(
                modifier = Modifier.fillMaxWidth()
                    .padding(horizontal = 24.dp),
                horizontalArrangement = Arrangement.Center
            ) {
                GroupActionItem(
//                    icon = Icons.Default.Group,
                    label = "สมาชิก",
                    onClick = onMembersClick
                )
                Spacer(modifier = Modifier.width(64.dp)) // ระยะห่างระหว่าง 2 ปุ่ม
                GroupActionItem(
//                    icon = Icons.Default.PersonAdd,
                    label = "เพิ่ม",
                    onClick = onAddMemberClick
                )
            }
        }
    }
}

// ชิ้นส่วนไอคอนเมนูในหน้ากลุ่ม
@Composable
private fun GroupActionItem(
//    icon: ImageVector,
    label: String, onClick: () -> Unit) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        IconButton(onClick = onClick) {
//            Icon(
//                imageVector = icon,
//                contentDescription = label,
//                tint = Color(0xFF3A2F2A),
//                modifier = Modifier.size(28.dp)
//            )
        }
        Text(
            text = label,
            fontSize = 12.sp,
            color = Color.Gray
        )
    }
}