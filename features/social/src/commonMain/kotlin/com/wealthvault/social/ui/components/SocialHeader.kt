package com.wealthvault.social.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
//import androidx.compose.material.icons.Icons
//import androidx.compose.material.icons.filled.ArrowDropDown
//import androidx.compose.material.icons.filled.GroupAdd
//import androidx.compose.material.icons.filled.PersonAdd
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun SocialHeader(
    currentTabName: String, // ชื่อแท็บปัจจุบัน ("เพื่อน" หรือ "กลุ่ม")
    onTabSelected: (String) -> Unit, // เมื่อกดเลือกแท็บจาก Dropdown
    onAddFriendClick: () -> Unit,
    onAddGroupClick: () -> Unit,
    themeColor: Color = Color(0xFFC27A5A)
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
            // ฝั่งซ้าย: ชื่อแท็บ + Dropdown
            Box {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.clickable { expanded = true }
                ) {
                    Text(
                        text = currentTabName,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Medium,
                        color = themeColor
                    )
//                    Icon(
//                        imageVector = Icons.Default.ArrowDropDown,
//                        contentDescription = "Select Tab",
//                        tint = themeColor
//                    )
                }

                // เมนู Dropdown
                DropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false }
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

            // ฝั่งขวา: ไอคอนเพิ่มเพื่อน และ เพิ่มกลุ่ม
            Row {
//                Icon(
//                    imageVector = Icons.Default.PersonAdd,
//                    contentDescription = "Add Friend",
//                    tint = themeColor,
//                    modifier = Modifier
//                        .size(28.dp)
//                        .clickable { onAddFriendClick() }
//                )
                Spacer(modifier = Modifier.width(16.dp))
//                Icon(
//                    imageVector = Icons.Default.GroupAdd,
//                    contentDescription = "Add Group",
//                    tint = themeColor,
//                    modifier = Modifier
//                        .size(28.dp)
//                        .clickable { onAddGroupClick() }
//                )
            }
        }

        // เส้นคั่นด้านล่าง Header
        HorizontalDivider(color = themeColor.copy(alpha = 0.3f), thickness = 1.dp)
    }
}