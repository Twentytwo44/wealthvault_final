package com.wealthvault.social.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border // 🌟 Import เพิ่มเติม
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape // 🌟 Import เพิ่มเติม
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip // 🌟 Import เพิ่มเติม
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

import org.jetbrains.compose.resources.painterResource
import com.wealthvault.core.generated.resources.Res
import com.wealthvault.core.generated.resources.ic_common_solid_down
import com.wealthvault.core.generated.resources.ic_social_plus
import com.wealthvault.core.generated.resources.ic_nav_profile
import com.wealthvault.core.generated.resources.ic_nav_social
import com.wealthvault.core.theme.LightPrimary

@Composable
fun SocialHeader(
    currentTabName: String,
    onTabSelected: (String) -> Unit,
    onAddFriendClick: () -> Unit,
    onAddGroupClick: () -> Unit,
    themeColor: Color = LightPrimary,
    hasPendingRequest: Boolean = false // 🌟 1. รับสถานะจุดแดงเข้ามา (ค่าเริ่มต้นคือ false)
) {
    var expanded by remember { mutableStateOf(false) }

    Column(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // --- ฝั่งซ้าย: ชื่อแท็บ + Dropdown ---
            Box {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.clickable { expanded = true }
                ) {
                    Text(
                        text = currentTabName,
                        style = MaterialTheme.typography.titleLarge,
                        color = themeColor
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Icon(
                        painter = painterResource(Res.drawable.ic_common_solid_down),
                        contentDescription = "Select Tab",
                        tint = themeColor,
                        modifier = Modifier.size(20.dp)
                    )
                }

                DropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false },
                    modifier = Modifier.background(Color.White),
                    shape = RoundedCornerShape(12.dp),
                ) {
                    DropdownMenuItem(
                        text = { Text(text = "เพื่อน", color = themeColor, style = MaterialTheme.typography.bodyLarge) },
                        onClick = {
                            onTabSelected("เพื่อน")
                            expanded = false
                        }
                    )
                    DropdownMenuItem(
                        text = { Text(text = "กลุ่ม", color = themeColor, style = MaterialTheme.typography.bodyLarge) },
                        onClick = {
                            onTabSelected("กลุ่ม")
                            expanded = false
                        }
                    )
                }
            }

            // --- ฝั่งขวา: ไอคอนเพิ่มเพื่อน และ เพิ่มกลุ่ม ---
            Row(verticalAlignment = Alignment.CenterVertically) {

                // 🌟 2. ปุ่มเพิ่มเพื่อน (เพิ่มเงื่อนไขจุดแดง)
                Box(
                    modifier = Modifier
                        .clickable { onAddFriendClick() }
                        .padding(4.dp),
                    contentAlignment = Alignment.TopEnd
                ) {
                    Icon(
                        painter = painterResource(Res.drawable.ic_nav_profile),
                        contentDescription = "Add Friend",
                        tint = themeColor,
                        modifier = Modifier
                            .padding(end = 8.dp, top = 4.dp)
                            .size(20.dp)
                    )
                    Icon(
                        painter = painterResource(Res.drawable.ic_social_plus),
                        contentDescription = null,
                        tint = themeColor,
                        modifier = Modifier
                            .padding(start = 8.dp, top = 8.dp)
                            .size(10.dp)
                    )

                    // 🌟 วาดจุดแดงถ้ามีคำขอเป็นเพื่อน
                    if (hasPendingRequest) {
                        Box(
                            modifier = Modifier
                                .size(10.dp)
                                .offset(x = 2.dp, y = (-2).dp) // ขยับให้ลอยอยู่ขวาบนนิดๆ
                                .clip(CircleShape)
                                .background(Color(0xFFDC4A3C)) // สีแดงแจ้งเตือน
                                .border(1.5.dp, Color.White, CircleShape) // ขอบขาวให้ตัดกับไอคอน
                        )
                    }
                }

                Spacer(modifier = Modifier.width(14.dp))

                // ปุ่มเพิ่มกลุ่ม
                Box(
                    modifier = Modifier
                        .clickable { onAddGroupClick() }
                        .padding(4.dp),
                    contentAlignment = Alignment.TopEnd
                ) {
                    Icon(
                        painter = painterResource(Res.drawable.ic_nav_social),
                        contentDescription = "Add Group",
                        tint = themeColor,
                        modifier = Modifier
                            .padding(end = 8.dp, top = 4.dp)
                            .size(22.dp)
                    )
                    Icon(
                        painter = painterResource(Res.drawable.ic_social_plus),
                        contentDescription = null,
                        tint = themeColor,
                        modifier = Modifier
                            .padding(start = 8.dp)
                            .size(10.dp)
                    )
                }
            }
        }
        HorizontalDivider(color = themeColor.copy(alpha = 0.3f), thickness = 1.dp)
    }
}