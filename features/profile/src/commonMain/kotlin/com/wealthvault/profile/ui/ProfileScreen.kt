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

@Composable
fun ProfileScreen(
    onEditProfileClick: () -> Unit,
    onLogoutClick: () -> Unit
) {
    val themeColor = Color(0xFFC27A5A)
    val bgColor = Color(0xFFFFF8F3)
    var expandedMenu by remember { mutableStateOf(false) }

    Scaffold(containerColor = bgColor) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 24.dp)
        ) {

            // --- Header & Settings Menu ---
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = "โปรไฟล์", fontSize = 24.sp, fontWeight = FontWeight.Medium, color = themeColor)

                Box {
//                    Icon(
//                        imageVector = Icons.Default.Settings,
//                        contentDescription = "Settings",
//                        tint = themeColor,
//                        modifier = Modifier
//                            .size(28.dp)
//                            .clickable { expandedMenu = true }
//                    )

                    // Popup Menu (แก้ไขข้อมูล / ออกจากระบบ)
                    DropdownMenu(
                        expanded = expandedMenu,
                        onDismissRequest = { expandedMenu = false },
                        offset = DpOffset(x = (-16).dp, y = 8.dp),
                        modifier = Modifier.background(Color.White, RoundedCornerShape(16.dp))
                    ) {
                        DropdownMenuItem(
                            text = { Text("แก้ไขข้อมูล", color = Color(0xFF3A2F2A)) },
                            onClick = {
                                expandedMenu = false
                                onEditProfileClick()
                            }
                        )
                        DropdownMenuItem(
                            text = { Text("ออกจากระบบ", color = Color(0xFFE74C3C)) },
                            onClick = {
                                expandedMenu = false
                                onLogoutClick()
                            }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            // --- Profile Info ---
            Row(verticalAlignment = Alignment.CenterVertically) {
                // รูปโปรไฟล์ (จำลองเป็นกล่องสีเทา)
                Box(
                    modifier = Modifier
                        .size(100.dp)
                        .clip(CircleShape)
                        .background(Color(0xFFE0DCDA))
                )

                Spacer(modifier = Modifier.width(24.dp))

                Column {
                    Text(text = "Twentytwo01", fontSize = 16.sp, color = Color(0xFF3A2F2A), fontWeight = FontWeight.Medium)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(text = "nptwosudinw@gmail.com", fontSize = 12.sp, color = Color.Gray)
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(text = "13/08/2549", fontSize = 14.sp, color = Color(0xFF3A2F2A))
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
                Text(text = "เปิดให้เห็นสินทรัพย์ทั้งหมดให้คนใกล้ชิดเมื่ออายุ", fontSize = 12.sp, color = Color(0xFF3A2F2A), modifier = Modifier.weight(1f))
                Text(text = "80 ปี", fontSize = 14.sp, color = Color.Gray)
            }

            Spacer(modifier = Modifier.height(24.dp))

            // --- Close People List ---
            Text(text = "คนใกล้ชิด", fontSize = 14.sp, fontWeight = FontWeight.Medium, color = Color(0xFF3A2F2A))
            Spacer(modifier = Modifier.height(16.dp))

            ClosePersonItem(name = "Nai", showDelete = false)
        }
    }
}