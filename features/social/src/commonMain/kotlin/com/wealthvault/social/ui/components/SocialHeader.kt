package com.wealthvault.social.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

// 🌟 Import Res และไอคอนที่เราจำไว้
import org.jetbrains.compose.resources.painterResource
import com.wealthvault.core.generated.resources.Res
import com.wealthvault.core.generated.resources.ic_common_solid_down
import com.wealthvault.core.generated.resources.ic_social_plus
import com.wealthvault.core.generated.resources.ic_nav_profile
import com.wealthvault.core.generated.resources.ic_nav_social

// 🌟 Import สีจาก Theme ของเรา
import com.wealthvault.core.theme.LightPrimary

@Composable
fun SocialHeader(
    currentTabName: String, // ชื่อแท็บปัจจุบัน ("เพื่อน" หรือ "กลุ่ม")
    onTabSelected: (String) -> Unit, // เมื่อกดเลือกแท็บจาก Dropdown
    onAddFriendClick: () -> Unit,
    onAddGroupClick: () -> Unit,
    // 🌟 เรียกใช้สี LightPrimary จาก Theme เป็นค่า Default
    themeColor: Color = LightPrimary
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
                        style = MaterialTheme.typography.titleLarge, // 🌟 ใช้ Typography ของแอป
                        fontWeight = FontWeight.Medium,
                        color = themeColor
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Icon(
                        // 🌟 ไอคอนลูกศรชี้ลง
                        painter = painterResource(Res.drawable.ic_common_solid_down),
                        contentDescription = "Select Tab",
                        tint = themeColor,
                        modifier = Modifier.size(24.dp)
                    )
                }

                // เมนู Dropdown
                DropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false },
                    modifier = Modifier.background(Color.White),
                    shape = RoundedCornerShape(12.dp),
                ) {
                    DropdownMenuItem(
                        text = { Text("เพื่อน", color = themeColor) },
                        onClick = {
                            onTabSelected("เพื่อน")
                            expanded = false
                        }
                    )
                    DropdownMenuItem(
                        text = { Text("กลุ่ม", color = themeColor) },
                        onClick = {
                            onTabSelected("กลุ่ม")
                            expanded = false
                        }
                    )
                }
            }

            // --- ฝั่งขวา: ไอคอนเพิ่มเพื่อน และ เพิ่มกลุ่ม ---
            Row(verticalAlignment = Alignment.CenterVertically) {

                // 🌟 1. ปุ่มเพิ่มเพื่อน (ไอคอนคน + เครื่องหมายบวกมุมขวาบน)
                Box(
                    modifier = Modifier
                        .clickable { onAddFriendClick() }
                        .padding(4.dp),
                    contentAlignment = Alignment.TopEnd // 🌟 สั่งให้ของในกล่องนี้ ไปกองรวมกันที่มุมขวาบน
                ) {
                    Icon(
                        painter = painterResource(Res.drawable.ic_nav_profile),
                        contentDescription = "Add Friend",
                        tint = themeColor,
                        modifier = Modifier
                            .padding(end = 8.dp, top = 4.dp) // ดันไอคอนหลักลงและไปซ้ายนิดนึง เพื่อเว้นที่ให้เครื่องหมายบวก
                            .size(24.dp)
                    )
                    Icon(
                        painter = painterResource(Res.drawable.ic_social_plus),
                        contentDescription = null,
                        tint = themeColor,
                        modifier = Modifier
                            .padding(start = 8.dp, top = 8.dp)
                            .size(14.dp)

                    )
                }

                Spacer(modifier = Modifier.width(14.dp))

                // 🌟 2. ปุ่มเพิ่มกลุ่ม (ไอคอนกลุ่มเพื่อน + เครื่องหมายบวกมุมขวาบน)
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
                            .size(27.dp) // ไอคอนกลุ่มปรับให้ใหญ่กว่าไอคอนคนเดี่ยวนิดนึง
                    )
                    Icon(
                        painter = painterResource(Res.drawable.ic_social_plus),
                        contentDescription = null,
                        tint = themeColor,
                        modifier = Modifier
                            .padding(start = 8.dp)
                            .size(14.dp)
                    )
                }
            }
        }

        // เส้นคั่นด้านล่าง Header
        HorizontalDivider(color = themeColor.copy(alpha = 0.3f), thickness = 1.dp)
    }
}